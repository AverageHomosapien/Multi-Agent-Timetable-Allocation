package week1exercises;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

// Counts to a minute and then kills itself
public class TimerAgent extends Agent {

	int seconds = 0;
	
	// Setup Agent
	public void setup() {
		
		addBehaviour(new TickerBehaviour(this, 1000) {
			
			protected void onTick() {
				seconds++;
				System.out.println("The current time is " + seconds);
				
				if (seconds >= 60) {
					System.out.println("We has counted to 60s. Bye bye boys!");
					myAgent.doDelete();
				}
			}
		});			
	}
}
