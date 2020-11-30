package practical2;

import java.util.HashMap;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.core.Runtime;
	
// Skipped the marketplace extension work. Have 11 working agents buying the cheapest book they can from 5 sellers

public class _runAgents {
	public static void main(String[] args) {
			
		Profile myProfile = new ProfileImpl();
		Runtime runtime = Runtime.instance();
		
		// Runtime runtime creates main container, based on Profile myProfile
		ContainerController myContainer = runtime.createMainContainer(myProfile);
				
		// Books and bookprices are length 11
		String[] bookList = new String[] {"Harry Potter and the Philosophers Stone", "Harry Potter and the Chamber of Secrets",
				"Harry Potter and the Chamber of Secrets", "Harry Potter and the Order of the Phoenix", "Harry Potter and the Goblet of Fire",
				"Harry Potter and the Half-Blood Price", "Harry Potter and the Deathly Hallows", "Forest Gump", "Lord of the Rings",
				"Lord of the Flies", "Where's Wally"};
		int[] bookPrices = new int[] {10, 8, 9, 10, 12, 12, 16, 7, 12, 6, 5};
		
		// Should have buyer names & seller names as seperate array
		
		Object[] buyerArgs = new Object[] {bookList[0]};
		Object[] buyerArgs1 = new Object[] {bookList[1]};
		Object[] buyerArgs2 = new Object[] {bookList[2]};
		Object[] buyerArgs3 = new Object[] {bookList[3]};
		Object[] buyerArgs4 = new Object[] {bookList[4]};
		Object[] buyerArgs5 = new Object[] {bookList[5]};
		Object[] buyerArgs6 = new Object[] {bookList[6]};
		Object[] buyerArgs7 = new Object[] {bookList[7]};
		Object[] buyerArgs8 = new Object[] {bookList[8]};
		Object[] buyerArgs9 = new Object[] {bookList[9]};
		Object[] buyerArgs10 = new Object[] {bookList[10]};
				
		
		// Declaring seller arguments
		String[] seller1books = new String[] {bookList[1], bookList[3], bookList[5], bookList[7]};
		String[] seller2books = new String[] {bookList[0], bookList[2], bookList[4], bookList[6]};
		String[] seller3books = new String[] {bookList[5], bookList[7], bookList[9], bookList[10]};
		String[] seller4books = new String[] {bookList[4], bookList[6], bookList[8], bookList[10]};
		String[] seller5books = new String[] {bookList[0], bookList[2], bookList[3], bookList[5]};
		
		int[] seller1prices = new int[] {bookPrices[1]-1, bookPrices[3], bookPrices[5]-3, bookPrices[7]+1};
		int[] seller2prices = new int[] {bookPrices[0], bookPrices[2]-1, bookPrices[4]-1, bookPrices[6]-2};
		int[] seller3prices = new int[] {bookPrices[5], bookPrices[7], bookPrices[9]+2, bookPrices[10]-1};
		int[] seller4prices = new int[] {bookPrices[4], bookPrices[6], bookPrices[8]-1, bookPrices[10]};
		int[] seller5prices = new int[] {bookPrices[0], bookPrices[2], bookPrices[3]-1, bookPrices[5]};
		
		Object[] seller1args = new Object[]{seller1books, seller1prices};
		Object[] seller2args = new Object[]{seller2books, seller2prices};
		Object[] seller3args = new Object[]{seller3books, seller3prices};
		Object[] seller4args = new Object[]{seller4books, seller4prices};
		Object[] seller5args = new Object[]{seller5books, seller5prices};
		
		
		try {
			AgentController rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
			rma.start();
			
			////////////////////////// Buyers //////////////////////////
			// Bobby Buyer
			AgentController buyerAgent = myContainer.createNewAgent("Bobby", BookBuyerAgent.class.getCanonicalName(), buyerArgs);
			buyerAgent.start(); // Start my Bobby Buyer
			
			// Billy Joe Buyer
			AgentController buyerAgent2 = myContainer.createNewAgent("Billy Joe", BookBuyerAgent.class.getCanonicalName(), buyerArgs1);
			buyerAgent2.start(); // Start my Bobby Buyer
			
			// Bobby Buyer
			AgentController buyerAgent3 = myContainer.createNewAgent("Bash Bandicoot", BookBuyerAgent.class.getCanonicalName(), buyerArgs2);
			buyerAgent3.start(); // Start my Bobby Buyer
			
			// Billy Joe Buyer
			AgentController buyerAgent4 = myContainer.createNewAgent("Borat", BookBuyerAgent.class.getCanonicalName(), buyerArgs3);
			buyerAgent4.start(); // Start my Bobby Buyer

			// Bobby Buyer
			AgentController buyerAgent5 = myContainer.createNewAgent("Big L", BookBuyerAgent.class.getCanonicalName(), buyerArgs4);
			buyerAgent5.start(); // Start my Bobby Buyer
			
			// Billy Joe Buyer
			AgentController buyerAgent6 = myContainer.createNewAgent("Billy Joe Jr", BookBuyerAgent.class.getCanonicalName(), buyerArgs5);
			buyerAgent6.start(); // Start my Bobby Buyer

			// Bobby Buyer
			AgentController buyerAgent7 = myContainer.createNewAgent("Benny", BookBuyerAgent.class.getCanonicalName(), buyerArgs6);
			buyerAgent7.start(); // Start my Bobby Buyer
			
			// Billy Joe Buyer
			AgentController buyerAgent8 = myContainer.createNewAgent("Benjamin", BookBuyerAgent.class.getCanonicalName(), buyerArgs7);
			buyerAgent8.start(); // Start my Bobby Buyer
			
			// Bobby Buyer
			AgentController buyerAgent9 = myContainer.createNewAgent("Biff", BookBuyerAgent.class.getCanonicalName(), buyerArgs8);
			buyerAgent9.start(); // Start my Bobby Buyer
			
			// Billy Joe Buyer
			AgentController buyerAgent10 = myContainer.createNewAgent("Balsy", BookBuyerAgent.class.getCanonicalName(), buyerArgs9);
			buyerAgent10.start(); // Start my Bobby Buyer
			
			// Bobby Buyer
			AgentController buyerAgent11 = myContainer.createNewAgent("Bender McAlister", BookBuyerAgent.class.getCanonicalName(), buyerArgs10);
			buyerAgent11.start(); // Start my Bobby Buyer
			
			////////////////////////// Sellers //////////////////////////
			// Sammy Sender
			AgentController sellerAgent = myContainer.createNewAgent("Sammy", BookSellerAgent.class.getCanonicalName(), seller1args);
			sellerAgent.start(); // Start my Bobby Buyer
			
			// Sammy Sender
			AgentController sellerAgent2 = myContainer.createNewAgent("Slemmy", BookSellerAgent.class.getCanonicalName(), seller2args);
			sellerAgent2.start(); // Start my Bobby Buyer
			
			// Sammy Sender
			AgentController sellerAgent3 = myContainer.createNewAgent("Sonny", BookSellerAgent.class.getCanonicalName(), seller3args);
			sellerAgent3.start(); // Start my Bobby Buyer
			
			// Sammy Sender
			AgentController sellerAgent4 = myContainer.createNewAgent("Sarah", BookSellerAgent.class.getCanonicalName(), seller4args);
			sellerAgent4.start(); // Start my Bobby Buyer
			
			// Sammy Sender
			AgentController sellerAgent5 = myContainer.createNewAgent("Santa", BookSellerAgent.class.getCanonicalName(), seller5args);
			sellerAgent5.start(); // Start my Bobby Buyer
			
			
		}catch(Exception e) {
			System.out.println("Error was caused: " + e.toString());
			System.out.println("errrrrror here");
		}
	}
}
