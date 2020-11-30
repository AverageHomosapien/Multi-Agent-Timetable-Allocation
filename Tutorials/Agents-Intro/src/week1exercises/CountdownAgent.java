package week1exercises;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class CountdownAgent extends Agent {
	int w = 15;
	public void setup() {
		
		// Add new Ticker Behaviour
		addBehaviour(new TickerBehaviour(this, 1000) {
			// Call every 1s - since, it's a 1s timer countdown!
			
			protected void onTick() {
				
				if (w > 0) {
					System.out.println(w + " seconds left!");
					w--;
				}else {
					System.out.println("Exiting.. Goodbye!");
					myAgent.doDelete();
				}
			}
		}); // CLOSE BEHAVIOUR
		
	}
}
