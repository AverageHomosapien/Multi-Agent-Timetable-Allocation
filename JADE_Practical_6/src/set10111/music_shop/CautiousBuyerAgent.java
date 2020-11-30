package set10111.music_shop;

import java.util.ArrayList;
import java.util.List;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import set10111.music_shop_ontology.ECommerceOntology;
import set10111.music_shop_ontology.elements.*;

public class CautiousBuyerAgent extends Agent {
	private Codec codec = new SLCodec();
	private Ontology ontology = ECommerceOntology.getInstance();
	private List<AID> sellerAIDs = new ArrayList<>();
	
	@Override
	protected void setup(){
		register();
		
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		String[] args = (String[])this.getArguments();
		addBehaviour(new FindSellersBehaviour());
		addBehaviour(new QueryBuyerBehaviour(this,5000));
		addBehaviour(new BuyItemBehaviour());
	}
	
	// Finds the sellers in the environment
	private class FindSellersBehaviour extends OneShotBehaviour{

		@Override
		public void action() {
			// Create a template for the agent service we're looking for
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("seller-agent");
			
			try {
				DFAgentDescription[] results = DFService.search(myAgent, template);
				for (DFAgentDescription df : results) {
					sellerAIDs.add(df.getName());
				}
			}catch(FIPAException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class QueryBuyerBehaviour extends TickerBehaviour{
		public QueryBuyerBehaviour(Agent a, long period) {
			super(a, period);
		}
		private boolean finished = false;
		protected void onTick() {
			// Prepare the Query-IF message
			ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
			for (AID id : sellerAIDs) {
				msg.addReceiver(id);
			}
			msg.setLanguage(codec.getName());
			msg.setOntology(ontology.getName()); 
			
			// Prepare the content
			CD cd = new CD();
			cd.setName("Synchronicity");
			cd.setSerialNumber(123);
			ArrayList<Track> tracks = new ArrayList<Track>();
			Track t = new Track();
			t.setName("Every breath you take");
			t.setDuration(230);
			tracks.add(t);
			t = new Track();
			t.setName("King of pain");
			t.setDuration(500);
			tracks.add(t);
			cd.setTracks(tracks);
			
			Owns owns = new Owns();
			owns.setItem(cd);
			for (AID id : sellerAIDs) {
				owns.setOwner(id);
			}
			
			try {
			 // Let JADE convert from Java objects to string
			 getContentManager().fillContent(msg, owns);
			 System.out.println("Request message sent!");
			 send(msg);
			}
			catch (CodecException ce) {
			 ce.printStackTrace();
			}
			catch (OntologyException oe) {
			 oe.printStackTrace();
			}
			
			finished = true;
		}
	}
	
	// BUY ONLY BOOKS - THEN SEE IF I CAN BUY ALTERNATING BOOKS AND SINGLES IN THE ADVANCEDBUYERAGENT
	public class BuyItemBehaviour extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null) {
				try {
					ContentElement ce = getContentManager().extractContent(msg);
					if (ce instanceof Owns) { // Check if message contains owns
						Owns owns = (Owns) ce;
						Item it = owns.getItem();

						// Prepare the content
						CD cd = new CD();
						cd.setName("Synchronicity");
						cd.setSerialNumber(123);
						ArrayList<Track> tracks = new ArrayList<Track>();
						Track t = new Track();
						t.setName("Every breath you take");
						t.setDuration(230);
						tracks.add(t);
						t = new Track();
						t.setName("King of pain");
						t.setDuration(500);
						tracks.add(t);
						cd.setTracks(tracks);

						Sell sell = new Sell();
						sell.setItem(cd);
						sell.setBuyer(sellerAIDs.get(0));
						
						ACLMessage reply = new ACLMessage(ACLMessage.REQUEST);
						reply.setLanguage(codec.getName());
						reply.setOntology(ontology.getName()); 
						
						for (AID id : sellerAIDs) {
							reply.addReceiver(id);
						}					
						
						getContentManager().fillContent(reply, sell);
						System.out.println("Message expected is: " + reply.getContent());
						myAgent.send(reply);
					}
					else
					{
						block();
					}
				}
				catch(CodecException e) {
					e.printStackTrace();
				}
				catch(OntologyException e) {
					e.printStackTrace();
				}
			}
			else {
				block();
			}
		}
	}
	
	private void register() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("buyer-agent");
		sd.setName("-buyer-agent");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
}
