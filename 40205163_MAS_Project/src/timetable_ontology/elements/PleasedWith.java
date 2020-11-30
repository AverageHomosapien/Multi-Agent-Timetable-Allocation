package timetable_ontology.elements;

import java.util.List;

import jade.content.Predicate;
import jade.core.AID;

public class PleasedWith implements Predicate{

	private AID student;
	
	public AID getStudent() {
		return student;
	}
	public void setStudent(AID student) {
		this.student = student;
	}
}

