package __sequentialCyclicTesting;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;

public class ReturnBehaviourBehaviour extends Agent{
	private AID receiverAgent;
	private int called = 0;
	
	protected void setup() {
		DoThisBehaviour behaviour = new DoThisBehaviour();
		for (int i = 0; i < 3; i++) {
			addBehaviour(behaviour);
			int num = behaviour.onEnd();
			System.out.println("num is " + num);
		}
	}

	// Sends message to receiver to check duplicate cyclic behaviour
	public class DoThisBehaviour extends OneShotBehaviour {		
		public void action() {
			System.out.println("Action done");
			System.out.println("***********");
		}

		public int onEnd() {
			called++;
			return called;
		}
	}
}
