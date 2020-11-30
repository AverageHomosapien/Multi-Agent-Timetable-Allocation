package __sequentialCyclicTesting;

import jade.core.Agent;

public class PassingArgs extends Agent {

	
	protected void setup() {
		
		Object[] arguments = getArguments();
		System.out.println("Arg 0 is " + arguments[0]);
		System.out.println("Arg length is " + arguments.length);
		
		for (Object arg : arguments) {
			if (arg != null) {
				System.out.println(arg);
			}
			else {
				System.out.println("Null");
			}
		}
		//SlotPreferences = (int[][]) arguments[0];
	}
	
}
