package __sequentialCyclicTesting;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;

public class TimedSubBehaviour extends Agent {
	
	protected void setup() {
		addBehaviour(new CheckerBehaviour());
	}
	
	/*	ADDSUBBEHAVIOUR MSG 1
		Message 1!!
		Message 2!! -- Initiates second behaviour before it goes on to sub-behaviours of behaviour 1
		Message 4!!
		Message 3!!
		Message 2!!
		Message 3!!
		Message 4!!
	 */
	
	// After calling a sub-behaviour, the first sub-behaviour is always called before any sub-behaviour behaviours are called
	/* 
	 * AddSubBehaviour Msg1 inside of i<2 loop
	 * 	Message 1!!  // Msg1 Called
		Message 2!!  // Msg2 Called after 1
		Message 6!!  // Msg1 subroutine continues
		Message 4!!  // ..
		Message 5!!  // Msg2 subroutine continues
		Message 3!!  // ..
		Message 3!!
		Message 4!!
		Message 1!!  // Same again
		Message 2!!
		Message 6!!
		Message 4!!
		Message 5!!
		Message 3!!
		Message 3!!
		Message 4!!
	 */
		
	/*  AddSubBehaviour Msg1 outside of i<2 loop
	 * 	Message 1!!
		Message 2!! // Msg2 Called after 1
		Message 6!! // Msg1 subroutine continues
		Message 4!! // ..
		Message 5!! // Msg2 subroutine continues
		Message 3!! // ..
		Message 3!! // Msg3
		Message 4!! // Msg4
		Message 2!! // Msg2 Called
		Message 3!! // Msg3 Called
		Message 5!! // Msg2 subroutine continues
		Message 3!! // ..
		Message 4!! // Msg4 Called
	 */
	
	// MESSAGES ARE ADDED TO THE END OF THE QUEUE OF BEHAVIOURS
	/*	AddBehaviour all inside loop
	 *  Message 1!! 1-4
		Message 2!!
		Message 3!!
		Message 4!!
		Message 1!! 1-4 again
		Message 2!!
		Message 3!!
		Message 4!!
		Message 6!! Msg1 subroutine continues
		Message 4!! ..
		Message 5!! Msg2 subroutine continues
		Message 3!! ..
		Message 6!! Msg1 subroutine continues
		Message 4!! ..
		Message 5!! Msg2 subroutine continues
		Message 3!! ..
	 */
		
	public class CheckerBehaviour extends OneShotBehaviour {

		@Override
		public void action() {
			
			SequentialBehaviour dailyActivity = new SequentialBehaviour();
			myAgent.addBehaviour(dailyActivity);
			//sub-behaviours will execute in the order they are added
			for (int i = 0; i < 1; i++) {
				dailyActivity.addSubBehaviour(new Msg1());
				dailyActivity.addSubBehaviour(new EmptyBehaviour()); // Empty behaviour added after sub-behaviour if you want Msg1 to break into behaviour routine
				dailyActivity.addSubBehaviour(new Msg2());
				dailyActivity.addSubBehaviour(new EmptyBehaviour()); // Another empty behaviour added because 2 is now no longer directly called after 1 - same issue
				dailyActivity.addSubBehaviour(new Msg3());			//  .. in the sense that it calls 3 before calling sub-behaviours
				dailyActivity.addSubBehaviour(new Msg4());
			}
		}
	}
	
	/*
	public class Msg1 extends OneShotBehaviour {
		@Override
		public void action() {
			System.out.println("Message 1!!");
			SequentialBehaviour yetAnotherDailyActivity = new SequentialBehaviour();
			myAgent.addBehaviour(yetAnotherDailyActivity);
			yetAnotherDailyActivity.addSubBehaviour(new Msg6()); // Extremely weird calling pattern
			yetAnotherDailyActivity.addSubBehaviour(new Msg4());
			//addBehaviour(new Msg6());
			//addBehaviour(new Msg4());
		}
	}
	*/
	public class Msg1 extends OneShotBehaviour {
		@Override
		public void action() {
			System.out.println("Message 1!!");
			addBehaviour(new Msg6());
			addBehaviour(new Msg5());
		}
	}
	
	public class EmptyBehaviour extends OneShotBehaviour {
		public void action() {}
	}
	
	public class Msg2 extends OneShotBehaviour {
		@Override
		public void action() {
			System.out.println("Message 2!!");
			addBehaviour(new Msg5());
			addBehaviour(new Msg6());
		}
	}
	
	public class Msg3 extends OneShotBehaviour {
		@Override
		public void action() {
			System.out.println("Message 3!!");
		}
	}
	
	// Once cyclic sub-behaviour stops - it just stops going
	public class Msg4 extends OneShotBehaviour {
		@Override
		public void action() {
			System.out.println("Message 4!!");
		}
	}
	
	// Once cyclic sub-behaviour stops - it just stops going
	public class Msg5 extends OneShotBehaviour {
		@Override
		public void action() {
			System.out.println("Message 5!!");
		}
	}
	
	// Once cyclic sub-behaviour stops - it just stops going
	public class Msg6 extends OneShotBehaviour {
		@Override
		public void action() {
			System.out.println("Message 6!!");
		}
	}
}
