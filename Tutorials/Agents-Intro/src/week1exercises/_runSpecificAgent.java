package week1exercises;
import examples.behaviours.FSMAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import week1.SimpleAgent;


public class _runSpecificAgent {

	public static void main(String[] args) {

		Profile myProfile = new ProfileImpl();
		Runtime myRuntime = Runtime.instance();
		ContainerController myContainer = myRuntime.createMainContainer(myProfile);
		AgentController rma;
		try {
			rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null );
			rma.start();
			
			// Sandy the Sleepy Agent
			//AgentController myAgent = myContainer.createNewAgent("Sandy", FSMAgent.class.getCanonicalName(), null);
			AgentController myAgent = myContainer.createNewAgent("Sandy", SleepyFSMAgent.class.getCanonicalName(), null);
			myAgent.start();
			
			// Sarah the Sleepy Agent
			//AgentController myAgent1 = myContainer.createNewAgent("Sarah", SleepyFSMAgent.class.getCanonicalName(), null);
			//myAgent1.start();
			
		} catch (StaleProxyException e) {
			System.out.println("Exception starting agent: " + e.toString());
		}

	}

}
