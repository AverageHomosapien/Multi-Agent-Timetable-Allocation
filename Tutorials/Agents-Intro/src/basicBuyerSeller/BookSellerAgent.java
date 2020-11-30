package basicBuyerSeller;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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

public class BookSellerAgent extends Agent{
	// Store our books
	HashMap<String, Double> books = new HashMap<String, Double>();
	
	protected void setup() {		
		books.put("Harry Potter", 14.99);
		books.put("Lord of the Rings", 12.99);
		books.put("Lord of the Flies", 8.99);
		
		register();
		addBehaviour(new CheckBooksLeft(this, 20000));
		//addBehaviour(new )
		
		// Add the behaviour serving purchase orders from buyer agents
		addBehaviour(new PurchaseOrdersServer());
	}
	
	// If no books left, kills the agent
	private class CheckBooksLeft extends TickerBehaviour{
		public CheckBooksLeft(Agent a, long period) {
			super(a, period);
		}

		public void onTick() {
			if (books.size() == 0) {
				System.out.println("Purpose has been achieved");
				deregister();
				doDelete();
			}
			else {				
				for (String key : books.keySet()) {
					System.out.println("The book " + key + " is still available!");
				}
			}
		}
	}
	
	// Agent registers with the System
	protected void register() {
		DFAgentDescription dfd = new DFAgentDescription ();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("seller-agent");
		sd.setName( getLocalName() + "-seller-agent");
		dfd.addServices(sd);
		
		try{
			DFService.register(this , dfd);
			System.out.println("Registered");
		}
		catch(FIPAException e) {
			e.printStackTrace() ;
		}

	}
	
	protected void deregister() {
		try{
			// doDelete(); automatically deregisters the agent before killing it
			DFService.deregister(this);
			System.out.println("Deregistered");
		}
			catch(FIPAException e){
			e.printStackTrace() ;
		}
	}
			
	/**
	   Inner class PurchaseOrdersServer.
	   This is the behaviour used by Book-seller agents to serve incoming 
	   offer acceptances (i.e. purchase orders) from buyer agents.
	   The seller agent removes the purchased book from its catalogue 
	   and replies with an INFORM message to notify the buyer that the
	   purchase has been sucesfully completed.
	 */
	private class PurchaseOrdersServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				System.out.println("Message content is " + msg.getContent());
				// ACCEPT_PROPOSAL Message received. Process it - ONLY DECLINE IF NOT CONTAINED
				String title = msg.getContent();
				ACLMessage reply = msg.createReply();
				
				// If book still available
				if (books.containsKey(title)) {
					books.remove(title);
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					reply.setContent(title);
					System.out.println(title+" sold to agent "+msg.getSender().getName());
				}else {
					// The requested book has been sold to another buyer in the meanwhile .
					reply.setPerformative(ACLMessage.FAILURE);
					reply.setContent("not-available");
				}
				
				myAgent.send(reply);
			}
			else {
				System.out.println("Message content is null");
				block();
			}
		}
	}  // End of inner class OfferRequestsServer
}
