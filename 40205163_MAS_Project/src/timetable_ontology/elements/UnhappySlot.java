package timetable_ontology.elements;

import jade.content.AgentAction;

public class UnhappySlot implements AgentAction {
	
	private SwapInitial slotToSwap;

	public SwapInitial getSlotToSwap() {
		return slotToSwap;
	}

	public void setSlotToSwap(SwapInitial slotToSwap) {
		this.slotToSwap = slotToSwap;
	}
}
