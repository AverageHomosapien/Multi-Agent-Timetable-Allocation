package week1;
// https://www.wikihow.com/Add-JARs-to-Project-Build-Paths-in-Eclipse-(Java)

import jade.core.Agent;


public class SimpleAgent extends Agent{

	protected void setup() {
		System.out.println("Hello! Agent "+getAID().getName()+" is ready.");
	}
}
