package buyerSellerExample;

import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class BuyerSellerTicker extends Agent{
	
	public static final int NUM_DAYS = 30;
	
	@Override
	protected void setup() {
		register();
		
		doWait(5000);
		addBehaviour(new SynchAgentsBehaviour(this));
	}
	
	// Registers an agent
	protected void register() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("ticker-agent");
		sd.setName(getLocalName() + "-ticker-agent");
		dfd.addServices(sd);
		
		try{
			DFService.register(this , dfd);
		}
		catch(FIPAException e) {
			e.printStackTrace() ;
		}
	}
	
	// Take down an agent
	protected void takeDown() {
		try {
			DFService.deregister(this);
		}
		catch(FIPAException e) {
			e.printStackTrace();
		}
	}
	
	public class SynchAgentsBehaviour extends Behaviour{
		
		private int step = 0; // Switches between behaviours
		private int numFinReceived = 0; // finished messages from other agents
		private int day = 0; // Checks the day - finishes when reached NUM_DAYS
		private ArrayList<AID> simulationAgents = new ArrayList<>(); // Simulation agents filled in step 0 with buyers and sellers
		
		public SynchAgentsBehaviour(Agent a) {
			super(a);
		}
		
		@Override
		public void action() {			
			switch(step) {
			case 0:
				System.out.println("New day");
				
				DFAgentDescription template1 = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("buyer");
				template1.addServices(sd);
				
				DFAgentDescription template2 = new DFAgentDescription();
				ServiceDescription sd2 = new ServiceDescription();
				sd2.setType("seller");
				template2.addServices(sd2);
				
				try {
					DFAgentDescription[] agentsType1 = DFService.search(myAgent, template1);
					for (int i = 0; i < agentsType1.length; i++) {
						simulationAgents.add(agentsType1[i].getName()); // Get AIDs from the buyers
					}
					
					DFAgentDescription[] agentsType2 = DFService.search(myAgent, template2);
					for (int i = 0; i < agentsType2.length; i++) {
						simulationAgents.add(agentsType2[i].getName()); // Get AIDs from the sellers
					}
				}
				catch(FIPAException e) {
					e.printStackTrace();
				}
				
				ACLMessage tick = new ACLMessage(ACLMessage.INFORM);
				tick.setContent("new day");
				for (AID id : simulationAgents) { // Simulation agents has 2 kinds of AIDs
					tick.addReceiver(id);
				}
				myAgent.send(tick);
				step++;
				day++;
				break; // day+break at daybreak
			case 1:
				MessageTemplate mt = MessageTemplate.MatchContent("done"); // Only looking for "done" messages
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null) {
					numFinReceived++; // number of finished messages received
					if (numFinReceived >= simulationAgents.size()) { // If all agents are finished (enough messages received)
						step++;
					}
				}
				else { // if message is null
					block();
				}
			}
		}
		
		@Override
		public boolean done() {
			return step ==2;
		}
		
		@Override
		public void reset() {
			super.reset();
			step = 0;
			simulationAgents.clear();
			numFinReceived = 0;
		}
		
		@Override
		public int onEnd() {
			System.out.println("End of day " + day);
			if (day == NUM_DAYS) {
				// send termination message to each agent
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setContent("terminate");
				for (AID id : simulationAgents) {
					msg.addReceiver(id);
				}
				
				myAgent.send(msg);
				myAgent.doDelete();
			}
			else {
				reset();
				myAgent.addBehaviour(this);
			}
			
			return 0;
		}
	}
}
