package timetable_ontology.elements;

import java.util.ArrayList;
import java.util.List;
import jade.content.Concept;
import jade.content.onto.annotations.Slot;

public class MessageBoard implements Concept {
	
	private List<SwapInitial> messageBoard = new ArrayList<SwapInitial>();
	
	// try to set messageboard
	public void setMessageBoard(List<SwapInitial> message) {
		messageBoard = message;
	}
	
	public List<SwapInitial> getMessageBoard() {
		return messageBoard;
	}
	
	public void addToMessageBoard(SwapInitial message) {
		this.messageBoard.add(message);
	}
	
	public void removeFromMessageBoard(int index) {
		messageBoard.remove(index);
	}
}
