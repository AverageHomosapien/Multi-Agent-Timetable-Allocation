package set10111.music_shop;

import java.util.ArrayList;
import java.util.List;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import set10111.music_shop_ontology.ECommerceOntology;
import set10111.music_shop_ontology.elements.*;

public class AdvancedBuyerAgent extends Agent{
	private Codec codec = new SLCodec();
	private Ontology ontology = ECommerceOntology.getInstance();
	private List<AID> sellerAIDs = new ArrayList<>();
	private Item item;
	
	@Override
	protected void setup() {
		register();
		
		// Item input
		Object[] arguments = getArguments();
		item = (Item) arguments[0];
		
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		addBehaviour(new FindSellersBehaviour()); // Finds the sellers
		addBehaviour(new QueryBuyerBehaviour(this, 5000)); // Queries item availability
		addBehaviour(new BuyBehaviour()); // Cyclic responding to query behaviour
		addBehaviour(new UnableToBuyBehaviour()); // Deletes agent if they're unable to buy
	}

	
	// Finds the sellers in the environment
	private class FindSellersBehaviour extends OneShotBehaviour {

		@Override
		public void action() {
			// Create a template for the agent service we're looking for
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("seller-agent");
			
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				for (DFAgentDescription df : result) {
					sellerAIDs.add(df.getName());
				}
			}catch(FIPAException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Queries the seller to see if book in stock
	private class QueryBuyerBehaviour extends TickerBehaviour{
		public QueryBuyerBehaviour(Agent a, long period) {
			super(a, period);
		}
		
		protected void onTick() {
			ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF); // Prepare Query-IF message
			msg.setLanguage(codec.getName());
			msg.setOntology(ontology.getName()); 
			
			for (AID id : sellerAIDs) { // Good practice - even if only 1 seller
				msg.addReceiver(id);
			}
			
			Owns owns = new Owns();
			for (AID id : sellerAIDs) {
				owns.setOwner(id);
			}
			owns.setItem(item); // Set as input item
			// Since not CD/Single specific, need to unwrap 1 extra layer to
			//.. deal with underlying item directly if interaction required
			
			try {
				getContentManager().fillContent(msg, owns);
				send(msg);
			}
			catch (CodecException ce) {
				ce.printStackTrace();
			}
			catch (OntologyException oe) {
				oe.printStackTrace();
			} 
			
		}
	}
	
	// Agent attempts to corner the market of the given item
	public class BuyBehaviour extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM); 
			ACLMessage msg = receive(mt);
			if (msg != null) {
				try {
					ContentElement ce = getContentManager().extractContent(msg);
					if (ce instanceof Owns) {
						Owns owns = (Owns) ce;
						Item it = owns.getItem(); // Could use item passed in args, but unwrap item for example
						
						Sell sell = new Sell();
						sell.setItem(it);
						sell.setBuyer(myAgent.getAID());
						
						ACLMessage reply = msg.createReply();
						reply.setLanguage(codec.getName());
						reply.setOntology(ontology.getName());
						reply.setPerformative(ACLMessage.REQUEST);
						
						Action request = new Action();
						request.setAction(sell);
						request.setActor(myAgent.getAID());
						
						try {
							getContentManager().fillContent(reply, request);
							send(reply);
						}
						catch(CodecException codecE) {
							codecE.printStackTrace();
						}
						catch(OntologyException oe) {
							oe.printStackTrace();
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			else {
				block();
			}
		}
	}
	
	// Deletes agent if they're unable to buy
	public class UnableToBuyBehaviour extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.DISCONFIRM); 
			ACLMessage msg = receive(mt);
			if (msg != null) {
				try {
					ContentElement ce = getContentManager().extractContent(msg);
					if (ce instanceof Owns) {
						Owns owns = (Owns) ce;
						Item it = owns.getItem();
						if (it instanceof CD) { // Unwrap item
							CD cd = (CD) it;
							System.out.println("No more copies of " + cd.getName()+".. Deleting agent..");
						}
						else if (it instanceof Single) {
							Single single = (Single) it;
							System.out.println("No more copies of " + single.getName()+".. Deleting agent..");
						}
						else if (it instanceof Book) {
							Book book = (Book) it;
							System.out.println("No more copies of " + book.getName()+".. Deleting agent..");
						}
						doDelete();
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			else {
				block();
			}
		}
	}
	
	
	// Registers the agent with the Directory Finder
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
