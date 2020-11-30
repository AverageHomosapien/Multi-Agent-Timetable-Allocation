package set10111.music_shop;

import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
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
import set10111.music_shop_ontology.ECommerceOntology;
import set10111.music_shop_ontology.elements.Sell;

public class Receiver extends Agent{
	private AID senderAgent;	
	private Codec codec = new SLCodec();
	private Ontology ontology = ECommerceOntology.getInstance();
	
	protected void setup() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());

		getContentManager().registerLanguage(codec); // Need to ensure you register codec
		getContentManager().registerOntology(ontology); // Need to ensure you register codec
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("receiver-agent");
		sd.setName(getLocalName() + "-receiver-agent");
		dfd.addServices(sd);
		
		try{
			DFService.register(this , dfd);
		}
		catch(FIPAException e) {
			e.printStackTrace() ;
		}
		addBehaviour(new ReceiverBehaviour());
	}
	
	// Receiver Behaviour
	public class ReceiverBehaviour extends CyclicBehaviour {
		@Override
		public void action() {
			ACLMessage msg = myAgent.receive(); 
			if (msg != null) {
				try {
					ContentElement ce = getContentManager().extractContent(msg);
					System.out.println("Extracted content: " + msg.getContent());
					if (ce instanceof Action) {
						System.out.println("INSTANCE OF ACTION");
						Concept sellRQ = ((Action)ce).getAction(); // Casting action to Sell concept
						System.out.println("sellRQ is: " + sellRQ);
						if (sellRQ instanceof Sell) {
							System.out.println("MESSAGE IS INSTANCE OF SELL");
							System.out.println("Message is:" + sellRQ);
							System.out.println("buyer is " + ((Sell) sellRQ).getBuyer().getName());
							System.out.println("item serial is " + ((Sell) sellRQ).getItem().getSerialNumber());
							ACLMessage reply = msg.createReply();
							reply.setContent("aye");
							reply.setPerformative(ACLMessage.CONFIRM);
							doWait(2000);
							send(reply);
							System.out.println("It's an AYE for message: " + msg.getContent());
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
}

