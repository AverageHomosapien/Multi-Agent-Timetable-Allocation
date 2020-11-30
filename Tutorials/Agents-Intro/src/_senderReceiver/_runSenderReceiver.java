package _senderReceiver;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.core.Runtime;
	

public class _runSenderReceiver {
	public static void main(String[] args) {
		Profile myProfile = new ProfileImpl();
		Runtime runtime = Runtime.instance();
		
		// Runtime runtime creates main container, based on Profile myProfile
		ContainerController myContainer = runtime.createMainContainer(myProfile);
		
		try {
			AgentController rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
			rma.start();
			
			// Fergal Finder
			AgentController receiverAgent = myContainer.createNewAgent("Richard", ReceiverAgent.class.getCanonicalName(), null);
			receiverAgent.start(); // Start my Bobby Buyer
			
			// Richard Receiver
			AgentController senderAgent = myContainer.createNewAgent("Sammy", SenderAgent.class.getCanonicalName(), null);
			senderAgent.start(); // Start my Bobby Buyer
		
		
		}catch(Exception e) {
			System.out.println("Error was caused: " + e.toString());
		}
	}
	
}
