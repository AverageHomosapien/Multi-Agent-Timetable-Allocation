package set10111.music_shop;

import java.util.ArrayList;
import java.util.List;

import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import set10111.music_shop_ontology.ECommerceOntology;
import set10111.music_shop_ontology.elements.*;

public class RecklessBuyerAgent extends Agent {
	private Codec codec = new SLCodec();
	private Ontology ontology = ECommerceOntology.getInstance();
	private List<AID> sellerAIDs = new ArrayList<>();
	
	@Override
	protected void setup(){
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		String[] args = (String[])this.getArguments();
		addBehaviour(new FindSellersBehaviour());
		addBehaviour(new RecklessBuyerBehaviour(this,10000));
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
				DFAgentDescription[] result = DFService.search(myAgent, template);
				for (AID id : sellerAIDs) {
					sellerAIDs.add(id);
				}
			}catch(FIPAException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class RecklessBuyerBehaviour extends TickerBehaviour{
		public RecklessBuyerBehaviour(Agent a, long period) {
			super(a, period);
		}
		
		protected void onTick() {
			// Prepare the action request message
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			for (AID id : sellerAIDs) {
				msg.addReceiver(id);
			}
			msg.setLanguage(codec.getName());
			msg.setOntology(ontology.getName()); 
			// Prepare the content. 
			
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
			Sell order = new Sell();
			order.setBuyer(myAgent.getAID());
			order.setItem(cd);
			
			//IMPORTANT: According to FIPA, we need to create a wrapper Action object 
			//with the action and the AID of the agent
			//we are requesting to perform the action
			//you will get an exception if you try to send the sell action directly
			//not inside the wrapper!!!
			Action request = new Action();
			request.setAction(order);
			for (AID id : sellerAIDs) {
				request.setActor(id);  // the agent that you request to perform the action
			}
			try { // Let JADE convert from Java objects to string
			 getContentManager().fillContent(msg, request); //send the wrapper object
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
}
