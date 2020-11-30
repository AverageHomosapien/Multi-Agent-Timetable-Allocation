package timetable;

import java.util.Arrays;

import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import timetable_ontology.elements.Timeslot;
import timetable_ontology.elements.TutorialGroup;


public class Main {
	
	public int init=0; // used to increment and case as looping through the init processes

	public static void main(String[] args) {
		Profile myProfile = new ProfileImpl();
		Runtime myRuntime = Runtime.instance();
		int STUDENT_NUMBER = 30; // NUMBER OF STUDENT AGENTS TO BE CREATED
		int TESTRUNCHOICE = 3; // OPTION OF PRORGAM TO RUN

		Object[] passerArgs1 = new Object[2];
		Object[] passerArgs2 = new Object[2];
		Object[] passerArgs3 = new Object[2];
		Object[] passerArgs4 = new Object[2];
		Object[] passerArgs5 = new Object[2];
		Object[] passerArgs6 = new Object[2];
		
		
		// Choosing the tutorialGroup 
		if (TESTRUNCHOICE == 0) { // 2 classes with 1 set
			String tutID = "SET01010";
			int classSize = 5; 
			
			Timeslot slot1 = new Timeslot(0, 0); // Monday Morning
			TutorialGroup tg = new TutorialGroup(slot1, tutID, classSize);
			TutorialGroup[] group1 = new TutorialGroup[1];
			group1[0] = tg;
			
			Timeslot slot2 = new Timeslot(4, 8); // Friday Afternoon
			tg = new TutorialGroup(slot2, tutID, classSize);
			TutorialGroup[] group2 = new TutorialGroup[1];
			group2[0] = tg;
			
			int[][] slot_prefs_1 = new int[5][9];
			int[][] slot_prefs_2 = new int[5][9]; 
			
			for (int i = 0; i < slot_prefs_1.length; i++) {
				for (int j = 0; j < slot_prefs_1[0].length; j++) {
					slot_prefs_1[i][j] = 0;
					slot_prefs_2[i][j] = 0;
				}
			}
			
			slot_prefs_1[0][0] = 5; // Make them hate the slots they have
			slot_prefs_2[4][8] = 5;
			
			passerArgs1[0] = slot_prefs_1;
			passerArgs1[1] = group1;
			
			passerArgs2[0] = slot_prefs_2;
			passerArgs2[1] = group2;
		}
		else if (TESTRUNCHOICE == 1) { // 3 classes 1 set
			String tutID = "SET01010";
			int classSize = 5; 
			
			Timeslot slot1 = new Timeslot(0, 0); // Monday Morning
			TutorialGroup tg = new TutorialGroup(slot1, tutID, classSize);
			TutorialGroup[] group1 = new TutorialGroup[1];
			group1[0] = tg;
			
			Timeslot slot2 = new Timeslot(1, 0); // Tuesday Morning
			tg = new TutorialGroup(slot2, tutID, classSize);
			TutorialGroup[] group2 = new TutorialGroup[1];
			group2[0] = tg;
			
			Timeslot slot3 = new Timeslot(2, 0); // Wednesday Morning
			tg = new TutorialGroup(slot3, tutID, classSize);
			TutorialGroup[] group3 = new TutorialGroup[1];
			group3[0] = tg;
			
			int[][] slot_prefs_1 = new int[5][9];
			int[][] slot_prefs_2 = new int[5][9]; 
			int[][] slot_prefs_3 = new int[5][9]; 
			
			for (int i = 0; i < slot_prefs_1.length; i++) {
				for (int j = 0; j < slot_prefs_1[0].length; j++) {
					slot_prefs_1[i][j] = 4;
					slot_prefs_2[i][j] = 4;
					slot_prefs_3[i][j] = 4;
				}
			}
			
			// Hate the slots they have
			slot_prefs_1[0][0] = 5;
			slot_prefs_2[1][0] = 5;
			slot_prefs_3[2][0] = 5;
			
			// Love the slots they don't
			slot_prefs_1[1][0] = 0;
			slot_prefs_2[2][0] = 0;
			slot_prefs_3[0][0] = 0;
			
			passerArgs1[0] = slot_prefs_1;
			passerArgs1[1] = group1;
			
			passerArgs2[0] = slot_prefs_2;
			passerArgs2[1] = group2;
			
			passerArgs3[0] = slot_prefs_3;
			passerArgs3[1] = group3;
		}
		else if (TESTRUNCHOICE == 2) { // Monday & Tuesday Morning 2 module swap
			passerArgs1 = new Object[3];
			passerArgs2 = new Object[3];
			passerArgs3 = new Object[3];
			
			String tutID1 = "SET010101";
			String tutID2 = "SET010102";
			
			int classSize = 5; // Class size of 5
			
			Timeslot slot1 = new Timeslot(0, 0); // First thing Monday morning
			TutorialGroup tg1 = new TutorialGroup(slot1, tutID1, classSize);
			
			Timeslot slot2 = new Timeslot(1, 1); // Second thing Tuesday morning
			TutorialGroup tg2 = new TutorialGroup(slot2, tutID2, classSize);
			TutorialGroup[] group1 = new TutorialGroup[2];
			group1[0] = tg1;
			group1[1] = tg2;
			
			slot1 = new Timeslot(1, 0); // First thing Tuesday morning
			tg1 = new TutorialGroup(slot1, tutID1, classSize);
			slot2 = new Timeslot(0, 2); // Second thing Monday morning
			tg2 = new TutorialGroup(slot2, tutID2, classSize);
			
			TutorialGroup[] group2 = new TutorialGroup[2];
			group2[0] = tg1;
			group2[1] = tg2;
			
			int[][] slot_prefs_1 = new int[5][9];
			int[][] slot_prefs_2 = new int[5][9]; 
			
			for (int i = 0; i < slot_prefs_1.length; i++) {
				for (int j = 0; j < slot_prefs_1[0].length; j++) {
					slot_prefs_1[i][j] = 4;
					slot_prefs_2[i][j] = 4;
				}
			}
			
			slot_prefs_1[0][0] = 5; // Make them hate the slots they have
			slot_prefs_1[1][0] = 0;
			slot_prefs_2[0][0] = 0;
			slot_prefs_2[1][0] = 5;
			
			
			passerArgs1[0] = slot_prefs_1;
			passerArgs1[1] = group1;
			
			passerArgs2[0] = slot_prefs_2;
			passerArgs2[1] = group2;
		}
		else if (TESTRUNCHOICE == 3) { // 3 sets, 3 classes, 18 students
			String tutID1 = "SET01010"; // 9ams
			String tutID2 = "SET01011"; // 10ams
			String tutID3 = "SET01012"; // 11ams
					
			int classSize = 6; 
			
			// All trying to move to 3 slot preferences
			int[][] slot_prefs_1 = new int[5][9];
			int[][] slot_prefs_2 = new int[5][9]; 
			int[][] slot_prefs_3 = new int[5][9]; 
			int[][] slot_prefs_4 = new int[5][9];
			int[][] slot_prefs_5 = new int[5][9]; 
			int[][] slot_prefs_6 = new int[5][9]; 
			
			for (int i = 0; i < slot_prefs_1.length; i++) {
				for (int j = 0; j < slot_prefs_1[0].length; j++) {
					slot_prefs_1[i][j] = 4;
					slot_prefs_2[i][j] = 4;
					slot_prefs_3[i][j] = 4;
					slot_prefs_4[i][j] = 4;
					slot_prefs_5[i][j] = 4;
					slot_prefs_6[i][j] = 4;
				}
			}
			// Setting slot prefs to very desired
			
			slot_prefs_1[0][0] = 0;
			slot_prefs_2[0][0] = 0;
			slot_prefs_3[1][0] = 0;
			slot_prefs_4[1][0] = 0;
			slot_prefs_5[2][0] = 0;
			slot_prefs_6[2][0] = 0;
			for (int i = 1; i < 3; i++) {
				slot_prefs_1[0][i] = 0;
				slot_prefs_2[0][i] = 0;
				slot_prefs_3[1][i] = 0;
				slot_prefs_4[1][i] = 0;
				slot_prefs_5[2][i] = 0;
				slot_prefs_6[2][i] = 0;
			}
			
			
			
			/// 6 groups of students \\\
			
			// Want all Monday Mornings
			Timeslot slot1 = new Timeslot(2, 1);
			TutorialGroup tg1 = new TutorialGroup(slot1, tutID2, classSize);
			Timeslot slot2 = new Timeslot(1, 0);
			TutorialGroup tg2 = new TutorialGroup(slot2, tutID1, classSize);
			Timeslot slot3 = new Timeslot(1, 2);
			TutorialGroup tg3 = new TutorialGroup(slot2, tutID3, classSize);
			
			TutorialGroup[] group1 = new TutorialGroup[3];
			group1[0] = tg1;
			group1[1] = tg2;
			group1[2] = tg3;
			slot_prefs_1[1][0] = 5;
			slot_prefs_1[2][1] = 5;
			slot_prefs_1[1][2] = 5;
			passerArgs1[0] = slot_prefs_1; // Setting passer arguments
			passerArgs1[1] = group1;
			
			slot1 = new Timeslot(2, 2);
			tg1 = new TutorialGroup(slot1, tutID3, classSize);
			slot2 = new Timeslot(1, 1);
			tg2 = new TutorialGroup(slot2, tutID2, classSize);
			slot3 = new Timeslot(2, 0);
			tg3 = new TutorialGroup(slot2, tutID1, classSize);
			
			TutorialGroup[] group2 = new TutorialGroup[3];
			group2[0] = tg1;
			group2[1] = tg2;
			group2[2] = tg3;
			slot_prefs_2[2][2] = 5;
			slot_prefs_2[1][1] = 5;
			slot_prefs_2[2][0] = 5;
			passerArgs2[0] = slot_prefs_2; // Setting passer arguments
			passerArgs2[1] = group2;
			
			
			// Want Tuesday mornings
			slot1 = new Timeslot(2, 2);
			tg1 = new TutorialGroup(slot1, tutID3, classSize);
			slot2 = new Timeslot(0, 1);
			tg2 = new TutorialGroup(slot2, tutID2, classSize);
			slot3 = new Timeslot(0, 0);
			tg3 = new TutorialGroup(slot2, tutID1, classSize);
			
			TutorialGroup[] group3 = new TutorialGroup[3];
			group3[0] = tg1;
			group3[1] = tg2;
			group3[2] = tg3;
			slot_prefs_3[2][2] = 5;
			slot_prefs_3[0][1] = 5;
			slot_prefs_3[0][0] = 5;
			passerArgs3[0] = slot_prefs_3; // Setting passer arguments
			passerArgs3[1] = group3;
			
			slot1 = new Timeslot(0, 2);
			tg1 = new TutorialGroup(slot1, tutID3, classSize);
			slot2 = new Timeslot(2, 1);
			tg2 = new TutorialGroup(slot2, tutID2, classSize);
			slot3 = new Timeslot(2, 0);
			tg3 = new TutorialGroup(slot2, tutID1, classSize);
			
			TutorialGroup[] group4 = new TutorialGroup[3];
			group4[0] = tg1;
			group4[1] = tg2;
			group4[2] = tg3;
			slot_prefs_4[0][2] = 5;
			slot_prefs_4[2][1] = 5;
			slot_prefs_4[2][0] = 5;
			passerArgs4[0] = slot_prefs_4; // Setting passer arguments
			passerArgs4[1] = group4;
			
			
			// Wants Wednesday mornings
			slot1 = new Timeslot(0, 2);
			tg1 = new TutorialGroup(slot1, tutID3, classSize);
			slot2 = new Timeslot(0, 1);
			tg2 = new TutorialGroup(slot2, tutID2, classSize);
			slot3 = new Timeslot(0, 0);
			tg3 = new TutorialGroup(slot2, tutID1, classSize);
			
			TutorialGroup[] group5 = new TutorialGroup[3];
			group5[0] = tg1;
			group5[1] = tg2;
			group5[2] = tg3;
			slot_prefs_5[0][2] = 5;
			slot_prefs_5[0][1] = 5;
			slot_prefs_5[0][0] = 5;
			passerArgs5[0] = slot_prefs_5; // Setting passer arguments
			passerArgs5[1] = group5;
			
			slot1 = new Timeslot(1, 2); 
			tg1 = new TutorialGroup(slot1, tutID3, classSize);
			slot2 = new Timeslot(1, 1);
			tg2 = new TutorialGroup(slot2, tutID2, classSize);
			slot3 = new Timeslot(1, 0);
			tg3 = new TutorialGroup(slot2, tutID1, classSize);
			
			TutorialGroup[] group6 = new TutorialGroup[3];
			group6[0] = tg1;
			group6[1] = tg2;
			group6[2] = tg3;
			slot_prefs_6[1][2] = 5;
			slot_prefs_6[1][1] = 5;
			slot_prefs_6[1][0] = 5;
			passerArgs6[0] = slot_prefs_6; // Setting passer arguments
			passerArgs6[1] = group6;

		}
		
		try{
			ContainerController myContainer = myRuntime.createMainContainer(myProfile);	
			AgentController rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
			rma.start();
			
			// Need to pass number of modules as 
			AgentController timetablerAgent = myContainer.createNewAgent("timetabler", TimetablingAgent.class.getCanonicalName(), null);
			timetablerAgent.start();
			
			AgentController agent0, agent1, agent2, agent3, agent4, agent5;
			
			if (TESTRUNCHOICE == 0) { // 2 groups of 5 students for 1 module
				for (int i = 0; i < STUDENT_NUMBER; i+=2) {
					agent0 = myContainer.createNewAgent("student"+i, StudentAgent.class.getCanonicalName(), passerArgs1);
					agent0.start();
					
					agent1 = myContainer.createNewAgent("student"+(i+1), StudentAgent.class.getCanonicalName(), passerArgs2);
					agent1.start();
				}
			}
			else if (TESTRUNCHOICE == 1) { // 3 groups of 5 students for 1 module
				for (int i = 0; i < STUDENT_NUMBER; i+=3) {
					agent0 = myContainer.createNewAgent("student"+i, StudentAgent.class.getCanonicalName(), passerArgs1);
					agent0.start();
					
					agent1 = myContainer.createNewAgent("student"+(i+1), StudentAgent.class.getCanonicalName(), passerArgs2);
					agent1.start();
					
					agent2 = myContainer.createNewAgent("student"+(i+2), StudentAgent.class.getCanonicalName(), passerArgs3);
					agent2.start();
				}
			}
			else if (TESTRUNCHOICE == 2) { // 2 groups of 5 students for 2 modules
				for (int i = 0; i < STUDENT_NUMBER; i+=2) {
					agent0 = myContainer.createNewAgent("student"+i, StudentAgent.class.getCanonicalName(), passerArgs1);
					agent0.start();
					
					agent1 = myContainer.createNewAgent("student"+(i+1), StudentAgent.class.getCanonicalName(), passerArgs2);
					agent1.start();
				}
			}
			else if (TESTRUNCHOICE == 3) { // 6 groups of 5 students for 3 modules
				for (int i = 0; i < STUDENT_NUMBER; i+=6) {
					agent0 = myContainer.createNewAgent("student"+i, StudentAgent.class.getCanonicalName(), passerArgs1);
					agent0.start();
					
					agent1 = myContainer.createNewAgent("student"+(i+1), StudentAgent.class.getCanonicalName(), passerArgs2);
					agent1.start();
					
					agent2 = myContainer.createNewAgent("student"+(i+2), StudentAgent.class.getCanonicalName(), passerArgs3);
					agent2.start();
					
					agent3 = myContainer.createNewAgent("student"+(i+3), StudentAgent.class.getCanonicalName(), passerArgs4);
					agent3.start();
					
					agent4 = myContainer.createNewAgent("student"+(i+4), StudentAgent.class.getCanonicalName(), passerArgs5);
					agent4.start();
					
					agent5 = myContainer.createNewAgent("student"+(i+5), StudentAgent.class.getCanonicalName(), passerArgs6);
					agent5.start();
				}
			}
			
			
			
			//ac[0] = myContainer.createNewAgent("student"+0, StudentAgent.class.getCanonicalName(), passerArgs1);
			//ac[0].start();
			
			//ac[1] = myContainer.createNewAgent("student"+1, StudentAgent.class.getCanonicalName(), passerArgs2);
			//ac[1].start();
			
			/*
			for (int i = 0; i < STUDENT_NUMBER; i++) {
			-- generate the priorities inside if random???? --- at least loop generate priorities
				ac[i] = myContainer.createNewAgent("student"+i, StudentAgent.class.getCanonicalName(), priorities[i]);
				ac[i].start();
				System.out.println("Started agent " + ac[i].getName());
			}
			*/
			
			
		}
		catch(Exception e){
			System.out.println("Exception starting agent: " + e.toString());
		}
	}
	
	/*
	// First test case - 50 / 50 split where agents want to move to the other timetable slot
	private static Object[][] timetablerArgs1(){
		
		Object[][] moduleClasses = new Object[1][2];
		moduleClasses[0][0] = "Set10101";	// Set number
		moduleClasses[0][1] = 2;			// Class number
		
		return moduleClasses;
	}
	*/
	
	private int[][] testCase1(){
		int[][] foo = new int[1][1];
		
		return foo;
	}
	
	private int[][] testCase2(){
		int[][] foo = new int[1][1];
		
		return foo;
	}
	
	private int[][] testCase3(){
		int[][] foo = new int[1][1];
		
		return foo;
	}
	
	private int[][] stdUseCase(){
		int[][] foo = new int[1][1];
		
		return foo;
	}
}
