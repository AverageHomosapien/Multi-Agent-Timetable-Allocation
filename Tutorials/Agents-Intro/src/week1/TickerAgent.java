package week1;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class TickerAgent extends Agent{
	
	int count = 15;
	public void setup() {
		
		addBehaviour(new TickerBehaviour(this, 1000){
			
			protected void onTick() {
				
				if (count > 0) {
					System.out.println("Time left is " + count + " seconds");
					count--;
				}else {
					System.out.println("Bye, bye");
					myAgent.doDelete();
				}
			}
			
		});
	}

}
