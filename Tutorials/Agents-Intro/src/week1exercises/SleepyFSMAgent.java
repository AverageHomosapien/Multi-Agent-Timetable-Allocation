package week1exercises;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.FSMBehaviour;


public class SleepyFSMAgent extends Agent{
	
	/*  Allowable transactions are:
		AWAKE->ASLEEP
		AWAKE->EATING
		AWAKE->AWAKE
		EATING->AWAKE
		ASLEEP->AWAKE
		ASLEEP->ASLEEP
		ASLEEP-> DEAD
		AWAKE->DEAD
		EATING->DEAD
		The default state is ASLEEP, the final state is DEAD. Where multiple transitions exist
		from a state, select one at random as in the FSMAgent example.
	 */
	
	public static final String State_A = "Awake";
	public static final String State_B = "Asleep";
	public static final String State_C = "Eating"; // Default transition will be to awake. Unfair to kill someone if they're eating
	public static final String State_D = "Dead";
	
	public void setup() {
		FSMBehaviour fsm = new FSMBehaviour(this) {
			public int onEnd() {
				System.out.println("FSM behaviour completed.");
				myAgent.doDelete();
				return super.onEnd();
			}
		};

		// Register state A (first state)
		fsm.registerFirstState(new RandomGenerator(4), State_A);
		fsm.registerState(new RandomGenerator(3), State_B);
		fsm.registerState(new NamePrinter(), State_C);
		fsm.registerLastState(new NamePrinter(), State_D);
		
		fsm.registerTransition(State_A, State_A, 0);
		fsm.registerTransition(State_A, State_B, 1);
		fsm.registerTransition(State_A, State_C, 2);
		fsm.registerTransition(State_A, State_D, 3);
		
		fsm.registerTransition(State_B, State_A, 0);
		fsm.registerTransition(State_B, State_B, 1);
		fsm.registerTransition(State_B, State_D, 2);
		
		fsm.registerDefaultTransition(State_C, State_A);
		
		
		/* - Basic FSM Example
		fsm.registerFirstState(new NamePrinter(), State_A);
		fsm.registerFirstState(new RandomGenerator(3), State_B);
		fsm.registerFirstState(new RandomGenerator(2), State_C);
		fsm.registerLastState(new NamePrinter(), State_D);
		
		fsm.registerDefaultTransition(State_A, State_B);
		fsm.registerTransition(State_B, State_A, 0);
		fsm.registerTransition(State_B, State_B, 1);
		fsm.registerTransition(State_B, State_C, 2);
		fsm.registerTransition(State_C, State_B, 0);
		fsm.registerTransition(State_C, State_D, 1);
		*/
		
		addBehaviour(fsm);
	}
	
	
	/**
	 * COPIED FROM FSM EXAMPLE
	   Inner class NamePrinter.
	   This behaviour just prints its name
	 */
	private class NamePrinter extends OneShotBehaviour {
		public void action() {
			System.out.println("Executing behaviour "+getBehaviourName());
		}
	}
	
	/**
	 * COPIED FROM FSM EXAMPLE
	   Inner class RandomGenerator.
	   This behaviour prints its name and exits with a random value
	   between 0 and a given integer value
	 */
	private class RandomGenerator extends NamePrinter {
		private int maxExitValue;
		private int exitValue;
		
		private RandomGenerator(int max) {
			super();
			maxExitValue = max;
		}
		
		public void action() {
			System.out.println("Executing behaviour "+getBehaviourName());
			exitValue = (int) (Math.random() * maxExitValue);
			System.out.println("Exit value is "+exitValue);
		}
		
		public int onEnd() {
			return exitValue;
		}
	}
}
