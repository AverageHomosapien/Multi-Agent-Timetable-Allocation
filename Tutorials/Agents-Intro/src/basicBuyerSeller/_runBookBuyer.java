package basicBuyerSeller;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.core.Runtime;

public class _runBookBuyer {
	public static void main(String[] args) {
		
		Profile myProfile = new ProfileImpl();
		Runtime runtime = Runtime.instance();
		
		// Runtime runtime creates main container, based on Profile myProfile
		ContainerController myContainer = runtime.createMainContainer(myProfile);
		
		try {
			AgentController rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
			rma.start();
			
			AgentController buyerAgent = myContainer.createNewAgent("Bobby", BookBuyerAgent.class.getCanonicalName(), null);
			buyerAgent.start(); // Start my Bobby Buyer
			
			AgentController buyerAgent2 = myContainer.createNewAgent("Benny", BookBuyerAgent.class.getCanonicalName(), null);
			buyerAgent2.start(); // Start my Sammy Seller
			
			AgentController buyerAgent3 = myContainer.createNewAgent("Bimbo", BookBuyerAgent.class.getCanonicalName(), null);
			buyerAgent3.start(); // Start my Sarah Seller
			
			AgentController sellerAgent = myContainer.createNewAgent("Stephen", BookSellerAgent.class.getCanonicalName(), null);
			sellerAgent.start(); // Start my Stephen Seller
								
		}catch(Exception e) {
			System.out.println("Error was caused: " + e.toString());
		}
	}
}

//main class: jade.Boot
//program args: -gui jade.Boot <agent nickname>:<package name>.<agent class name>