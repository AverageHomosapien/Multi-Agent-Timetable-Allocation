package week2exercises;

import jade.core.Agent;
import jade.core.AID;



public class BuyerAgent extends Agent{
	
	// Want to track number of books and the titles
	int books = 0;
	String[] bookArr = new String[2];
	
	protected void setup() {
		System.out.println("My name is " + getAID().getLocalName() + " and I'm here to buy!");
		
		System.out.println("Goodbye!");
		doDelete();
	}
	
	public void action() {
		
	}
	
}
