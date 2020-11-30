package week1exercises;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import week1.SimpleAgent;


public class _runAgents {

	public static void main(String[] args) {

		Profile myProfile = new ProfileImpl();
		Runtime myRuntime = Runtime.instance();
		ContainerController myContainer = myRuntime.createMainContainer(myProfile);
		AgentController rma;
		try {
			rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null );
			rma.start();
			
			// Clarence Countdown Agent
			AgentController myAgent = myContainer.createNewAgent("Clarence", CountdownAgent.class.getCanonicalName(), null);
			myAgent.start();
			
			// Tina 'Twenty-Second' Timer Agent -- Outranks Timmy now
			AgentController myAgent2 = myContainer.createNewAgent("Tina", TimerAgent20s.class.getCanonicalName(), null);
			myAgent2.start();
			
		} catch (StaleProxyException e) {
			System.out.println("Exception starting agent: " + e.toString());
		}

	}

}
