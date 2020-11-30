package set10111.music_shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import set10111.music_shop_ontology.ECommerceOntology;
import set10111.music_shop_ontology.elements.*;

/*
 * Since I'm using example ontologies given by the university,
 * .. I kept the ontologies the same and just added on top. In reality,
 * .. I would add the name to the Item class to allow me to call the
 * .. name for printing, without having to instanceof into Single/CD
 */

public class SellerAgent extends Agent {
	private Codec codec = new SLCodec();
	private Ontology ontology = ECommerceOntology.getInstance();
	
	// Stock list - <Serial number / Item>
	private HashMap<Integer,Item> itemsForSale = new HashMap<>();
	// Item stock - <Serial number / Stock>
	private HashMap<Integer,Integer> itemsInStock = new HashMap<>();
	
	@Override
	protected void setup(){
		register();
		
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		// Initialise tracks in constructor
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
		addToCatalogue(cd);
		
		
		Single single = new Single();
		single.setName("ACDC Hot Hits");
		single.setSerialNumber(124);
		tracks = new ArrayList<>();
		t = new Track();
		t.setName("Back in Black");
		t.setDuration(255);
		tracks.add(t);
		t = new Track();
		t.setName("Black in Back");
		t.setDuration(522);
		tracks.add(t);
		single.setTracks(tracks);
		addToCatalogue(single);
		
		Book book = new Book();
		book.setName("One very dark night");
		book.setAuthor("Arthur Conan Doyle");
		book.setLength(350);
		book.setSerialNumber(125);
		addToCatalogue(book);
		
		increaseStock(123, 8); // Increase CDs in stock -- STARTS WITH 1 BY DEFAULT
		increaseStock(124, 5); // Increase Singles in stock -- STARTS WITH 1 BY DEFAULT
		increaseStock(125, 3); // Increase Books in stock -- STARTS WITH 1 BY DEFAULT
		
		addBehaviour(new PrintStockBehaviour());
		addBehaviour(new QueryBehaviour());
		addBehaviour(new SellBehaviour());	
	}
	
	// Adding to catalogue - assume that item being added means 1 in stock
	public void addToCatalogue(Item itemToAdd) {
		itemsForSale.put(itemToAdd.getSerialNumber(), itemToAdd);
		itemsInStock.put(itemToAdd.getSerialNumber(), 1);
	}
	
	// Attempts to remove an item from the catalogue - if at least 1 in stock
	public boolean decreaseStock(int serialNumber, boolean doAction) {
		if (!itemsInStock.containsKey(serialNumber)) { // Not in stock
			return false;
		}
		
		int stock = itemsInStock.get(serialNumber);
		if (stock <= 0) { // Stock isn't empty or negative
			return false;
		}
		if (doAction) { // if doAction is false - don't decrease stock
			itemsInStock.put(serialNumber, (stock-1));
		}
		return true;
	}
	
	// Adding stock 
	public boolean increaseStock(int serialNumber, int incremBy) {
		 // Checking item in the catalogue
		if (!itemsForSale.containsKey(serialNumber) || !itemsInStock.containsKey(serialNumber)){ // CHECK THIS WORKS
			return false;
		}
		
		if (incremBy <= 1) {
			itemsInStock.put(serialNumber, itemsInStock.get(serialNumber)+1);
		}
		else {
			itemsInStock.put(serialNumber, itemsInStock.get(serialNumber)+incremBy);
		}
		return true;
	}
	
	// Prints the stock of each type at the beginning of the run
	private class PrintStockBehaviour extends OneShotBehaviour {
		@Override
		public void action() {
			for (Item item : itemsForSale.values()) {
				if (item instanceof CD) {
					CD cd = (CD)item; // Extract CD
					System.out.println("I have " + itemsInStock.get(cd.getSerialNumber()) + " " + cd.getName() + " CDs in stock!");
				}
				else if (item instanceof Single) {
					Single single = (Single)item; // Extract CD
					System.out.println("I have " + itemsInStock.get(single.getSerialNumber()) + " " + single.getName() + " CDs in stock!");
				}
				else if (item instanceof Book) {
					Book book = (Book)item; // Extract CD
					System.out.println("I have " + itemsInStock.get(book.getSerialNumber()) + " " + book.getName() + " books in stock!");
				}
			}
		}
	}
	
