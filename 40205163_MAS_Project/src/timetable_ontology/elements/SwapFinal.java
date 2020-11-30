package timetable_ontology.elements;

import jade.content.AgentAction;
import jade.content.onto.annotations.Slot;
import jade.core.AID;

public class SwapFinal implements AgentAction{
	
	private SwapInitial swapInitial; // Initial slot on the timetable
	private AID agentTo; // Agent requesting tutorial (REQUESTER)
	private TutorialGroup tutorialTo;
	
	@Slot (mandatory = true)
	public AID getAgentTo() {
		return agentTo;
	}
	public void setAgentTo(AID agentTo) {
		this.agentTo = agentTo;
	}
	
	@Slot (mandatory = true)
	public SwapInitial getInitalSwapRequest() {
		return swapInitial;
	}
	public void setInitalSwapRequest(SwapInitial initalRequest) {
		this.swapInitial = initalRequest;
	}
	
	@Slot (mandatory = true)
	public TutorialGroup getTutorialTo() {
		return tutorialTo;
	}
	public void setTutorialTo(TutorialGroup tutorialTo) {
		this.tutorialTo = tutorialTo;
	}
}
