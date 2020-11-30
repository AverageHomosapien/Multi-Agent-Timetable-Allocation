package timetable;

import java.util.ArrayList;
import java.util.List;

import timetable_ontology.TimetableOntology;
import timetable_ontology.elements.HappyWith;
import timetable_ontology.elements.MessageBoard;
import timetable_ontology.elements.PleasedWith;
import timetable_ontology.elements.SlotsAvailable;
import timetable_ontology.elements.SlotsRequested;
import timetable_ontology.elements.SwapFinal;
import timetable_ontology.elements.SwapInitial;
import timetable_ontology.elements.Timeslot;
import timetable_ontology.elements.TutorialGroup;
import timetable_ontology.elements.UnhappySlot;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

// Timetabling agent
public class TimetablingAgent extends Agent{
	private Codec codec = new SLCodec();
	private Ontology ontology = TimetableOntology.getInstance();

	private MessageBoard messageBoard = new MessageBoard(); // WHEN MESSAGEBOARD PASSED - NEED TO TAKE ALL ELEMENTS OF THIS BOARD AND ADD THEM TO A NEW BOARD
	
	// Keep the 'happy students' since you could run over them in theory to check if there's a few unhappy agents left..
	// 		..may find an even better slot
	private List<AID> happyAgents = new ArrayList<AID>(); // StudentAgents 'happy' with their tutorial assignment
	private List<AID> unhappyAgents = new ArrayList<AID>(); // StudentAgents 'unhappy' with their tutorial assignment
	
	@Override
	protected void setup() {
		register();
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		doWait(5000); // Wait for student agents to load -- Traditionally wait 30000
		addBehaviour(new FindStudentsBehaviour(this)); // Finds the students
		addBehaviour(new SwapStudentSlotBehaviour()); // Runs the Student Swapping Behaviour
	}
	
