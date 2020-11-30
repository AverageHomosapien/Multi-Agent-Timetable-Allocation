package timetable_ontology.elements;

import java.util.ArrayList;

import jade.content.AgentAction;
import jade.content.onto.annotations.Slot;

// HappyWith is breaking ontology
public class HappyWith implements AgentAction{

	private ArrayList<SwapFinal> slots;

	@Slot (mandatory = true)
	public ArrayList<SwapFinal> getSlots() {
		return slots;
	}

	public void setSlots(ArrayList<SwapFinal> slots) {
		this.slots = slots;
	}
	
	public void addSlots(SwapFinal slots) {
		this.slots.add(slots);
	}
}
