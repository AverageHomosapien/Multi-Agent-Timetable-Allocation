package _basicFindersAndReceivers;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class FoundAgent extends Agent{
	
	int timeLeft = 60;
	protected void setup() {
		register();
		
		addBehaviour(new TickerBehaviour(this, 1000) {
			protected void onTick() {
				if (timeLeft > 0) {
					System.out.println(timeLeft + " seconds of " + getAID().getName() + " left.");
					timeLeft-= 1;
				}else {
					System.out.println("Bye bye");
					myAgent.doDelete();
				}
			}
		});
	}
	
	protected void register() {
		DFAgentDescription dfd = new DFAgentDescription ();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("simple-agent");
		sd.setName( getLocalName() + "-simple-agent");
		dfd.addServices(sd);
		
		try{
			DFService.register(this , dfd);
			System.out.println("Registered");
		}
		catch(FIPAException e) {
			e.printStackTrace() ;
		}

	}
}
