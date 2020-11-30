package set10111.music_shop;

import java.util.ArrayList;

import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import set10111.music_shop_ontology.ECommerceOntology;
import set10111.music_shop_ontology.elements.CD;
import set10111.music_shop_ontology.elements.Sell;
import set10111.music_shop_ontology.elements.Track;

public class SeqNewSender extends Agent{
	private Codec codec = new SLCodec();
	private Ontology ontology = ECommerceOntology.getInstance();
	private AID receiverAgent;
	
	protected void setup() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());

		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
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
			for (int i = 0; i < 3; i++) {
				myAgent.addBehaviour(new Msg3());
				System.out.println("Loop number " + i + " is over");
			}
		}
	}
	
	public class Msg3 extends OneShotBehaviour {
		@Override
		public void action() {
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
			sell.setBuyer(receiverAgent);
			
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST); // Set request (ACTION)
			msg.setLanguage(codec.getName()); // Set codec
			msg.setOntology(ontology.getName()); // Set ontology
			msg.addReceiver(receiverAgent); // Set receiver -- USUALLY FOR LOOP
			
			Action request = new Action(); // Need to wrap the request in an action
			request.setAction(sell); // NEED TO SET ACTION
			request.setActor(receiverAgent); // NEED TO SET ACTOR -- USUALLY FOR LOOP
			
			try {
				// Let JADE convert from Java objects to string
				getContentManager().fillContent(msg, request);
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
