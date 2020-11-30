package week1;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class _TenSimpleAgents {

	public static void main(String[] args) {
		Profile myProfile = new ProfileImpl();
		Runtime myRuntime = Runtime.instance();
		ContainerController myContainer = myRuntime.createMainContainer(myProfile);
		AgentController rma;
		try {
			rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null );
			rma.start();
			
			AgentController myAgent = myContainer.createNewAgent("Danny", SimpleAgent.class.getCanonicalName(), null);
			myAgent.start();
			AgentController myAgent1 = myContainer.createNewAgent("Fanny", SimpleAgent.class.getCanonicalName(), null);
			myAgent1.start();
			AgentController myAgent2 = myContainer.createNewAgent("Fred", SimpleAgent.class.getCanonicalName(), null);
			myAgent2.start();
			AgentController myAgent3 = myContainer.createNewAgent("George", SimpleAgent.class.getCanonicalName(), null);
			myAgent3.start();
			AgentController myAgent4 = myContainer.createNewAgent("Peter", SimpleAgent.class.getCanonicalName(), null);
			myAgent4.start();
			AgentController myAgent5 = myContainer.createNewAgent("Patrick", SimpleAgent.class.getCanonicalName(), null);
			myAgent5.start();
			AgentController myAgent6 = myContainer.createNewAgent("Eugene", SimpleAgent.class.getCanonicalName(), null);
			myAgent6.start();
			AgentController myAgent7 = myContainer.createNewAgent("Harry", SimpleAgent.class.getCanonicalName(), null);
			myAgent7.start();
			AgentController myAgent8 = myContainer.createNewAgent("A Pimp Called Slickback", SimpleAgent.class.getCanonicalName(), null);
			myAgent8.start();
			AgentController myAgent9 = myContainer.createNewAgent("Grandpa", SimpleAgent.class.getCanonicalName(), null);
			myAgent9.start();
			
		} catch (StaleProxyException e) {
			System.out.println("Exception starting agent: " + e.toString());
		}
		
	}
}

//main class: jade.Boot
//program args: -gui jade.Boot <agent nickname>:<package name>.<agent class name>
