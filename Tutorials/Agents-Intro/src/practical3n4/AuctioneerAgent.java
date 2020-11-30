package practical3n4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AuctioneerAgent extends Agent{

	List<AID>[] auctionRooms; // Jagged Array
	ArrayList<AID> buyerAgents = new ArrayList<>();
	Object[][] items; // Create a 'row' class and then use an array of the row class
	
	Map<String, Integer> itemAndPrice = new HashMap<>();
	
	@Override
	protected void setup() {
		register();
		
		Object[][] args = (Object[][]) getArguments();
		items = args; // 2d array
		
		auctionRooms = new ArrayList[items.length]; // Know the number of rooms (= number of items to auction) but unsure of the number of agents
		for (int i = 0; i < items.length; i++) {
			auctionRooms[i] = new ArrayList<AID>();
			itemAndPrice.put(items[i][0].toString(), (int) items[i][1]);
		}
		
		doWait(10000);
		
		addBehaviour(new FindBuyersBehaviour());
		addBehaviour(new InviteBuyersBehaviour());
		addBehaviour(new AddToRoomBehaviour());


		doWait(10000);
				
		for(int i = 0; i < auctionRooms.length; i++) {
			for (int j = 0; j < auctionRooms[i].size(); j++) {
				System.out.println("Agent " + auctionRooms[i] + " has been assigned to auction room " + j);
			}
		}
		
		addBehaviour(new BiddingBehaviour()); // PROPOSE bids and then accept/reject bid
		//doDelete();
	}
	
	
	// Finds the buyers and adds them to their correct 
	public class FindBuyersBehaviour extends OneShotBehaviour {
		
		@Override
		public void action() {
			DFAgentDescription template = new DFAgentDescription(); // Create template for searching for agent
			ServiceDescription sd = new ServiceDescription();
			sd.setType("buyer-agent");
			template.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				buyerAgents.clear();
				for(int i=0; i<result.length; i++) {
					buyerAgents.add(result[i].getName());
				}
				System.out.println("Found " + result.length + " buyer agents");
			}catch(FIPAException e) {
				e.printStackTrace();
			}
		}

	}
	
	// Invites the buyer agents to the auction
	public class InviteBuyersBehaviour extends OneShotBehaviour {
		
		@Override
		public void action() {
			System.out.println("Inside InviteBuyersBehaviour");
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setContent("join-auction");
			
			for (AID id : buyerAgents) {
				msg.addReceiver(id);
			}
			send(msg);
		}
	}
	
	// Adds the buyer agents to the appropriate room - based on item they wish to purchase
	public class AddToRoomBehaviour extends CyclicBehaviour {

		@Override
		public void action() {
			System.out.println("Inside AddToRoomBehaviour");
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
			ACLMessage msg = myAgent.receive(mt);
			
			if (msg != null) { 
				System.out.println("Message received: " + msg.getContent());
				for (int i = 0; i < items.length; i++) {
					if (msg.getContent().equals(items[i][0])) { // should be comparing against hashmap now
						auctionRooms[i].add(msg.getSender()); // Add the agent to the appropriate auction room
						System.out.println("Added " + msg.getSender() + " to room " + i);
					}
				}
			}
			else {
				block();
			}
		}
	}
	
	// Continues bidding behaviour between rooms - PROPOSES AUCTIONS (price as content)
	public class BiddingBehaviour extends Behaviour {
		boolean[] auctionFinished = new boolean[auctionRooms.length]; // check if auction finished

		@Override
		public void action() {
			System.out.println("Inside BiddingBehaviour");
			for (int i = 0; i < auctionRooms.length; i++) {
				if (auctionFinished[i] == false) {
					ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
					msg.setContent(itemAndPrice.get(items[i][0].toString()).toString());
					System.out.println("Bidding proposal sent with message " + msg.getContent());
				}
			}
			
			for (int i = 0; i < auctionFinished.length; i++) {
				if (auctionRooms[i].size() == 1 && auctionFinished[i] == false) {
					auctionFinished[i] = true;
					if (itemAndPrice.get(items[i][0].toString()) >= (int) items[i][2]) {
						System.out.println("Confirming purchase for room " + i);
						addBehaviour(new ConfirmPurchaseBehaviour(auctionRooms[i].get(0))); // CONFIRM purchase to buyer / REJECT_PROPOSAL to loser
					}
					else {
						System.out.println("Confirming purchase for room " + i);
						addBehaviour(new DeclinePurchaseBehaviour(auctionRooms[i].get(0)));
					}
				}
			}
			
			for (Integer value : itemAndPrice.values()) {
				System.out.println("Prior value is " + value);
			}
			
			int count = 0;

			for (String key : itemAndPrice.keySet()) {
				if (!auctionFinished[count]) {
					int val = itemAndPrice.get(key);
					itemAndPrice.put(key, (int) (val * 1.2));
					
				}
				count++;
			}
			
			for (Integer value : itemAndPrice.values()) {
				System.out.println("New values are " + value);
			}
		}
		@Override
		public boolean done() {
			boolean auctionRoomsEmpty = true;
			for (boolean finished : auctionFinished) {
				if (!finished) {
					auctionRoomsEmpty = false;
				}
			}
			return auctionRoomsEmpty;
		}
	}
	
	// public class ConfirmAuctionBehaviour Switch to behaviour which waits to 
	//confirm the bidding prices of the remaining agents?
	
	// Confirm the purchase of the auction item
	public class ConfirmPurchaseBehaviour extends OneShotBehaviour {
		
		AID receiverID;
		public ConfirmPurchaseBehaviour(AID aid) {
			receiverID = aid;
		}
		
		// Send a message to the winning agent
		@Override
		public void action() {
			System.out.println("ConfirmPurchaseBehaviour");
			ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
			msg.setContent("confirm purchase");
			msg.addReceiver(receiverID);
			send(msg);
		}
	}
	
	// Decline the purchase of the auction item
	// because they haven't bid enough to meet the minimum required value
	public class DeclinePurchaseBehaviour extends OneShotBehaviour {

		AID receiverID;
		public DeclinePurchaseBehaviour(AID aid) {
			receiverID = aid;
		}

		// Inform the loser
		@Override
		public void action() {
			System.out.println("DeclinePurchaseBehaviour");
			ACLMessage msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
			msg.setContent("decline purchase");
			msg.addReceiver(receiverID);
			send(msg);
		}
	}
	
	// Register with DFD
	protected void register() {
		DFAgentDescription dfd = new DFAgentDescription ();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("auctioneer-agent");
		sd.setName( getLocalName() + "-auctioneer-agent");
		dfd.addServices(sd);
		
		try{
			DFService.register(this , dfd);
		}
		catch(FIPAException e) {
			e.printStackTrace() ;
		}
	}
}
