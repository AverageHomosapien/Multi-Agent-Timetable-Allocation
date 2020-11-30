package week1exercises;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;

public class TimerAgent20s extends Agent{
	
	int timer = 0;
	double randVal;
	Behaviour loop;
	Behaviour loop2;
	
	public void setup() {
		
		loop = new TickerBehaviour(this, 1000) {
			protected void onTick() {
				
				randVal = Math.random();
				System.out.println("Tick. " + timer + " second(s) have passed");
				timer++;
				
				if (randVal < 0.05) {
					System.out.println("Randomed number is " + randVal);
					System.out.println("Agent is lazy. Going to sleep");
					myAgent.doDelete();
				}else if (timer > 20) {
					System.out.println("Timer has reached 20! Bye bye cruel world..");
					myAgent.doDelete();
				}
			} 
		};
		
		loop2 = new TickerBehaviour(this, 10000){
			public void onTick() {
				System.out.println("My name is " + myAgent.getAID() + " and 10 seconds have passed!");
			}
		};
		
		// Another way of adding a behaviour - customize behaviour and then add
		addBehaviour(loop);
		addBehaviour(loop2);
	}

}
