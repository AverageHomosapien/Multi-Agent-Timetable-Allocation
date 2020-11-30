package week2exercises;

import java.util.Arrays;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class _CreateContainer {

	public static void main(String[] args) {
		
		String[] books = {"Java for Dummies", "An Introduction to MultiAgent Systems", "Artifical Intelligence, 2nd Edition", "Horton Hears a Who", 
							"Harry Potter and the Chamber of Secrets", "Harry Potter and the Order of the Phoenix", "1984", "Animal Farm",
							"Wealth of Nations", "12 Rules for Life"};
		
		String[] sellerNames = {"Cindy", "Sarah", "Scott", "Stephen", "Samuel"};
		Object[] sellers = new Object[5];
		
		String[] buyerNames = {"Bilbo", "Barry", "Benjamin", "Boris", "Beefy"};
		Object[] buyers = new Object[5];
		
		Profile myProfile = new ProfileImpl();
		Runtime myRuntime = Runtime.instance();
		ContainerController myContainer = myRuntime.createMainContainer(myProfile);
		AgentController rma;
		try {
			rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null );
			rma.start();
						
			// Creating buyers and sellers
			for (int i = 0; i < sellerNames.length; i++) {
				sellers[i] = myContainer.createNewAgent(sellerNames[i], SellerAgent.class.getCanonicalName(), Arrays.copyOfRange(books, i*2, i*2+2));
				((AgentController) sellers[i]).start(); // Add a cast to the agent controller in sellers
				
				buyers[i] = myContainer.createNewAgent(buyerNames[i], BuyerAgent.class.getCanonicalName(), null);
				((AgentController) buyers[i]).start();
			}

			// Arnold Advertisor
			AgentController myAdvertisor = myContainer.createNewAgent("Arnold", AdvertisorAgent.class.getCanonicalName(), null);
			myAdvertisor.start();
			
			
		} catch (StaleProxyException e) {
			System.out.println("Exception starting agent: " + e.toString());
		}
	}

}
