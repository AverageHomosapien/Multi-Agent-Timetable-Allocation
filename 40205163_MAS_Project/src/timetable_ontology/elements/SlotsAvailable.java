package timetable_ontology.elements;

import jade.content.Predicate;
import jade.content.onto.annotations.Slot;
import jade.core.AID;

public class SlotsAvailable implements Predicate {
	
	private MessageBoard board;
	
	@Slot (mandatory = true)
	public MessageBoard getBoard() {
		return board;
	}
	public void setBoard(MessageBoard board) {
		this.board = board;
	}
}
