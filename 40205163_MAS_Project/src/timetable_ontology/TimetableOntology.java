package timetable_ontology;

import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.Ontology;

// Use a singleton pattern to initialise our ontology
public class TimetableOntology extends BeanOntology{
	
private static Ontology theInstance = new TimetableOntology("my_ontology"); // Single static instance
	
	public static Ontology getInstance(){
		return theInstance;
	}
	
	private TimetableOntology(String name) { // Private constructor setter, Public getter
		super(name);
		try {
			add("timetable_ontology.elements");
		} catch (BeanOntologyException e) {
			e.printStackTrace();
		}
	}
}
