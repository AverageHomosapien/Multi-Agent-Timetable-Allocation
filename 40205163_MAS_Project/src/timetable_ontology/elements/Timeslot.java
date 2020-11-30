/**
 * 
 */
package timetable_ontology.elements;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;


public class Timeslot implements Concept {
	
	public Timeslot() {

	}
	
	// Used by main
	public Timeslot(int day, int time) {
		this.day = day;
		this.time = time;
	}
	
	private int day;
	private int time;
	
	@Slot (mandatory = true)
	public int getDay() {
		return day;
	}
	
	public void setDay(int day) {
		this.day = day;
	}
	
	@Slot (mandatory = true)
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
}