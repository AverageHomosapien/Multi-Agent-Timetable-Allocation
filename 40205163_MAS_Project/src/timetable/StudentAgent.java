package timetable;

import java.util.ArrayList;
import java.util.List;

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
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
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

// Student Agent
public class StudentAgent extends Agent{
	private Codec codec = new SLCodec();
	private Ontology ontology = TimetableOntology.getInstance();
	private AID timetableAgent;
	MessageBoard slotsMessageBoard;
	
	private int[][] SlotPreferences;
	private TutorialGroup[] CurrentTutorials;
	private int messagesSent = 0;

	
	@Override
	protected void setup() {
		register();
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		Object[] arguments = getArguments();
		SlotPreferences = (int[][]) arguments[0];
		CurrentTutorials = (TutorialGroup[]) arguments[1];
		
		addBehaviour(new FindTimetablerBehaviour());
		addBehaviour(new AwaitTimetableBehaviour()); // Slots requested by timetable agent
		addBehaviour(new AwaitSlotVerifyBehaviour()); // Checks the slot request from timetable agent
		addBehaviour(new AwaitHappyBehaviour()); // Takes in the slots confirmed by the timetable agent and confirms if happy/slot to give
	}
	
	
	// Awaiting call of the timetable agent to begin
	public class AwaitTimetableBehaviour extends CyclicBehaviour {

		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(MessageTemplate.MatchConversationId("messageBoard"));
			if (msg != null) {
				try {
					System.out.println(getAID().getLocalName() + " - Initiating timetable check"); //print out the message content in SL
					ContentElement ce = getContentManager().extractContent(msg);
					
					if (ce instanceof SlotsAvailable) {
						SlotsAvailable available = (SlotsAvailable) ce;
						slotsMessageBoard = available.getBoard();
						List<SwapInitial> messageBoard = slotsMessageBoard.getMessageBoard();
						
						// Checking if messages in the messageboard -- otherwise just send unwanted slots
						if (messageBoard.size() > 0) {
							// Array to hold 'best' slots we're interested in for each tutorial group
							SwapInitial[] interestedSlots = new SwapInitial[CurrentTutorials.length];
							
							// Fill the 'interested slots'
							for (int i = 0; i < interestedSlots.length; i++) {
								for (SwapInitial boardSwap : messageBoard) {
									if (boardSwap.getTutorial().getTutorialID().equals(CurrentTutorials[i].getTutorialID())) { // If matching tutorial class
										if (interestedSlots[i] == null) {
											if (SlotFitness(boardSwap.getTutorial().getTimeslot()) > SlotFitness(CurrentTutorials[i].getTimeslot())) { // If no current 'better slot'
												interestedSlots[i] = boardSwap;
											}
										}else if (SlotFitness(boardSwap.getTutorial().getTimeslot()) > SlotFitness(interestedSlots[i].getTutorial().getTimeslot())){ // Better than 'best' slot
											interestedSlots[i] = boardSwap;
										}
									}
								}
							}
							
							
							SlotsRequested wishedSlots = new SlotsRequested();
							SwapFinal swapRequest;
							ArrayList<SwapFinal> swapsRequested = new ArrayList<>();
							
							// Inside interested slots
							for (SwapInitial swap : interestedSlots){
								if (swap != null) { // Non-null
									messagesSent++;
									swapRequest = new SwapFinal();
									swapRequest.setInitalSwapRequest(swap);
									swapRequest.setAgentTo(getAID());
									
									for (TutorialGroup currentTutorial : CurrentTutorials) {
										if (currentTutorial.getTutorialID().equals(swap.getTutorial().getTutorialID())) {
											swapRequest.setTutorialTo(currentTutorial); // Get the current tutorial
											break;
										}
									}
									swapsRequested.add(swapRequest);
								}
							}
							wishedSlots.setSlots(swapsRequested);
							
							ACLMessage reply = msg.createReply();
							try {
								getContentManager().fillContent(reply, wishedSlots);
								send(reply);
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
						else { // If messageboard has no slots
							SlotsRequested wishedSlots = new SlotsRequested();
							wishedSlots.setSlots(new ArrayList<>());
							ACLMessage reply = msg.createReply(); // 0 wished slots 
							try {
								getContentManager().fillContent(reply, wishedSlots);
								send(reply);
							}
							catch (Exception e) {
								e.printStackTrace();
							}
							
						}
					}
				
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			else {
				block();
			}
		}
	}
	

	// Responds to SwapFinal requests
	public class AwaitSlotVerifyBehaviour extends CyclicBehaviour {
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchConversationId("swapRequestCheck");
			ACLMessage msg = myAgent.receive(mt); 
			if (msg != null) {
				try {
					System.out.println(getAID().getLocalName() + " - Checking slot request");
					ContentElement ce = getContentManager().extractContent(msg);
					if (ce instanceof Action) { // Unwrap swapFinal as action
						Concept action = ((Action)ce).getAction();
						if (action instanceof SwapFinal) {
							SwapFinal swapRequest = (SwapFinal) action;
							
							ACLMessage reply = msg.createReply();
							getContentManager().fillContent(reply, swapRequest);
							
							int count = 0;
							for (TutorialGroup tutorial : CurrentTutorials) {
								if (tutorial.getTutorialID().equals(swapRequest.getTutorialTo().getTutorialID())) { // IF THE SAME TUTORIAL
									if (SlotFitness(tutorial.getTimeslot()) < SlotFitness(swapRequest.getTutorialTo().getTimeslot())) {
										reply.setPerformative(ACLMessage.AGREE); // Agree because want to swap slot
										System.out.println(getAID().getLocalName() + " - Agreed to the slot switch");
										CurrentTutorials[count] = swapRequest.getTutorialTo();
										break;
									}
									else {
										reply.setPerformative(ACLMessage.REFUSE); // Refuse because not interested
										System.out.println(getAID().getLocalName() + " - Refused the slot switch");
										break;
									}
								}
								else {
									reply.setPerformative(ACLMessage.REFUSE); // Refuse as different tutorial
								}
								count++;
							}
							
							Action actionReply = new Action(); // Wrap swapRequest inside action again
							actionReply.setAction(swapRequest);
							actionReply.setActor(getAID());
							
							try {
								getContentManager().fillContent(reply, actionReply);
								send(reply);
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
				catch (CodecException ce) {
					ce.printStackTrace();
				}
				catch (OntologyException oe) {
					oe.printStackTrace();
				}
			}
			else {
				block();
			}
		}
	}
	

	// Sends worst slot to Timetable Agent OR check AGENT IS HAPPY HERE
	public class AwaitHappyBehaviour extends CyclicBehaviour {
		
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchConversationId("happyNow");
			ACLMessage msg = myAgent.receive(mt); 
			if (msg != null) {
				System.out.println(getAID().getLocalName() + " - Happy? or Post worst slot");
				try {
					ContentElement ce = getContentManager().extractContent(msg);
					if (ce instanceof Action) {
						Concept action = ((Action)ce).getAction();
						
						if (action instanceof HappyWith) {
							HappyWith happy = ((HappyWith)action);
							
							for (SwapFinal slot : happy.getSlots()) {
								for (int i = 0; i < CurrentTutorials.length; i++) {
									if (slot.getTutorialTo().getTutorialID().equals(CurrentTutorials[i].getTutorialID())) {
										CurrentTutorials[i] = slot.getInitalSwapRequest().getTutorial();
										break;
									}
								}
							}
							
							double meanHappiness = 0;
							
							// Get Mean unhappiness across slots
							for (int i = 0; i < CurrentTutorials.length; i++) {
								meanHappiness += SlotFitness(CurrentTutorials[i].getTimeslot());
							}
							meanHappiness /= CurrentTutorials.length;
							System.out.println(getAID().getLocalName() + " - Happiness is " + meanHappiness);
							
							// If high enough overall happiness -- 0 or 1 slots accepted
							if (meanHappiness <= 0.5) {
								System.out.println(getAID().getLocalName() + " - Unhappy with slots");
								
								// Checking slots that haven't been advertised yet
								List<Integer> slotsNotAdvertised = new ArrayList<>();
								
								// Getting slots not on messageboard
								for (int i = 0; i < CurrentTutorials.length; i++) {
									boolean advertisedAlready = false;
									for (SwapInitial item : slotsMessageBoard.getMessageBoard()) {
										if (item.getAgentFrom().equals(getAID()) && item.getTutorial().getTutorialID() == item.getTutorial().getTutorialID()) {
											advertisedAlready = true;
											continue;
										}
									}
									if (!advertisedAlready) {
										slotsNotAdvertised.add(i);
									}
								}
								
								int worstTutorial = 0;
								
								// Comparing slots - if nothing on board then just send slot and have it denied by TTAgent
								if (slotsNotAdvertised.size() > 0) {
									// Select worst slot not currently on timetable
									for (int i = 0; i < slotsNotAdvertised.size(); i++) {
										if (SlotFitness(CurrentTutorials[slotsNotAdvertised.get(i)].getTimeslot()) < SlotFitness(CurrentTutorials[slotsNotAdvertised.get(worstTutorial)].getTimeslot())) {
											worstTutorial = i;
										}
									} // Pass to TimetablingAgent and check it's not on MessageBoard already
								}
								
								
								UnhappySlot slot = new UnhappySlot();
								
								SwapInitial si = new SwapInitial();
								si.setAgentFrom(myAgent.getAID());
								si.setTutorial(CurrentTutorials[worstTutorial]);
								slot.setSlotToSwap(si);
								
								Action newAction = new Action();
								newAction.setActor(getAID());
								newAction.setAction(slot);
								
								ACLMessage reply = msg.createReply();
								
								try {
									getContentManager().fillContent(reply, newAction);
									send(reply);
								}
								catch(CodecException codecE) {
									codecE.printStackTrace();
								}
								catch(OntologyException oe) {
									oe.printStackTrace();
								}
							}else {
								System.out.println(getAID().getLocalName() + " - Happy with slots!");
								ACLMessage reply = msg.createReply();
								
								PleasedWith pleased = new PleasedWith();
								pleased.setStudent(getAID());
								
								try {
									getContentManager().fillContent(reply, pleased);
									send(reply);
									doDelete();
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
				}
				catch (CodecException ce) {
					ce.printStackTrace();
				}
				catch (OntologyException oe) {
					oe.printStackTrace();
				}
			}
			else {
				block();
			}
		}
	}
	
	
	// Finds the Student Agents and adds them to the UnhappyAgents list
	public class FindTimetablerBehaviour extends OneShotBehaviour {
		
		@Override
		public void action() {
			DFAgentDescription timetablerTemplate = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("timetabling-agent");
			timetablerTemplate.addServices(sd);
			try{
				DFAgentDescription[] ttAgent  = DFService.search(myAgent,timetablerTemplate); 
				timetableAgent = ttAgent[0].getName();
			}
			catch(FIPAException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Returns the fitness of any time slot for the agent
	private double SlotFitness(Timeslot slot) {
		return (1.0 / (SlotPreferences[slot.getDay()][slot.getTime()] + 1));
	}
	
	// Register with the DFD
	protected void register() {
		DFAgentDescription dfd = new DFAgentDescription ();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("student-agent");
		sd.setName( getLocalName() + "-student-agent");
		dfd.addServices(sd);
		
		try{
			DFService.register(this , dfd);
		}
		catch(FIPAException e) {
			e.printStackTrace();
		}
	}
}
