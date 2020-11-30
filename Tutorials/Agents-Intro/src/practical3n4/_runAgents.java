package practical3n4;

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
		
		/// SELLER PARAMETERS ///
		// Name, starting price, minimum price to accept, new/second-hand (buyers can't see min price to accept)
		int totalItems = 4;
		Object[] item1 = {"bike", 60, 160, "new"};
		Object[] item2 = {"motorbike", 300, 600, "second-hand"};
		Object[] item3 = {"mini cooper", 1000, 1800, "second-hand"};
		Object[] item4 = {"pogo stick", 40, 100, "new"};
		
		//Object[] auctionItems = {item1, item2, item3, item4};
		
		Object[][] auctionItems = new Object[totalItems][item1.length];
		
		for (int i = 0; i < item1.length; i++) {
			auctionItems[0][i] = item1[i];
			auctionItems[1][i] = item2[i];
			auctionItems[2][i] = item3[i];
			auctionItems[3][i] = item4[i];
		}
				
		/// BUYER PARAMETERS ///
		
		// Auctions joined - 3, 2, 2, 1 = (8 total)
		// Looking for behaviour 
		String[] buyerNames = {"Billy", "Brianna", "Benny", "Baldrick", "Batman", "Robin", "Joker", "Bart Simpson"};
		
		// Item, MaxPreparedToPay
		Object[] buyer1 = {item1[0], 200};
		Object[] buyer2 = {item1[0], 240};
		Object[] buyer3 = {item1[0], 180};
		
		Object[] buyer4 = {item2[0], 700};
		Object[] buyer5 = {item2[0], 600};
		
		Object[] buyer6 = {item3[0], 2000};
		Object[] buyer7 = {item3[0], 2600};
		
		Object[] buyer8 = {item4[0], 180};
		
		
		Object[] auctioneerArgs = {auctionItems};
		
		try {
			AgentController rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
			rma.start();
			
			// Auctioneer
			AgentController auctionAgent = myContainer.createNewAgent("Andy", AuctioneerAgent.class.getCanonicalName(), auctionItems);
			auctionAgent.start();
			
			// Buyers
			AgentController buyerAgent = myContainer.createNewAgent(buyerNames[0], BuyerAgent.class.getCanonicalName(), buyer1);
			buyerAgent.start();
			AgentController buyerAgent2 = myContainer.createNewAgent(buyerNames[1], BuyerAgent.class.getCanonicalName(), buyer2);
			buyerAgent2.start();
			AgentController buyerAgent3 = myContainer.createNewAgent(buyerNames[2], BuyerAgent.class.getCanonicalName(), buyer3);
			buyerAgent3.start();
			AgentController buyerAgent4 = myContainer.createNewAgent(buyerNames[3], BuyerAgent.class.getCanonicalName(), buyer4);
			buyerAgent4.start();
			AgentController buyerAgent5 = myContainer.createNewAgent(buyerNames[4], BuyerAgent.class.getCanonicalName(), buyer5);
			buyerAgent5.start();
			AgentController buyerAgent6 = myContainer.createNewAgent(buyerNames[5], BuyerAgent.class.getCanonicalName(), buyer6);
			buyerAgent6.start();
			AgentController buyerAgent7 = myContainer.createNewAgent(buyerNames[6], BuyerAgent.class.getCanonicalName(), buyer7);
			buyerAgent7.start();
			AgentController buyerAgent8 = myContainer.createNewAgent(buyerNames[7], BuyerAgent.class.getCanonicalName(), buyer8);
			buyerAgent8.start();
		
		
		}catch(Exception e) {
			System.out.println("Error was caused: " + e.toString());
		}
	}
}
