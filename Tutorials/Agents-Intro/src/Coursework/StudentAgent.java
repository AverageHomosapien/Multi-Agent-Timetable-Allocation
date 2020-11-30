package Coursework;

import java.util.Arrays;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class StudentAgent extends Agent{
	// STEP 1: FIGURE OUT HOW ONTOLOGIES WORK - USE THE EXISTING ONTOLOGIES FOR THE (AUCTION AGENTS))?? Practical work
	// STEP 2: do

	/*
	 *	[0-5 preference array for each time for each student]
	 *	Scored array based on preference score and times
	 *	Times that the student agent has
	 *	Threshold which student is 'happy to proceed' but will still request swapping until program over?
	 */
	
	int[][] studentPreferences = new int[5][9];
	boolean[][] tutorialClass = new boolean[5][9];
	
	@Override
	protected void setup() {
		register();
		
		Arrays.fill(tutorialClass, false);
		
		for (int i = 0; i < studentPreferences.length; i++) {
			for (int j = 0; j < studentPreferences.length; j++) {
				studentPreferences[i][j] = generateStudentPreference();
			}
		}
		
		addBehaviour(new GetClassTimeBehaviour());
		
		addBehaviour(new CheckFitness());
	}
	
	
	public class GetClassTimeBehaviour extends OneShotBehaviour {

		@Override
		public void action() {
			
		}
		
	}
	
	
	
	
	
	protected void register() {
		DFAgentDescription dfd = new DFAgentDescription ();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("student-agent");
		sd.setName( getLocalName() + "-student-agent");
		dfd.addServices(sd);
		
		try{
			DFService.register(this , dfd);
			System.out.println("Registered");
		}
		catch(FIPAException e) {
			e.printStackTrace() ;
		}

	}
		
	// Generates a random number between 0-4, heavily weighted towards 2 (semi-normal distribution)
	private int generateStudentPreference() {
		int val;
		double rand = Math.random();
		
		if (rand < 0.5) { // 50% chance for don't mind
			return 2;
		}else if (rand < 0.65) { // 15 % chance for would prefer
			return 1;
		}else if (rand < 0.8) { // 15 % chance for wouldn't like
			return 3;
		}else if (rand < 0.9) { // 10% chance for would love
			return 0;
		}
		return 4; // 10% chance for would hate
	}
	
	// Calculates the fitness of the agent
	private double calculateFitness() {
		double val; // calculate fitness as 1/number_given+1 (if 1/5 then count as -10, since student can't do the slot)
		return 0.0;
		
	}
}
