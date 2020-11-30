package __sequentialCyclicTesting;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Receiver extends Agent{
	private AID senderAgent;	
	
	protected void setup() {
		register();
		doWait(3000);
		addBehaviour(new FindSenderBehaviour());
		addBehaviour(new ReceiverBehaviour());
		//addBehaviour(new DuplicateReceiverBehaviour());
		//addBehaviour(new DupliDuplicateReceiverBehaviour());
		//addBehaviour(new TickerSenderBehaviour(this, 500));
	}
	
	public class TickerSenderBehaviour extends TickerBehaviour {

		public TickerSenderBehaviour(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			ACLMessage enquiry = new ACLMessage(ACLMessage.CONFIRM);
			enquiry.setContent("billy n'aye");
			enquiry.addReceiver(senderAgent);
			myAgent.send(enquiry);
		}
	}
		
	// Finds the receiver agent
	public class FindSenderBehaviour extends OneShotBehaviour {
		@Override
		public void action() {
			DFAgentDescription studentTemplate = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("sender-agent");
			studentTemplate.addServices(sd);
			try{
				DFAgentDescription[] agentsType1  = DFService.search(myAgent,studentTemplate); 
				senderAgent = agentsType1[0].getName();
				System.out.println("Sender agent is " + senderAgent);
			}
			catch(FIPAException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Receiver Behaviour
	public class ReceiverBehaviour extends CyclicBehaviour {
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchContent("crazy?");
			ACLMessage msg = myAgent.receive(mt); 
			if (msg != null) {
				System.out.println("ORIGINAL TRUE");
				ACLMessage reply = msg.createReply();
				reply.setContent("aye");
				reply.setPerformative(ACLMessage.CONFIRM);
				doWait(2000);
				send(reply);
			}
			else {
				block();
			}
		}
	}
	
	// Receiver Behaviour
	public class DuplicateReceiverBehaviour extends CyclicBehaviour {
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchContent("crazy?");
			ACLMessage msg = myAgent.receive(mt); 
			if (msg != null) {
				System.out.println("DUPLICATE TRUE");
				ACLMessage reply = msg.createReply();
				reply.setContent("aye");
				reply.setPerformative(ACLMessage.CONFIRM);
				doWait(2000);
				send(reply);
			}
			else {
				block();
			}
		}
	}
	
	// Receiver Behaviour
		public class DupliDuplicateReceiverBehaviour extends CyclicBehaviour {
			@Override
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchContent("crazy?");
				ACLMessage msg = myAgent.receive(mt); 
				if (msg != null) {
					System.out.println("DUPLDUPLICATE TRUE");
					ACLMessage reply = msg.createReply();
					reply.setContent("aye");
					reply.setPerformative(ACLMessage.CONFIRM);
					doWait(2000);
					send(reply);
				}
				else {
					block();
				}
			}
		}
	
	// Register with DFD
	protected void register() {
		DFAgentDescription dfd = new DFAgentDescription ();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("receiver-agent");
		sd.setName( getLocalName() + "-receiver-agent");
		dfd.addServices(sd);
		
		try{
			DFService.register(this , dfd);
		}
		catch(FIPAException e) {
			e.printStackTrace() ;
		}
	}
}

