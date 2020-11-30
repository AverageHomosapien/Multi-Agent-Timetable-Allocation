package __sequentialCyclicTesting;
import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class Main {

	public static void main(String[] args) {
		Profile myProfile = new ProfileImpl();
		Runtime myRuntime = Runtime.instance();
		try{
			ContainerController myContainer = myRuntime.createMainContainer(myProfile);	
			AgentController rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
			rma.start();
			
			
			// Checking the order that subbehaviours are carried out
			//AgentController simulationAgent = myContainer.createNewAgent("sender", BehaviourIfs.class.getCanonicalName(), null);
			//simulationAgent.start();
			
			// Returns values after the running of the agent
			//AgentController simulationAgent = myContainer.createNewAgent("sender", ReturnBehaviourBehaviour.class.getCanonicalName(), null);
			//simulationAgent.start();
			
			// Allows cyclic behaviour in the running of Sequential behaviours
			//AgentController simulationAgent = myContainer.createNewAgent("sender", SeqNewSender.class.getCanonicalName(), null);
			//simulationAgent.start();
			
			//AgentController receiverAgent = myContainer.createNewAgent("receiver", Receiver.class.getCanonicalName(), null);
			//receiverAgent.start();
			
			Object[] passerArgs = new Object[4];
			
			for (int i = 0; i < (passerArgs.length-1); i++) {
				passerArgs[i] = "Hello World";
			}
			
			AgentController passerAgent = myContainer.createNewAgent("passer", PassingArgs.class.getCanonicalName(), passerArgs);
			passerAgent.start();
		}
		catch(Exception e){
			System.out.println("Exception starting agent: " + e.toString());
		}
	}
}
