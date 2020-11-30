package __sequentialCyclicTesting;

import java.util.ArrayList;

public class SlotsInEmpty {

	public static void main(String[] args) {
		
		ArrayList<Integer> arrayList = new ArrayList<>();
		
		for (int elem : arrayList) {
			System.out.println("ELEM IS " + elem);
		}
		System.out.println("DONE");

	}

}
