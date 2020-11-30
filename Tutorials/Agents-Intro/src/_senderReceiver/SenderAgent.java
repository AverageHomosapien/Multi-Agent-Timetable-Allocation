package _senderReceiver;

import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SenderBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class SenderAgent extends Agent {
	
	private ArrayList <AID> receiverAgents = new ArrayList<>();
	
	@Override
	protected void setup() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("sender-agent");
		sd.setName(getLocalName() + "-sender-agent");
		dfd.addServices(sd);
		
		try{
			DFService.register(this , dfd);
		}
		catch(FIPAException e) {
			e.printStackTrace() ;
		}
		
		// Find agents
		addBehaviour(new SearchYellowPages(this, 10000));
		
		// Send message
		addBehaviour(new SenderBehaviour(this, 10000));
	}
	
	// De-registers agent from yellow pages
	protected void deregister() {
		try {
			DFService.deregister(this);
		}
		catch(FIPAException e) {
			e.printStackTrace();
		}
	}
	
	
	public class SearchYellowPages extends TickerBehaviour{
		
		public SearchYellowPages(Agent a, long period) {
			super(a, period);
		}
		
		@Override
		protected void onTick() {
			// Create a message template
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("receiver-agent");
			template.addServices(sd);
			
			// Query the DF agent
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				receiverAgents.clear();
				for (int i = 0; i<result.length; i++) {
					receiverAgents.add(result[i].getName()); // receiverAgents has AIDs
				}
			}
			catch(FIPAException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public class SenderBehaviour extends TickerBehaviour {
		
		public SenderBehaviour(Agent a, long period) {
			super(a, period);
		}
		
		@Override
		protected void onTick() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setContent("hello sir, from agent " + myAgent.getLocalName());
			
			for (AID receiver : receiverAgents) {
				msg.addReceiver(receiver);
			}
			send(msg);
		}
	}
}