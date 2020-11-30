package week1;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class _AgentOfEachType {

	public static void main(String[] args) {
		Profile myProfile = new ProfileImpl();
		Runtime myRuntime = Runtime.instance();
		ContainerController myContainer = myRuntime.createMainContainer(myProfile);
		AgentController rma;
		try {
			rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null );
			rma.start();
			
			// Simple Agent
			AgentController myAgent = myContainer.createNewAgent("Danny", SimpleAgent.class.getCanonicalName(), null);
			myAgent.start();
			
			// Timer Agent
			AgentController myAgent1 = myContainer.createNewAgent("Fanny", TimerAgent.class.getCanonicalName(), null);
			myAgent1.start();
			
		} catch (StaleProxyException e) {
			System.out.println("Exception starting agent: " + e.toString());
		}
		
	}
}

//main class: jade.Boot
//program args: -gui jade.Boot <agent nickname>:<package name>.<agent class name>
