package __sequentialCyclicTesting;

import _senderReceiver.SenderAgent.SearchYellowPages;
import _senderReceiver.SenderAgent.SenderBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class NewSender extends Agent{
	
	private AID receiverAgent;
	
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
		
		addBehaviour(new FindReceiverBehaviour());
		addBehaviour(new CheckerBehaviour());
	}
	
	// Finds the receiver agent
	public class FindReceiverBehaviour extends OneShotBehaviour {

		@Override
		public void action() {
			DFAgentDescription studentTemplate = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("receiver-agent");
			studentTemplate.addServices(sd);
			try{
				DFAgentDescription[] agentsType1  = DFService.search(myAgent,studentTemplate); 
				receiverAgent = agentsType1[0].getName();
				System.out.println("Receiver agent is " + receiverAgent);
			}
			catch(FIPAException e) {
				e.printStackTrace();
			}
		}
	}
	
	public class CheckerBehaviour extends OneShotBehaviour {

		@Override
		public void action() {
			
			SequentialBehaviour dailyActivity = new SequentialBehaviour();
			//sub-behaviours will execute in the order they are added
			for (int i = 0; i < 3; i++) {
				dailyActivity.addSubBehaviour(new Msg1());
				dailyActivity.addSubBehaviour(new Msg2());
				dailyActivity.addSubBehaviour(new Msg3());
				dailyActivity.addSubBehaviour(new SenderMsg());
				myAgent.addBehaviour(dailyActivity);
				myAgent.addBehaviour(new Msg4());
				System.out.println("Loop number " + i + " is over");
			}
		}
	}
	
	public class Msg1 extends OneShotBehaviour {
		@Override
		public void action() {
			System.out.println("Message 1!!");
		}
	}
	
	public class Msg2 extends OneShotBehaviour {
		@Override
		public void action() {
			System.out.println("Message 2!!");
		}
	}
	
	public class Msg3 extends OneShotBehaviour {
		@Override
		public void action() {
			System.out.println("Message 3!!");
			ACLMessage enquiry = new ACLMessage(ACLMessage.QUERY_IF);
			enquiry.setContent("crazy?");
			enquiry.addReceiver(receiverAgent);
			myAgent.send(enquiry);
		}
	}
	
	// Once cyclic sub-behaviour stops - it just stops going
	public class SenderMsg extends Behaviour {
		boolean msgReceived = false;
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchContent("aye");
			ACLMessage msg = myAgent.receive(mt); 
			if (msg != null) {
				System.out.println("Stuck on message 4!!");
				msgReceived = true;
			}
			else {
				System.out.println("blocked..");
				block();
			}
		}
		@Override
		public boolean done() {
			return msgReceived;
		}
	}
	
	public class Msg4 extends OneShotBehaviour {
		@Override
		public void action() {
			System.out.println("Message 4!!");
			doWait(5000);
			System.out.println("Waited. Start again");
		}
	}
}
