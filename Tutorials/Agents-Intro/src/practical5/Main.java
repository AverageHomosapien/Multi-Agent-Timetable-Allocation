package practical5;
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
			
			AgentController simulationAgent = myContainer.createNewAgent("buyer1", BuyerAgent.class.getCanonicalName(), null);
			simulationAgent.start();
			
			int numSellers = 5;
			AgentController seller;
			for(int i=0; i<numSellers; i++) {
				seller = myContainer.createNewAgent("seller" + i, SellerAgent.class.getCanonicalName(), null);
				seller.start();
			}
			
			/*
			 * int numSellers = 3;
			AgentController[] sellers = new AgentController[numSellers];
			for(int i=0; i<numSellers; i++) {
				sellers[i] = myContainer.createNewAgent("seller" + i, SellerAgent.class.getCanonicalName(), null);
				sellers[i].start();
			}
			 * 
			 */
			
			AgentController tickerAgent = myContainer.createNewAgent("ticker", BuyerSellerTicker.class.getCanonicalName(),
					null);
			tickerAgent.start();
			
		}
		catch(Exception e){
			System.out.println("Exception starting agent: " + e.toString());
		}


	}

}
