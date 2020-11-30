package __tester;

import java.util.ArrayList;
import java.util.List;


/// BUILD PATH TO JADE JAR IN MAS
/// CONFIGURATION

public class ListTesting {
	
	public static void main(String[] args) {
		List<List<String>> testDS = new ArrayList<List<String>>(5); 
		
		for (int i = 0; i < testDS.size(); i++) {
			testDS.add(new ArrayList<String>());
		}
		
		testDS.get(0).add("0, 0");
		testDS.get(1).add("0, 1");
		try {
			System.out.println(testDS.get(0));
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Can't do 1");
		}
		
		try {
			System.out.println(testDS.get(0).get(0));
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Can't do 2");
		}
		
		try {
			System.out.println(testDS.get(1));
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Can't do 3");
		}
		
		try {
			System.out.println(testDS.get(1).get(0));
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Can't do 4");
		}
	}
}
