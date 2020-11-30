package timetable_ontology.elements;

import jade.content.AgentAction;
import jade.content.onto.annotations.Slot;
import jade.core.AID;

// MessageBoard is an ArrayList of SwapInitials
// Requester details always listed in SwapFinal
public class SwapInitial implements AgentAction {

	private TutorialGroup tutorialFrom;
	private AID agentFrom; // Agent receiving request
	
	@Slot (mandatory = true)
	public AID getAgentFrom() {
		return agentFrom;
	}
	public void setAgentFrom(AID agentFrom) {
		this.agentFrom = agentFrom;
	}
	
	@Slot (mandatory = true)
	public TutorialGroup getTutorial() {
		return tutorialFrom;
	}
	public void setTutorial(TutorialGroup tutorial) {
		this.tutorialFrom = tutorial;
	}
}
