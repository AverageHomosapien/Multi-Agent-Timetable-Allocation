package timetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import timetable_ontology.TimetableOntology;
import timetable_ontology.elements.HappyWith;
import timetable_ontology.elements.MessageBoard;
import timetable_ontology.elements.PleasedWith;
import timetable_ontology.elements.SlotsAvailable;
import timetable_ontology.elements.SlotsRequested;
import timetable_ontology.elements.SwapFinal;
import timetable_ontology.elements.SwapInitial;
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
	
	// Tracks Student Slots
	private ArrayList<AID>[][] StudentSlots;
	
	// Maps 
	private HashMap<String, Integer> TutorialMap = new HashMap<>();
	
	// Keep the 'happy students' since you could run over them in theory to check if there's a few unhappy agents left..
	// 		..may find an even better slot
	private List<AID> happyAgents = new ArrayList<AID>(); // StudentAgents 'happy' with their tutorial assignment
	private List<AID> unhappyAgents = new ArrayList<AID>(); // StudentAgents 'unhappy' with their tutorial assignment
	
	@SuppressWarnings("unchecked")
	@Override
	protected void setup() {
		register();
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		Object[] arguments = getArguments();
		List<String> studentString = (List<String>) arguments[0];
		List<TutorialGroup[]> tutorialList = (List<TutorialGroup[]>) arguments[1];
		
		doWait(5000); // Wait for student agents to load
		addBehaviour(new FindStudentsBehaviour(this)); // Finds the students
		addBehaviour(new SetupTTBehaviour(studentString, tutorialList));
		addBehaviour(new SwapStudentSlotBehaviour()); // Runs the Student Swapping Behaviour
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
				currentAgent = unhappyAgents.get(studentIndex);
				addBehaviour(new InitiateStudentBehaviour(currentAgent)); // Notifies the swapperAgent they're 'live'
				step++;
				break;
			case 1:
				MessageTemplate swapConfirmMt = MessageTemplate.and(MessageTemplate.MatchConversationId("messageBoard")
							,MessageTemplate.MatchSender(currentAgent)); // Listens for request from 'live' agent
				ACLMessage msg = myAgent.receive(swapConfirmMt); 
				if (msg != null) {
					try {
						ContentElement ce = getContentManager().extractContent(msg);
						if (ce instanceof SlotsRequested) {
							slotsRequested = ((SlotsRequested)ce);
							step++;
							
							// Checking the student has the slot it says it does
							Iterator<SwapFinal> it = slotsRequested.getSlots().iterator();
							while (it.hasNext()) {
								SwapFinal itSwap = it.next();
								boolean present = false;
								// Looping through requester agent to check if agent has those tutorial slots
								for (AID agent : StudentSlots
										[TutorialMap.get(itSwap.getTutorialTo().getTutorialID())][itSwap.getTutorialTo().getTutNum()]) {
									if (itSwap.getAgentTo().equals(agent)) {
										present = true;
										break;
									}
								}
								if (!present) { // If student doesn't own the slot
									it.remove();
								}
								else if (!itSwap.getTutorialTo().getTutorialID().equals
										(itSwap.getInitalSwapRequest().getTutorial().getTutorialID())) { // Refuse to accept if different module
									it.remove();
								}
							}
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
				slotsConfirmed.setSlots(new ArrayList<>()); // Setting slotsConfirmed slots
				slotsDenied.setSlots(new ArrayList<>());
				
				// This allows the Timetable Agent to only ask for slots if slots have been selected
				// (Reduced message passing)
				if (slotsRequested.getSlots().size() == 0) { // Go to step requesting slots if no slots selected
					step=4;
				}
				else {
					addBehaviour(new VerifySlotBehaviour(slotsRequested)); // Verifies slot with swappedAgent
					step++;
				}
				break;
			case 3:
				MessageTemplate swapRequestFinalMt = MessageTemplate.MatchConversationId("swapRequestCheck"); // Listens for response from swappedAgent
				msg = myAgent.receive(swapRequestFinalMt); 
				
				if (msg != null) { // Check for swapped slots and un-swapped slots
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
							
							// Updating the slots for the Requester Agent and the Poster Agent
							for (SwapFinal swap : slotsConfirmed.getSlots()) {
								// Update requester agent to the new slot
								Iterator<AID> it = StudentSlots[TutorialMap.get(swap.getTutorialTo().getTutorialID())]
																				[swap.getTutorialTo().getTutNum()].iterator();
								while (it.hasNext()) {
									AID aidRem = it.next();
									if (aidRem.equals(swap.getAgentTo())) {
										it.remove();
										break;
									}
								}
								StudentSlots[TutorialMap.get(swap.getTutorialTo().getTutorialID())]
										[swap.getTutorialTo().getTutNum()].add(swap.getInitalSwapRequest().getAgentFrom());
								
								// Update the initial agent on the timetable to the agreed slot
								Iterator<AID> receiverit = StudentSlots[TutorialMap.get(swap.getInitalSwapRequest().getTutorial().getTutorialID())]
																					[swap.getInitalSwapRequest().getTutorial().getTutNum()].iterator();
								while (receiverit.hasNext()) {
									AID aidRem = receiverit.next();
									if (aidRem.equals(swap.getInitalSwapRequest().getAgentFrom())) {
										receiverit.remove();
										break;
									}
								}
								StudentSlots[TutorialMap.get(swap.getInitalSwapRequest().getTutorial().getTutorialID())]
											[swap.getInitalSwapRequest().getTutorial().getTutNum()].add(swap.getAgentTo());
							}
							
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
				addBehaviour(new InitiateStudentHappyBehaviour(slotsConfirmed, currentAgent)); // Initiates response verification with swapperAgent
				step++;
				break;
			case 5:
				boolean happy = false;
				boolean done = false;
				MessageTemplate SlotRequestMt = MessageTemplate.and
						(MessageTemplate.MatchConversationId("happyNow"),MessageTemplate.MatchSender(currentAgent));
				msg = myAgent.receive(SlotRequestMt); 
				if (msg != null) {
					try {
						ContentElement ce = getContentManager().extractContent(msg);
						if (ce instanceof Action) {
							Concept action = ((Action)ce).getAction();
							
							if (action instanceof UnhappySlot) {
								UnhappySlot slot = (UnhappySlot) action;
								SwapInitial initSwap = slot.getSlotToSwap();
								if (initSwap != null) {
									boolean present = false;
									
									// Checking if duplicate slot
									for (SwapInitial checkSlot : messageBoard.getMessageBoard()) {
										if (checkSlot.getTutorial().getTutorialID().equals
												(initSwap.getTutorial().getTutorialID()) && checkSlot.getAgentFrom().equals(initSwap.getAgentFrom())) {
											present = true;
										}
									}
									
									// Checking if timetabler has student as owning slot
									boolean found = false;
									for (AID student : StudentSlots
											[TutorialMap.get(initSwap.getTutorial().getTutorialID())][initSwap.getTutorial().getTutNum()]) {
										if (student.equals(initSwap.getAgentFrom())) {
											found = true;
											break;
										}
									}
									// If not found, student not present
									if (!found) {
										present = false;
									}
									if (!present) {
										messageBoard.addToMessageBoard(initSwap);
									}
									
									
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
				
				// If happy, add to happier list increment student index
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
						
						happyAgents.add(currentAgent);
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
			System.out.println("");
			System.out.println("The program has finished");

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
				action.setActor(getAID()); // set itself as the actor - since it's requesting swap
				
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
				
				// Make sure slots are removed from the message board if they've been swapped to another agent
				for (SwapFinal slot : slotsConfirmed.getSlots()) {
					happy.addSlots(slot);
					
					for (int i = 0; i < messageBoard.getMessageBoard().size(); i++) {
						if (slot.getInitalSwapRequest().getAgentFrom().equals(messageBoard.getMessageBoard().get(i).getAgentFrom()) && 
							slot.getInitalSwapRequest().getTutorial().getTutorialID().equals(messageBoard.getMessageBoard().get(i).getTutorial().getTutorialID())) {
								messageBoard.getMessageBoard().remove(i);
								break;
						}
					}
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
	
	
	// Sets up the timetable agent with a list of all the modules
	public class SetupTTBehaviour extends OneShotBehaviour {
		List<String> students;
		List<TutorialGroup[]> tutorials;
		
		
		public SetupTTBehaviour(List<String> students, List<TutorialGroup[]> tutorials) {
			this.students = students;
			this.tutorials = tutorials;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void action() {
			
			// Gets the maximum number of tutorials and tutorial groups
			int maxTuts = 0;
			List<AID> tempAIDs = new ArrayList<>();
			List<String> tempTuts = new ArrayList<>();
			
			// Getting Student AIDs in order
			for (String student : students) {
				for (AID agent : unhappyAgents) {
					if (agent.getName().equals(student)) {
						tempAIDs.add(agent);
						break;
					}
				}
			}
			
			// Getting the Unique Tutorials in a sorted ArrayList
			for (TutorialGroup[] group : tutorials) {
				for (TutorialGroup tutorial : group) {
					if (tutorial.getTutNum() > maxTuts) {
						maxTuts = tutorial.getTutNum();
					}
					if (!tempTuts.contains(tutorial.getTutorialID())) {
						tempTuts.add(tutorial.getTutorialID());
					}
				}
			}
			maxTuts++; // Since the tutorial numbers are 0 upwards
			Collections.sort(tempTuts);
			
			// Adding tutorials to the Tutorial HashMap
			for (int i = 0; i < tempTuts.size(); i++) {
				TutorialMap.put(tempTuts.get(i), i);
			}
			
			StudentSlots = new ArrayList[TutorialMap.size()][maxTuts]; // Initialising the student slots
			for (int i = 0; i < TutorialMap.size(); i++) {
				for (int j = 0; j < maxTuts; j++) {
					StudentSlots[i][j] = new ArrayList<AID>(); // Initialise the ArrayList correctly for each element
				}
			}
			
			int loopVal = 0;
			// Adding students to the correct tutorial group
			for (int i = 0; i < tempAIDs.size(); i++){
				for (int j = 0; j < TutorialMap.size(); j++) { // SET ID HashMap (e.g. SET010101, SET010102)
					// Adds Students to the 2d array ArrayList based on tutorial and class number (allows tracking of students in tutorials)
					StudentSlots[TutorialMap.get(tutorials.get(loopVal)[j].getTutorialID())][tutorials.get(loopVal)[j].getTutNum()].add(tempAIDs.get(i));
					}
				// LoopVal tracks the correct arguments (e.g. 9 students created with 3 arguments)
				if (loopVal >= tutorials.size()-1) {
					loopVal = 0;
				}else {
					loopVal++;
				}
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
		}
		catch(FIPAException e) {
			e.printStackTrace() ;
		}
	}
}
