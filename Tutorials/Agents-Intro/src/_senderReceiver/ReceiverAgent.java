package _senderReceiver;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceiverAgent extends Agent{

	@Override
	protected void setup() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("receiver-agent");
		sd.setName(getLocalName() + "-receiver-agent");
		dfd.addServices(sd);
		
		try{
			DFService.register(this , dfd);
		}
		catch(FIPAException e) {
			e.printStackTrace() ;
		}
		
		addBehaviour(new ReceiverBehaviour(this));
	}
	
	
	// Receiving messages -- CYCLIC BEHAVIOUR IS USED TO RECEIVE
	public class ReceiverBehaviour extends CyclicBehaviour{
		
		public ReceiverBehaviour(Agent a) {
			super(a);
		}
		
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM); // MESSAGE TEMP MUST BE THE SAME AS THE MESSAGE RECEIVED
			ACLMessage msg = myAgent.receive(mt);
			//ACLMessage msg = myAgent.receive();// Try to receive agent message
			if (msg != null) {
				System.out.println("I am " + myAgent.getLocalName());
				System.out.println("Message received from " + msg.getSender());
				System.out.println("The message is: ");
				System.out.println(msg.getContent());
				System.out.println();
			}else {
				System.out.println("Message is null");
				block(); // Put the behaviour to sleep until a message arrived
			}
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
