package practical3n4;

import java.util.ArrayList;
import java.util.regex.Pattern;

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

// BuyerAgent will be a reactive class, responding to the AuctioneerAgent
public class BuyerAgent extends Agent{
	
	AID auctioneerAgent;
	Object[] items;
	
	@Override
	protected void setup() {
		register();

		// Items in the form (String item, int maxBid)
		Object[] args = getArguments();
		items = args;
				
		// All cyclicBehaviour, waiting for the AuctioneerAgent
		addBehaviour(new AwaitAuctionBehaviour());
		addBehaviour(new BidOrBustBehaviour());
		addBehaviour(new ConfirmPurchaseBehaviour());
		addBehaviour(new ConfirmAuctionLostBehaviour());
	}
	
	// Waiting to be invited to auction
	public class AwaitAuctionBehaviour extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchContent("join-auction");
			ACLMessage msg = myAgent.receive(mt);
			// Message not empty
			if (msg != null) {
				try {
					if (msg.getContent().equals("join-auction")) {
						auctioneerAgent = msg.getSender();
						ACLMessage reply = msg.createReply();
						if (items != null && items.length > 0) {
							System.out.println("Attempting to purchase a "+items[0].toString());
							reply.setPerformative(ACLMessage.CONFIRM);
							reply.setContent(items[0].toString());
							myAgent.send(reply);
						}else {
							System.out.println("I have no more items to try and purchase. Goodbye");
							reply.setPerformative(ACLMessage.DISCONFIRM);
							myAgent.send(reply);
							doDelete();
						}
					}
				}
				catch(Exception e) {
					System.out.println("AwaitAuctionBehaviour error");
					e.printStackTrace();
				}
			}
			else {
				block();
			}
		}
	}
	
	// Keeps bidding until the agent taps out (not willing to pay that much)
	public class BidOrBustBehaviour extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchSender(auctioneerAgent), 
					MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
			ACLMessage msg = myAgent.receive(mt);
			int price;
			
			if (msg != null) {
				System.out.println("BidOrBustBehaviour");
				ACLMessage reply = msg.createReply();
				price = Integer.valueOf(msg.getContent());
				
				if (price < (int) items[1]) {
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					reply.setContent("accept-bid");
				}else {
					reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
					reply.setContent("decline-bid");
				}
				send(reply);
			}
			else {
				block();
			}
		}
	}
	
	// Confirms purchase if notified they have won auction
	public class ConfirmPurchaseBehaviour extends CyclicBehaviour {
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL), 
					MessageTemplate.MatchContent("confirm purchase"));
			ACLMessage msg = myAgent.receive(mt);
			
			if (msg != null) {
				System.out.println("ConfirmPurchaseBehaviour");
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.AGREE); // Changed to agree from inform
				reply.setContent("accept-win");
				send(reply);
				doDelete();
			}
			else {
				block();
			}
			
		}
		
	}
	
	// Confirms that auction has been lost
	public class ConfirmAuctionLostBehaviour extends CyclicBehaviour {
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL), 
					MessageTemplate.MatchContent("decline purchase"));
			ACLMessage msg = myAgent.receive(mt);
			
			if (msg != null) {
				System.out.println("ConfirmAuctionLostBehaviour");
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.AGREE); // Confirms that message has been received
				reply.setContent("goodbye");
				send(reply);
				doDelete();
			}
			else {
				block();
			}
			
		}
		
	}
	
	// Registers the buyer with the DFD service
	protected void register() {
		DFAgentDescription dfd = new DFAgentDescription ();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("buyer-agent");
		sd.setName( getLocalName() + "-buyer-agent");
		dfd.addServices(sd);
		
		try{
			DFService.register(this , dfd);
		}
		catch(FIPAException e) {
			e.printStackTrace() ;
		}
	}
}
