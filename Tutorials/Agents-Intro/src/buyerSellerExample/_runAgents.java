package buyerSellerExample;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.core.Runtime;
	
//
//
// COPYING ACROSS THE REGISTER FUNCTION AND THEN NOT CHANGING THE NAME
// THATS WHERE ERRORS ARE BEING THROWN
//

public class _runAgents {
	public static void main(String[] args) {
			
		Profile myProfile = new ProfileImpl();
		Runtime runtime = Runtime.instance();
		
		// Runtime runtime creates main container, based on Profile myProfile
		ContainerController myContainer = runtime.createMainContainer(myProfile);
		
		
		try {
			AgentController rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
			rma.start();
			
			AgentController buyerAgent = myContainer.createNewAgent("Billy", BuyerAgent.class.getCanonicalName(), null);
			buyerAgent.start(); 
			
			AgentController buyerAgent2 = myContainer.createNewAgent("Bobby", BuyerAgent.class.getCanonicalName(), null);
			buyerAgent2.start(); 

			AgentController buyerAgent3 = myContainer.createNewAgent("Benny", BuyerAgent.class.getCanonicalName(), null);
			buyerAgent3.start(); 

			AgentController buyerAgent4 = myContainer.createNewAgent("Binge Drinker", BuyerAgent.class.getCanonicalName(), null);
			buyerAgent4.start(); 

			AgentController sellerAgent = myContainer.createNewAgent("Sleepy", SellerAgent.class.getCanonicalName(), null);
			sellerAgent.start(); 

			AgentController sellerAgent2 = myContainer.createNewAgent("Smelly", SellerAgent.class.getCanonicalName(), null);
			sellerAgent2.start(); 

			AgentController buyerSellerTicker = myContainer.createNewAgent("Bertrude", BuyerSellerTicker.class.getCanonicalName(), null);
			buyerSellerTicker.start(); 

		}catch(Exception e) {
			System.out.println("Error was caused: " + e.toString());
		}
	}
}
