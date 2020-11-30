package set10111.music_shop;
import java.util.ArrayList;

import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import set10111.music_shop_ontology.elements.Book;
import set10111.music_shop_ontology.elements.CD;
import set10111.music_shop_ontology.elements.Single;
import set10111.music_shop_ontology.elements.Track;


public class Main {

	public static void main(String[] args) {
		Profile myProfile = new ProfileImpl();
		Runtime myRuntime = Runtime.instance();
		try{
			ContainerController myContainer = myRuntime.createMainContainer(myProfile);	
			AgentController rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
			rma.start();

			Object[] cdPass = new Object[1];
			Object[] singlePass = new Object[1];
			Object[] bookPass = new Object[1];
					
			// Prepare CD content
			CD cd = new CD();
			cd.setName("Synchronicity");
			cd.setSerialNumber(123);
			ArrayList<Track> tracks = new ArrayList<Track>();
			Track t = new Track();
			t.setName("Every breath you take");
			t.setDuration(230);
			tracks.add(t);
			t = new Track();
			t.setName("King of pain");
			t.setDuration(500);
			tracks.add(t);
			cd.setTracks(tracks);
			cdPass[0] = cd;
			
			// Prepare single content
			Single single = new Single();
			single.setName("ACDC Hot Hits");
			single.setSerialNumber(124);
			tracks = new ArrayList<>();
			t = new Track();
			t.setName("Back in Black");
			t.setDuration(255);
			tracks.add(t);
			t = new Track();
			t.setName("Black in Back");
			t.setDuration(522);
			tracks.add(t);
			single.setTracks(tracks);
			singlePass[0] = single;
			
			Book book = new Book();
			book.setName("One Very Dark Night");
			book.setAuthor("Arthur Conan Doyle");
			book.setLength(350);
			book.setSerialNumber(125);
			bookPass[0] = book;
			
			
			//AgentController sellerAgent = myContainer.createNewAgent("seller", GenericAgent.class.getCanonicalName(), singlePass);
			//sellerAgent.start();
			
			
			
			AgentController sellerAgent = myContainer.createNewAgent("seller", SellerAgent.class.getCanonicalName(), null);
			sellerAgent.start();
			
			AgentController singleBuyerAgent = myContainer.createNewAgent("Single buyer", AdvancedBuyerAgent.class.getCanonicalName(), singlePass);
			singleBuyerAgent.start();
			
			AgentController cdBuyerAgent = myContainer.createNewAgent("CD buyer", AdvancedBuyerAgent.class.getCanonicalName(), cdPass);
			cdBuyerAgent.start();
			
			AgentController bookBuyerAgent = myContainer.createNewAgent("Book buyer", AdvancedBuyerAgent.class.getCanonicalName(), bookPass);
			bookBuyerAgent.start();
			
			/*
			AgentController recklessBuyerAgent = myContainer.createNewAgent("reckless buyer", RecklessBuyerAgent.class.getCanonicalName(), null);
			recklessBuyerAgent.start();
			AgentController buyerAgent = myContainer.createNewAgent("buyer", SeqNewSender.class.getCanonicalName(), null);
			AgentController buyerAgent = myContainer.createNewAgent("buyer", CautiousBuyerAgent.class.getCanonicalName(), null);
			AgentController advancedBuyerAgent = myContainer.createNewAgent("advanced buyer", AdvancedBuyerAgent.class.getCanonicalName(), null);
			advancedBuyerAgent.start();
			AgentController sellerAgent = myContainer.createNewAgent("seller", Receiver.class.getCanonicalName(), null);
			*/
		}
		catch(Exception e){
			System.out.println("Exception starting agent: " + e.toString());
		}


	}

}
