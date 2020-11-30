package _syncedBehaviour;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.core.Runtime;
	

public class _runAgents {
	public static void main(String[] args) {
			
		Profile myProfile = new ProfileImpl();
		Runtime runtime = Runtime.instance();
		
		// Runtime runtime creates main container, based on Profile myProfile
		ContainerController myContainer = runtime.createMainContainer(myProfile);
		
		
		try {
			AgentController rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
			rma.start();
			
			// Fergal Finder
			AgentController tickerAgent = myContainer.createNewAgent("Tammy", TickerAgent.class.getCanonicalName(), null);
			tickerAgent.start(); // Start my Bobby Buyer
			
			// Richard Receiver
			AgentController simAgent = myContainer.createNewAgent("Sammy", SimpleSimulationAgent.class.getCanonicalName(), null);
			simAgent.start(); // Start my Bobby Buyer

			// Richard Receiver
			AgentController simAgent2 = myContainer.createNewAgent("Sonny", SimpleSimulationAgent.class.getCanonicalName(), null);
			simAgent2.start(); // Start my Bobby Buyer
		
		
		}catch(Exception e) {
			System.out.println("Error was caused: " + e.toString());
		}
	}
}
