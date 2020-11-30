package __tester;

import java.util.Map;
import java.util.HashMap;

public class HashMapTesting {

	public static void main(String[] args) {
		Map<String, Integer> newMap = new HashMap<>();
		
		newMap.put("Calum", 22);
		newMap.put("Erik", 21);
		newMap.put("Andrew", 23);
		newMap.put("Alex", 20);
		

		int num = newMap.get("Peter");
		System.out.println("Num is " + num);
	}
}
