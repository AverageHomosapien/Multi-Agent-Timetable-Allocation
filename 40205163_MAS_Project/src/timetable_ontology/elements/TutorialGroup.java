package timetable_ontology.elements;

import java.util.ArrayList;

import jade.content.Concept;
import jade.content.onto.annotations.AggregateSlot;
import jade.content.onto.annotations.Slot;
import jade.core.AID;

// It can be assumed that all tutorial groups are the same size
public class TutorialGroup implements Concept {

	public TutorialGroup() {

	}
	
	// Used by main
	public TutorialGroup(Timeslot slot, String tutorialID, int classSize) {
		this.slot = slot;
		this.tutorialID = tutorialID;
		this.classSize = classSize;
	}
	
	
	private Timeslot slot;
	private String tutorialID; // The set code (e.g. SET10101)
	private int classSize;
	private ArrayList<AID> studentsPresent;

	@Slot (mandatory = true)
	public Timeslot getTimeslot() {
		return slot;
	}
	public void setTimeslot(Timeslot slot) {
		this.slot = slot;
	}
	
	@Slot (mandatory = true)
	public String getTutorialID() {
		return tutorialID;
	}	
	public void setTutorialID(String tutorialID) {
		this.tutorialID = tutorialID;
	}
	
	public int getClassSize() {
		return classSize;
	}
	public void setClassSize(int classSize) {
		this.classSize = classSize;
	}
	
	public ArrayList<AID> getStudentsPresent() {
		return studentsPresent;
	}
	public void setStudentsPresent(ArrayList<AID> studentsPresent) {
		this.studentsPresent = studentsPresent;
	}
}