	// Finds the Student Agents and adds them to the UnhappyAgents list
	public class FindStudentsBehaviour extends OneShotBehaviour {
		public FindStudentsBehaviour(Agent a) {
			super(a);
		}
		@Override
		public void action() {
			
			DFAgentDescription studentTemplate = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("student-agent");
			studentTemplate.addServices(sd);
			
			try{
				unhappyAgents.clear();
				DFAgentDescription[] agentsType1  = DFService.search(myAgent,studentTemplate); 
				for(int i=0; i<agentsType1.length; i++){
					unhappyAgents.add(agentsType1[i].getName()); // this is the AID
				}
			}
			catch(FIPAException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Runs the Student Swapping Behaviour for the whole agent
	public class SwapStudentSlotBehaviour extends Behaviour {
		private AID currentAgent;
		private int studentIndex = 0;
		private int step = 0;
		private SlotsRequested slotsRequested = new SlotsRequested(); // Slots requested by StudentAgents
		private SlotsRequested slotsConfirmed = new SlotsRequested(); // Slots confirmed by TimetablingAgent
		private SlotsRequested slotsDenied = new SlotsRequested(); // Slots denied by StudentAgents
		
		@Override
		public void action() {
			switch(step) {
			case 0:
				System.out.println("Step 0 - notify of live");
				currentAgent = unhappyAgents.get(studentIndex);
				addBehaviour(new InitiateStudentBehaviour(currentAgent)); // Notifies the swapperAgent they're 'live'
				step++;
				break;
			case 1:
				System.out.println("Step 1 - sending messageboard");
				MessageTemplate swapConfirmMt = MessageTemplate.and(MessageTemplate.MatchConversationId("messageBoard"),MessageTemplate.MatchSender(currentAgent)); // Listens for request from 'live' agent
				ACLMessage msg = myAgent.receive(swapConfirmMt); 
				if (msg != null) {
					try {
						ContentElement ce = getContentManager().extractContent(msg);
						if (ce instanceof SlotsRequested) {
							slotsRequested = ((SlotsRequested)ce);
							step++;
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				else {
					block();
				}
				break;
			case 2:
				System.out.println("Step 2 - Confirming selected slots with studentAgents");
				slotsConfirmed.setSlots(new ArrayList<>()); // Setting slotsConfirmed slots
				slotsDenied.setSlots(new ArrayList<>());
				
				if (slotsRequested.getSlots().size() == 0) { // Go to next step if slots don't need confirmed
					System.out.println("No slots. Proceed to confirming if agent is happy/slots to swap");
					step=4;
				}
				else {
					addBehaviour(new VerifySlotBehaviour(slotsRequested)); // Verifies slot with swappedAgent
					step++;
				}
				break;
			case 3:
				System.out.println("Step 3 - Listening for swappedAgent");
				MessageTemplate swapRequestFinalMt = MessageTemplate.MatchConversationId("swapRequestCheck"); // Listens for response from swappedAgent
				msg = myAgent.receive(swapRequestFinalMt); 
				if (msg != null) { // CHECK FOR TOTAL REPLIES - POSITIVE AND NEGATIVE FOR THIS
					try {
						ContentElement ce = getContentManager().extractContent(msg);
						if (ce instanceof Action) {
							Concept action = ((Action)ce).getAction();
							if (ce instanceof Action) {
								SwapFinal swapRequest = (SwapFinal) action;
								if (msg.getPerformative() == ACLMessage.AGREE) {
									slotsConfirmed.addSlot(swapRequest);
								}else {
									slotsDenied.addSlot(swapRequest);
								}
							}
						}
						if ((slotsConfirmed.getSlots().size() + slotsDenied.getSlots().size()) == slotsRequested.getSlots().size()) { // Increase step if received all replies
							step++;
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				else {
					block();
				}
				break;
			case 4:
				System.out.println("Step 4 - Confirming slots with requester agent & if happy");
				addBehaviour(new InitiateStudentHappyBehaviour(slotsConfirmed, currentAgent)); // Initiates response verification with swapperAgent
				step++;
				break;
			case 5:
				System.out.println("Step 5 - Getting happy or slots requested");
				boolean happy = false;
				boolean done = false;
				MessageTemplate SlotRequestMt = MessageTemplate.and(MessageTemplate.MatchConversationId("happyNow"),MessageTemplate.MatchSender(currentAgent));
				msg = myAgent.receive(SlotRequestMt); 
				if (msg != null) {
					try {
						ContentElement ce = getContentManager().extractContent(msg);
						if (ce instanceof Action) {
							Concept action = ((Action)ce).getAction();
							
							if (action instanceof UnhappySlot) {
								UnhappySlot slot = (UnhappySlot) action;
								SwapInitial initSwap = slot.getSlotToSwap();
								boolean present = false;
								
								// Checking if duplicate slot
								for (SwapInitial swapCheck : messageBoard.getMessageBoard()) {
									if (swapCheck.getTutorial().getTutorialID().equals(initSwap.getTutorial().getTutorialID()) &&
											swapCheck.getAgentFrom().equals(initSwap.getAgentFrom())) {
										present = true;
									}
								}
								if (!present) {
									messageBoard.addToMessageBoard(initSwap);
								}
								done = true;
								step = 0;
							}
						}
						else if (ce instanceof PleasedWith) { // Agent is pleased with slot
							happy = true;
							done = true;
							step = 0;
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				else {
					block();
				}
				
				// If happy, add to happier list increment student index -- DONT WANT TO CHANGE INDEX UNTIL FINISHED WORKING WITH ACTIVELIST??
				if (done) {
					if (happy) {
						
						// Remove agent's slots from the timetable if they're happy
						List<Integer> slotLocs = new ArrayList<>();
						for (int i = 0; i < messageBoard.getMessageBoard().size(); i++) {
							if (messageBoard.getMessageBoard().get(i).getAgentFrom().equals(currentAgent)) {
								slotLocs.add(i);
							}
						}
						
						// Remove slot locations from the message board
						// and then negatively increment all slot locations
						for (int i = 0; i < slotLocs.size(); i++) {
							messageBoard.removeFromMessageBoard(slotLocs.get(i));
							for (int j = i+1; j < slotLocs.size(); j++) {
								slotLocs.set(j, slotLocs.get(j)-1); 
							}
						}
						
						System.out.println("Agent " + getAID().getLocalName() + " is happy");
						happyAgents.add(currentAgent); // Don't use lists and check at end - doesn't make sense anymore (unless can queue unhappy agents for switch at end of loop)
						unhappyAgents.remove(currentAgent); // Remove agent
					}
					
					if (studentIndex >= (unhappyAgents.size()-1)) { // -1 because used for indexing
						studentIndex = 0;
					}else {
						studentIndex++;
					}
					
				}
				break;
			}
		}

		@Override
		public boolean done() {
			if (unhappyAgents.size() > 0) {
				return false;
			}
			System.out.println("Program has finished");
			doDelete();
			return true;
		}
		
		@Override
		public void reset() {
			System.out.println("Resetting program");
			super.reset();
			step = 0;
		}
	}
	
	
	// Notifies the swapperAgent they're 'live'
	// Receives a done message if students are finished their activity
	public class InitiateStudentBehaviour extends OneShotBehaviour {
		private AID studentAid;
		
		// Called at the moment the behaviour is added
		public InitiateStudentBehaviour (AID studentAgent) {
			this.studentAid = studentAgent;
		}
		
		@Override
		public void action() {
			List<SwapInitial> siList = new ArrayList<SwapInitial>();
			
			for (SwapInitial m : messageBoard.getMessageBoard()) {
				siList.add(m);
			}
			MessageBoard mb = new MessageBoard();
			mb.setMessageBoard(siList);
			
			SlotsAvailable available = new SlotsAvailable();
			available.setBoard(mb);

			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(studentAid);
			msg.setConversationId("messageBoard");
			msg.setLanguage(codec.getName());
			msg.setOntology(ontology.getName());
						
			try {
				getContentManager().fillContent(msg, available);
				send(msg);
			}
			catch (CodecException ce) {
				ce.printStackTrace();
			}
			catch (OntologyException oe) {
				oe.printStackTrace();
			} 
		}
	}
	
	
	// Confirms the validity of the slots requested with other agents
	public class VerifySlotBehaviour extends OneShotBehaviour {
		SlotsRequested slotsRequested;
		
		public VerifySlotBehaviour(SlotsRequested slotsRequested) {
			this.slotsRequested = slotsRequested;
		}
		
		@Override
		public void action() {
			for (SwapFinal swap : slotsRequested.getSlots()) {
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setLanguage(codec.getName());
				msg.setOntology(ontology.getName());
				msg.addReceiver(swap.getInitalSwapRequest().getAgentFrom());
				msg.setConversationId("swapRequestCheck");
				
				Action action = new Action();
				action.setAction(swap);
				action.setActor(getAID()); // set itself as the actor - since it's requesting final swap?
				
				try {
					getContentManager().fillContent(msg, action);
					send(msg);
				}
				catch(CodecException codecE) {
					codecE.printStackTrace();
				}
				catch(OntologyException oe) {
					oe.printStackTrace();
				}
			}
			
		}
	}

	
	// Initiates response verification with the swapperAgent
	public class InitiateStudentHappyBehaviour extends OneShotBehaviour {
		SlotsRequested slotsConfirmed;
		AID receiver;

		public InitiateStudentHappyBehaviour(SlotsRequested slotsConfirmed, AID agentTo) {
			this.slotsConfirmed = slotsConfirmed;
			this.receiver = agentTo;
		}
		
		@Override
		public void action() {
			try {
				HappyWith happy = new HappyWith();
				happy.setSlots(new ArrayList<>());
				
				for (SwapFinal slot : slotsConfirmed.getSlots()) {
					happy.addSlots(slot);
				}
				
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setLanguage(codec.getName());
				msg.setOntology(ontology.getName());
				msg.addReceiver(receiver);
				msg.setConversationId("happyNow");
				
				Action action = new Action();
				action.setAction(happy);
				action.setActor(getAID());
				
				getContentManager().fillContent(msg, action);
				send(msg);
			}
			catch(CodecException codecE) {
				codecE.printStackTrace();
			}
			catch(OntologyException oe) {
				oe.printStackTrace();
			}
		}
	}
	
	
	// Register with the DFD
	protected void register() {
		DFAgentDescription dfd = new DFAgentDescription ();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("timetabling-agent");
		sd.setName( getLocalName() + "-timetabling-agent");
		dfd.addServices(sd);
		
		try{
			DFService.register(this , dfd);
			System.out.println("Timetabling agent registered");
		}
		catch(FIPAException e) {
			e.printStackTrace() ;
		}
	}
	
	/*
	// Gets the first test case arguments
	private void getArgs1(){
		Object[][] args = (Object[][]) getArguments();
		TutorialGroup _tut = new TutorialGroup();
		_tut.setTutorial((String) args[0][0]);
		_tut.setClasses((Integer) args[0][1]);
		Tutorials.add(_tut);
	}
	*/
}