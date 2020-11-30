package _syncedBehaviour;

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

public class TickerAgent extends Agent{
	
	@Override
	protected void setup() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("ticker-agent");
		sd.setName(getLocalName() + "-ticker-agent");
		dfd.addServices(sd);
		
		try {
			DFService.register(this, dfd);
		}
		catch(FIPAException e){
			e.printStackTrace();
		}
		
		doWait(10000);
		addBehaviour(new SynchAgentsBehaviour(this));
	}
	
	@Override
	protected void takeDown() {
		
		try {
			DFService.deregister(this);
		}
		catch(FIPAException e) {
			e.printStackTrace();
		}
	}
	
	public class SynchAgentsBehaviour extends Behaviour {
		private int step = 0; // where we are in the behaviour (state?)
		private int numFinReceived = 0; // number of finished messages from other agents
		
		private ArrayList<AID> simulationAgents = new ArrayList<>();
		
		public SynchAgentsBehaviour(Agent a) {
			super(a);
		}
		
		public void action() {
			switch(step) {
			case 0:
				// Find all agents here using directory service
				// Have 2 types of agents here
				// "simulation-agent" and "simulation-agent-2"
				DFAgentDescription template1 = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("simulation-agent");
				template1.addServices(sd);
				
				DFAgentDescription template2 = new DFAgentDescription();
				ServiceDescription sd2 = new ServiceDescription();
				sd2.setType("simulation-agent2");
				template2.addServices(sd);
				try {
					simulationAgents.clear();
					DFAgentDescription[] agentsType1 = DFService.search(myAgent, template1);
					for(int i = 0; i < agentsType1.length; i++) {
						simulationAgents.add(agentsType1[i].getName()); // The AID
						
						System.out.println(agentsType1[i].getName());
					}
					
					DFAgentDescription[] agentsType2 = DFService.search(myAgent, template2);
					for (int i = 0; i < agentsType2.length; i++) {
						simulationAgents.add(agentsType2[i].getName()); // The AID
						System.out.println(agentsType2[i].getName());
					}
					
					
				}catch(FIPAException e) {
					e.printStackTrace();
				}
				
				ACLMessage tick = new ACLMessage(ACLMessage.INFORM);
				tick.setContent("new day");
				
				for (AID id : simulationAgents) {
					tick.addReceiver(id);
				}
				myAgent.send(tick);
				step++;
				break;
			case 1: // wait to receive a "Done" message from all agents
				MessageTemplate mt = MessageTemplate.MatchContent("done");
				ACLMessage msg = myAgent.receive(mt);
				
				if (msg != null) {
					numFinReceived++;
					if(numFinReceived >= simulationAgents.size()) {
						step++;
					}
				}
				else {
					block();
				}
			}
		}
		
		@Override
		public boolean done() {
			return step == 2;
		}
		
		@Override
		public void reset() {
			step = 0;
			numFinReceived = 0;
		}
		
		@Override
		public int onEnd() {
			System.out.println("End of day");
			reset();
			myAgent.addBehaviour(this);
			return 0;
		}
	}
	
}
