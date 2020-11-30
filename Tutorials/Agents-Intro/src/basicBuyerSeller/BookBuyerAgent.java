package basicBuyerSeller;
import java.util.ArrayList;
import java.util.Random;

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

// Sammy, Stephen and Sarah are trying to buy 1 book each
public class BookBuyerAgent extends Agent{
	
	private ArrayList<AID> sellers = new ArrayList<>();
	int tries = 3;
	Random rand = new Random();
	
	protected void setup() {
		int book_rq_timer = rand.nextInt(10) + 1;
		System.out.println("Book request timer is " + book_rq_timer);
		
		System.out.println("HellO! My name is " + getAID().getName() + " \n");
		addBehaviour(new FindSellers());
		
		addBehaviour(new RequestBook(this, 1000 * book_rq_timer));
		
		addBehaviour(new SellerAccept());
	}
	
	// Finds the sellers once at the beginning - Examples have active looking as ticker Behaviour
	private class FindSellers extends OneShotBehaviour {
		public void action() {
			// Create a template for the agent service we're looking for
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("seller-agent");
			template.addServices(sd);
			
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				for(int i=0; i<result.length; i++) {
					sellers.add(result[i].getName());
					System.out.println(sellers.get(i) + " found");
				}
				// Notifying if no sellers have been found
				if (sellers.size() == 0) {
					System.out.println("I am " + myAgent.getAID() + " and have found no sellers");
				}
			}catch(FIPAException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Delete buyer if seller receives message
	private class SellerAccept extends CyclicBehaviour {
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			
			if (msg != null) {
				System.out.println("Received successful sale message. " + myAgent.getAID() + " bought " + msg.getContent());	
				System.out.println("Goodnight!");
				doDelete();
			}else {
				block();
			}
		}
	}
	
	// Requests a book from the seller agents
	private class RequestBook extends TickerBehaviour {

		public RequestBook(Agent a, long period) {
			super(a, period);
		}
		
		// On TickerBehaviour call
		protected void onTick() {
			System.out.println("Try number " + (4 - tries));
			tries-=1;
			
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			
			// Randomly deciding on a book
			int num = rand.nextInt(3);
			switch(num) {
			case 0:
				msg.setContent("Harry Potter");
				break;
			case 1:
				msg.setContent("Lord of the Rings");
				break;
			case 2:
				msg.setContent("Lord of the Flies");
			}
			
			for (AID agents : sellers) {
				msg.addReceiver(agents);
			}			
			System.out.println("Message content is " + msg.getContent());
			send(msg);
			if (tries <= 0) {
				System.out.println("Agent has been unable to purchase a book. Closing");
				doDelete();
			}
		}
	}
}

// Default arguments to run Jade Runtime Environment
// main class: jade.Boot
// program args: -gui jade.Boot <agent nickname>:<package name>.<agent class name>