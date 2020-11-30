package _basicFindersAndReceivers;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;


public class UnfoundAgent extends Agent{
	
	int timeLeft = 60;
	protected void setup() {
		
		addBehaviour(new TickerBehaviour(this, 1000) {
			protected void onTick() {
				if (timeLeft > 0) {
					System.out.println(timeLeft + " seconds of " + getAID().getName() + " left.");
					timeLeft-= 1;
				}else {
					System.out.println("Bye bye");
					myAgent.doDelete();
				}
			}
		});
	}
}
