package timetable_ontology.elements;

import java.util.ArrayList;

import jade.content.Predicate;
import jade.content.onto.annotations.Slot;

public class SlotsRequested implements Predicate{

	private ArrayList<SwapFinal> slots;
	
	@Slot (mandatory = true)
	public ArrayList<SwapFinal> getSlots() {
		return slots;
	}
	
	public void setSlots(ArrayList<SwapFinal> slots) {
		this.slots = slots;
	}
	
	public void addSlot(SwapFinal slot) {
		this.slots.add(slot);
	}
}
