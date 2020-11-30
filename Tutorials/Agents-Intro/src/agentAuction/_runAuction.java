 package agentAuction;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

/*
 * Initial auction will only use 8 items - allows handling of items as 'single entities', rather than having duplicate CPUs etc
 * Next auction will use all items & deal with multiple items with the same name
 */
public class _runAuction {

	public static void main(String[] args) {
		
		// 400 items of 8 types, roughly 50 each

		// Passing catalogue of items to the Auctioneer
		String[][] items = readCSV();
		Object[] auctioneerArgs = new Object[1]; // Allows passing of multiple arguments (can also just pass items as an arg and use args[0]?)
		auctioneerArgs[0] = items;

		// Passing unique items to the buyer
		Set<String> itemSet = new HashSet<String>();
		for (int i = 0; i < items.length; i++) {
			itemSet.add(items[i][0]);
		}
		Object[] buyerArgs = new Object[1];
		buyerArgs[0] = itemSet;
		
		
		Profile myProfile = new ProfileImpl();
		Runtime runtime = Runtime.instance();
		
		// Runtime runtime creates main container, based on Profile myProfile
		ContainerController myContainer = runtime.createMainContainer(myProfile);
		
		try {
			AgentController rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
			rma.start();
			
			// Adam Auctioneer
			AgentController auctioneerAgent = myContainer.createNewAgent("Adam", Auctioneer.class.getCanonicalName(), auctioneerArgs);
			auctioneerAgent.start(); // Start my Bobby Buyer
			
			// Billy Buyer
			AgentController buyerAgent = myContainer.createNewAgent("Billy", Buyer.class.getCanonicalName(), buyerArgs);
			buyerAgent.start(); // Start my Bobby Buyer
		
		}catch(Exception e) {
			System.out.println("Error was caused: " + e.toString());
		}
	}
	
	// Reads the components.csv file and returns the items and prices
	public static String[][] readCSV() {
		String filepath = "src//agentAuction//components.csv";
		int lineNumber;
		
		try {
			lineNumber = countLinesNew(filepath);
		}
		catch(IOException e) {
			e.printStackTrace();
			lineNumber = 401; // Set lineNumber manually (doesn't go here)
		}
		
		String[][] readItems = new String[lineNumber-1][2];
		
		try {
			String line = "";
			String splitBy = ",";
			String[] temp = new String[3];
			
			BufferedReader br = new BufferedReader(new FileReader(filepath));// current program directory
			br.readLine(); // read & ignore the first line
			
			int ct = 0;
			while((line = br.readLine()) != null) {
				temp = line.split(splitBy);
				readItems[ct][0] = temp[1];
				readItems[ct][1] = temp[2];
				ct++;
			}
						
			br.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("ReadCSV broke");
		}
		
		return readItems;
	}
	
	// Counts the lines of a csv file
	public static int countLinesNew(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];

	        int readChars = is.read(c);
	        if (readChars == -1) {
	            // bail out if nothing to read
	            return 0;
	        }

	        // make it easy for the optimizer to tune this loop
	        int count = 0;
	        while (readChars == 1024) {
	            for (int i=0; i<1024;) {
	                if (c[i++] == '\n') {
	                    ++count;
	                }
	            }
	            readChars = is.read(c);
	        }

	        // count remaining characters
	        while (readChars != -1) {
	            System.out.println(readChars);
	            for (int i=0; i<readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	            readChars = is.read(c);
	        }

	        return count == 0 ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
}
