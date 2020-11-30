package week2exercises;

import jade.core.Agent;
import jade.core.AID;


public class SellerAgent extends Agent{
	
	// Every seller will have 1 book of each kind
	int[] stock = {1,1};
	
	
	protected void setup() {
		Object[] args = getArguments();
		
		for (Object item : args) {
			System.out.println("My name is " + getAID().getLocalName() + " and have have " + item + " to sell!");
		}
		
		System.out.println("Goodbye!");
		doDelete();
	}
}
