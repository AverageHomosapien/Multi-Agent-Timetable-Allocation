package agentAuction;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Buyer extends Agent{

	String[] itemTypes;
	
	protected void setup() {
		Object[] args = getArguments();
		itemTypes = (String[]) args[0];
		
		register();
	}
	
	
	// Registers an agent
	protected void register() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		
		ServiceDescription sd = new ServiceDescription();
		sd.setType("auctioneer-agent");
		sd.setName(getLocalName() + "-auctioneer-agent");
		dfd.addServices(sd);
		
		try{
			DFService.register(this , dfd);
		}
		catch(FIPAException e) {
			e.printStackTrace() ;
		}
	}
	
	// De-registers agent from yellow pages
	protected void deregister() {
		try {
			DFService.deregister(this);
		}
		catch(FIPAException e) {
			e.printStackTrace();
		}
	}
}
