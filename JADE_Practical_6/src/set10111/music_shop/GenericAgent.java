package set10111.music_shop;

import java.util.ArrayList;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import set10111.music_shop_ontology.ECommerceOntology;
import set10111.music_shop_ontology.elements.CD;
import set10111.music_shop_ontology.elements.Item;
import set10111.music_shop_ontology.elements.Single;

/* Testing the use of generics to try and understand how best to import a CD into 
 * .. one AdvancedBuyerAgent vs importing a Single into another
 * 
 * Since there's very little object interaction with the CD/Single, I can probably just
 * .. import it via the arguments, cast to Item and then just use instanceof for
 * .. each interaction.
 */
public class GenericAgent extends Agent {
	private Item itemIn;
	private Codec codec = new SLCodec();
	private Ontology ontology = ECommerceOntology.getInstance();
	private Object[] arguments;
	
	protected void setup() {
		Object[] arguments = getArguments();
		itemIn = (Item) arguments[0];
		
		ItemGeneric newItem = setItem();
		System.out.println("New item type is " + newItem.get());
		
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		addBehaviour(new ItemBehaviour(newItem));
	}
	
	
	public class ItemBehaviour extends OneShotBehaviour {
		
		ItemGeneric item;
		
		public ItemBehaviour(ItemGeneric item) {
			this.item = item;
		}
		
		@Override
		public void action() {
			
			
		}
	}
	
	
	private ItemGeneric setItem() {
		if (itemIn instanceof Item) {
			System.out.println("ITEM");
			if (itemIn instanceof CD) {
				System.out.println("CD");
				ItemGeneric<CD> cd = new ItemGeneric<>();
				cd.add((CD) itemIn);
				return cd;
			}
			else if (itemIn instanceof Single) {
				System.out.println("SINGLE");
				ItemGeneric<Single> single = new ItemGeneric<>();
				single.add((Single) itemIn);
				return single;
			}else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	
	// Generic Class
	public class ItemGeneric<T>{
		private T t;
		
		public void add(T t) {
			this.t = t;
		}
		
		public T get() {
			return this.t;
		}
	}
}
