package __sequentialCyclicTesting;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;

public class BehaviourIfs extends Agent {
	public int randint = 0;
	
	/*	AFTER BEHAVIOUR, RANDINT IS: 0
		AFTER SECOND AFTER BEHAVIOUR, RANDINT IS: 0
		Randint is : 0 AFTER THE WHILE
		Randint is : 0 AFTER THE WHILE
		Message 1!!
		Inside MSG1 - Randint is: 0
		POST - Randint is: 1
		Message 1!!
		Inside MSG1 - Randint is: 1
		POST - Randint is: 2
	 */
	
	protected void setup() {
		addBehaviour(new CheckerBehaviour());
	}
		
	public class CheckerBehaviour extends OneShotBehaviour {
		@Override
		public void action() {
			SequentialBehaviour dailyActivity = new SequentialBehaviour();
			myAgent.addBehaviour(dailyActivity);
			
			for (int i = 0; i < 2; i++) {
				dailyActivity.addSubBehaviour(new Msg1());
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
	It runs all the code in the setup & classes and adds the behaviours to an execute to a queue. Which means that all the control loops etc are just useless because all the code just gets ignored since the behaviours can't initialise them
	*/
	public class Msg1 extends OneShotBehaviour {
		@Override
		public void action() {
			System.out.println("Message 1!!");
			randint ++;
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
			System.out.println("in message 2, randint is: " + randint);
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
