package week1exercises;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

// Creating Finite State Agent without using FSM
public class SleepyAgent extends Agent {

	TickerBehaviour loop;
	String[] options = new String[] {"Awake", "Dead", "Asleep", "Eating"}; // Set up array to maximise first hits (highest prob states first)
			   // total transitions:{   3   ,   3   ,    2    ,    1    };
	int currOption, nextOption, timer;
	double randval;
	
	public void setup() {
		
		currOption = 2;
		
		loop = new TickerBehaviour(this, 2000) {
			protected void onTick() {
				
				timer++;
				System.out.println(myAgent.getAID() + " is currently " + options[currOption] + " and has been alive for " + timer + " ticks");
				
				switch(options[currOption]){
				case "Awake":
					nextOption = getNextOption(4);
					break;
				case "Asleep":
					nextOption = getNextOption(3); // Transitions to Awake, Asleep, Dead
					break;
				case "Eating":
					nextOption = getNextOption(2); // Transitions to Asleep and Dead
					break;
				case "Dead":
					System.out.println("Agent is dead. God rest their soul.");
					myAgent.doDelete();
					break;
				}
				
				currOption = nextOption;
			}
		};
		
		addBehaviour(loop);
	}

	// Random a transition between the total available transitions and return to the Agent - Basically Uniform Distribution FSM
	public int getNextOption(int totalTransitions) {
		double retDoubVal = (Math.random() * totalTransitions);
		return (int) retDoubVal;
	}
}