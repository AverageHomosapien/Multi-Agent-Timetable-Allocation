package _basicFindersAndReceivers;

import java.util.ArrayList;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;


public class Finder extends Agent{
	
private ArrayList<AID> simpleAgents = new ArrayList<>();
	
	protected void setup() {
		
		addBehaviour(new TickerBehaviour(this, 5000){
			protected void onTick() {
				
				// Create a template for the agent service we're looking for
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("Simple-agent");
				
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					simpleAgents.clear();
					for(int i=0; i<result.length; i++) {
						simpleAgents.add(result[i].getName());
						System.out.println("I have found " + simpleAgents.get(i));
					}
				}catch(FIPAException e) {
					e.printStackTrace();
				}
				
				// Can't find agents that aren't registered
				if (simpleAgents.size() == 0) {
					System.out.println("No agents can be found");
				}
			}
		});
	}
}
