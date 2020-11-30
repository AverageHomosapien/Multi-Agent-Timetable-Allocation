package agentAuction;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Auctioneer extends Agent{
	
	String[][] items;
	
	@Override
	protected void setup() {
		Object[] args = getArguments();
		items = (String[][]) args[0];
		
		register();
		
		addBehaviour(new StartAuctionBehaviour(this)); // Need to instantiate as new behaviour
	}
	
	
	
	public class StartAuctionBehaviour extends CyclicBehaviour{

		public StartAuctionBehaviour (Agent a) {
			super(a);
		}
		
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST); // MESSAGE TEMP MUST BE THE SAME AS THE MESSAGE RECEIVED
			ACLMessage msg = myAgent.receive(mt);
			
			// If message has no content
			if (msg != null) {
				String item = msg.getContent();
				
				System.out.println(msg.getContent());
				
			}else {
				System.out.println("Null message");
				block();
			}
			
		}
	}

	// Registers an agent
	protected void register() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("auctioneer-agent");
		sd.setName(getLocalName() + "-auctioneer-agent");
		dfd.addServices(sd);
		
		try{
			DFService.register(this , dfd);
		}
		catch(FIPAException e) {
			e.printStackTrace() ;
		}
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
	
	
	
}
