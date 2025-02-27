package timetable_ontology.elements;

import java.util.ArrayList;

import jade.content.Concept;
import jade.content.onto.annotations.AggregateSlot;
import jade.content.onto.annotations.Slot;
import jade.core.AID;

public class TutorialGroup implements Concept {

	public TutorialGroup() {

	}
	
	// Used by main
	public TutorialGroup(Timeslot slot, String tutorialID, int tutNum) {
		this.slot = slot;
		this.tutorialID = tutorialID;
		this.setTutNum(tutNum);
	}

	
	private Timeslot slot;
	private String tutorialID; // The set code (e.g. SET10101)
	private int classSize;
	private int tutNum;

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

	@Slot (mandatory = true)
	public int getTutNum() {
		return tutNum;
	}

	public void setTutNum(int tutNum) {
		this.tutNum = tutNum;
	}
}
