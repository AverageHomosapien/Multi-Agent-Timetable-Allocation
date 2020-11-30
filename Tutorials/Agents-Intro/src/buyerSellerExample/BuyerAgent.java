package buyerSellerExample;

import java.util.ArrayList;
import java.util.HashMap;

import buyerSellerExample.BuyerSellerTicker.SynchAgentsBehaviour;
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
import jade.tools.sniffer.Message;

public class BuyerAgent extends Agent{

	private ArrayList<AID> sellers = new ArrayList<>();
	private ArrayList<String> booksToBuy = new ArrayList<>();
	private HashMap<String, Offer> bestOffers = new HashMap<>();
	private AID tickerAgent;
	private int numQueriesSent;
	
	@Override
	protected void setup() {
		register();
		
		booksToBuy.add("Java for Dummies");
		booksToBuy.add("JADE: the Inside Story");
		booksToBuy.add("Multi-Agent Systems for Everybody");
		
		addBehaviour(new TickerWaiter(this)); // waits for messages from the ticker agent - day end or terminate
	}
	
	// Registers an agent
	protected void register() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("buyer");
		sd.setName(getLocalName() + "-buyer-agent");
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
	
	// Waiting for the Buyer Seller Ticker
	public class TickerWaiter extends CyclicBehaviour{
		
		// behaviour to wait for a new day
		public TickerWaiter(Agent a) {
			super(a);
		}
		
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchContent("new day"), MessageTemplate.MatchContent("terminate"));
			ACLMessage msg = myAgent.receive(mt);
			
			// Checking if message is "new day" or "terminate"
			if (msg != null){
				if (tickerAgent == null) {
					tickerAgent = msg.getSender();
				}
				
				if (msg.getContent().equals("new day")) {
					SequentialBehaviour dailyActivity = new SequentialBehaviour(); // Spawn new sequential behaviour for day activities
					
					// Sub-behaviours
					dailyActivity.addSubBehaviour(new FindSellers(myAgent));
					dailyActivity.addSubBehaviour(new SendEnquiries(myAgent));
					dailyActivity.addSubBehaviour(new CollectOffers(myAgent));
					dailyActivity.addSubBehaviour(new EndDay(myAgent));
				}
				else { // If not new day, termination message
					myAgent.doDelete();
				}
			}
			else {
				block();
			}
		}
	}
	
	// Finds the seller agents
	public class FindSellers extends OneShotBehaviour {
		
		public FindSellers(Agent a) {
			super(a);
		}
		
		@Override
		public void action() { // Send out a call for proposals for each book using Contract Net Protocol
			numQueriesSent = 0;
			for(String bookTitle : booksToBuy) {
				ACLMessage enquiry = new ACLMessage(ACLMessage.CFP);
				enquiry.setContent(bookTitle);
				enquiry.setConversationId(bookTitle);
				for (AID seller : sellers) {
					enquiry.addReceiver(seller);
					numQueriesSent++;
				}
				
				myAgent.send(enquiry);
			}
		}
	}
	
	public class SendEnquiries extends OneShotBehaviour {
		
		public SendEnquiries(Agent a) {
			super(a);
		}
		
		@Override
		public void action() { 
		// Send out call for proposals for each book, using the Contract Net Protocol
			numQueriesSent = 0;
			for(String bookTitle : booksToBuy) {
				ACLMessage enquiry = new ACLMessage(ACLMessage.CFP);
				enquiry.setContent(bookTitle);
				enquiry.setConversationId(bookTitle);
				for (AID seller : sellers) {
					enquiry.addReceiver(seller);
					numQueriesSent++;
				}
				myAgent.send(enquiry);
			}
		}
	}
	
	public class CollectOffers extends Behaviour {
		private HashMap<String, Integer> repliesReceived = new HashMap<>();
		private int numRepliesReceived = 0;
		private boolean finished = false;
		
		public CollectOffers(Agent a) {
			super(a);
		}
		
		@Override
		public void action() {
			boolean received = false;
			for(String bookTitle : booksToBuy) {
				MessageTemplate mt = MessageTemplate.MatchConversationId(bookTitle);
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null) {
					received = true;
					numRepliesReceived++;
					if(msg.getPerformative() == ACLMessage.PROPOSE) { // The reply is an offer so see whether to update best offer so far
						if (!bestOffers.containsKey(bookTitle)) {
							bestOffers.put(bookTitle, new Offer(msg.getSender(), Integer.parseInt(msg.getContent())));
						}
						else { // Update only if new offer is better than existing offer
							int newOffer = Integer.parseInt(msg.getContent());
							int existingOffer = bestOffers.get(bookTitle).getPrice();
							if (newOffer < existingOffer) {
								bestOffers.remove(bookTitle);
								bestOffers.put(bookTitle, new Offer(msg.getSender(), newOffer));
							}
						}
					}
				}
			}
			if (!received) {
				block();
			}
		}
		
		@Override
		public boolean done() { // can't seem to override the oneshotbehaviour
			return numRepliesReceived == numQueriesSent;
		}
		
		@Override
		public int onEnd() {
			// print the offers
			for (String book : booksToBuy) {
				if (bestOffers.containsKey(book)) {
					Offer o = bestOffers.get(book);
					System.out.println(book + "," + o.getSeller() + "," + o.getPrice());
				}else {
					System.out.println("No offers for " + book);
				}
			}
			return 0;
		}
		
	}
	
	public class EndDay extends OneShotBehaviour {
		
		public EndDay(Agent a) {
			super(a);
		}
		
		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(tickerAgent);
			msg.setContent("done");
			myAgent.send(msg);
			
			// Send a message to each seller that we have finished
			ACLMessage sellerDone = new ACLMessage(ACLMessage.INFORM);
			sellerDone.setContent("done");
			for (AID seller : sellers) {
				sellerDone.addReceiver(seller);
			}
			myAgent.send(sellerDone);
		}
	}
}
