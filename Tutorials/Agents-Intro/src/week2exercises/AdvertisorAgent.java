package week2exercises;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AdvertisorAgent extends Agent{
	
	int done = 0;
	
	protected void setup() {
		
		addBehaviour(new NewOneShotBehaviour());
		addBehaviour(new NewBehaviour());
		
		// Register the book-selling service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("book-selling");
		sd.setName("JADE-book-trading");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	
	private class NewBehaviour extends CyclicBehaviour{
		public void action(){
			System.out.println("Advertisor Ready for the " + done + "th time!");
			done++;
			
			if(done >= 10) {
				block();
			}
		}
	}
	
	private class NewOneShotBehaviour extends OneShotBehaviour{
		public void action(){
			System.out.println("Hey, 1 shot here!");
		}
	}
}