	// When seller is queried by buyer agents
	private class QueryBehaviour extends CyclicBehaviour{
		@Override
		public void action() {
			//This behaviour should only respond to QUERY_IF messages
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF); 
			ACLMessage msg = receive(mt);
			if(msg != null){
				try {
					ContentElement ce = getContentManager().extractContent(msg);
					if (ce instanceof Owns) {
						Owns owns = (Owns) ce;
						Item item = owns.getItem();
						if (item instanceof CD) {
							CD cd = (CD)item; // Extract CD
							ACLMessage reply = msg.createReply();
							
							// Check if seller has stock
							if (decreaseStock(cd.getSerialNumber(), false)) {
								reply.setPerformative(ACLMessage.CONFIRM);
							}
							else {
								System.out.println("Sorry, CD " + cd.getName() + " is out of stock");
								reply.setPerformative(ACLMessage.DISCONFIRM);
							}
							getContentManager().fillContent(reply, owns); // adding the original owns, since it's still applicable--
							send(reply);
						}
						else if (item instanceof Single) {
							Single single = (Single)item; // Extract the single
							ACLMessage reply = msg.createReply();
							// Check if seller has stock
							if (decreaseStock(single.getSerialNumber(), false)) {
								reply.setPerformative(ACLMessage.CONFIRM);
							}
							else {
								System.out.println("Sorry, single " + single.getName() + " is out of stock");
								reply.setPerformative(ACLMessage.DISCONFIRM);
							}
							getContentManager().fillContent(reply, owns); // adding the original owns, since it's still applicable--
							send(reply);
						}
						else if (item instanceof Book) {
							Book book = (Book)item; // Extract the single
							ACLMessage reply = msg.createReply();
							// Check if seller has stock
							if (decreaseStock(book.getSerialNumber(), false)) {
								reply.setPerformative(ACLMessage.CONFIRM);
							}
							else {
								System.out.println("Sorry, book " + book.getName() + " is out of stock");
								reply.setPerformative(ACLMessage.DISCONFIRM);
							}
							getContentManager().fillContent(reply, owns); // adding the original owns, since it's still applicable--
							send(reply);
						}
						
					}
				}
				catch (CodecException ce) {
					ce.printStackTrace();
				}
				catch (OntologyException oe) {
					oe.printStackTrace();
				}
			}
			else{
				block();
			}
		}
	}
	
	// Sells the an item
	private class SellBehaviour extends CyclicBehaviour{
		@Override
		public void action() {
			//This behaviour should only respond to REQUEST messages
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST); 
			ACLMessage msg = receive(mt);
			if(msg != null){
				try {
					ContentElement ce = getContentManager().extractContent(msg);
					
					if(ce instanceof Action) {
						Concept action = ((Action)ce).getAction();
						if (action instanceof Sell) {
							Sell order = (Sell)action;
							Item item = order.getItem();
							int serial = item.getSerialNumber();
														
							// Check if item in stock before unpacking
							if (!itemsForSale.containsKey(serial)) {
								System.out.println("You tried to order something out of stock!!!! Check first!");
							}
							else if(item instanceof CD){ // Extract the CD name and print
								CD cd = (CD)item;
								System.out.println("Selling " + cd.getName() + " to " + order.getBuyer().getLocalName());
								if (decreaseStock(serial, false)) { // Check if transaction can be completed
									decreaseStock(serial, true);
								}
								System.out.println("The new stock for " + cd.getName() + " is " + itemsInStock.get(serial));
							}
							else if (item instanceof Single) { // Extract the Single name and print
								Single single = (Single) item;
								System.out.println("Selling " + single.getName() + " to " + order.getBuyer().getLocalName());
								if (decreaseStock(serial, false)) { // Check if transaction can be completed
									decreaseStock(serial, true);
								}
								System.out.println("The new stock for " + single.getName() + " is " + itemsInStock.get(serial));
							}
							else if (item instanceof Book) { // Extract the Single name and print
								Book book = (Book) item;
								System.out.println("Selling " + book.getName() + " to " + order.getBuyer().getLocalName());
								if (decreaseStock(serial, false)) { // Check if transaction can be completed
									decreaseStock(serial, true);
								}
								System.out.println("The new stock for " + book.getName() + " is " + itemsInStock.get(serial));
							}
						}
					}
				}
				catch (CodecException ce) {
					ce.printStackTrace();
				}
				catch (OntologyException oe) {
					oe.printStackTrace();
				}
			}
			else{
				block();
			}
		}
	}
	
	// Registers the agent with the Directory Finder
	protected void register() {
		DFAgentDescription dfd = new DFAgentDescription ();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("seller-agent");
		sd.setName( getLocalName() + "-seller-agent");
		dfd.addServices(sd);
		
		try{
			DFService.register(this , dfd);
		}
		catch(FIPAException e) {
			e.printStackTrace() ;
		}
	}
}
