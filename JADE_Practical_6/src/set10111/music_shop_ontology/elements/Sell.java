/**
 * 
 */
package set10111.music_shop_ontology.elements;

import jade.content.AgentAction;
import jade.content.onto.annotations.Slot;
import jade.core.AID;

public class Sell implements AgentAction {
	private AID buyer;
	private Item item;
	
	@Slot (mandatory = true)
	public AID getBuyer() {
		return buyer;
	}
	
	public void setBuyer(AID buyer) {
		this.buyer = buyer;
	}
	
	@Slot (mandatory = true)
	public Item getItem() {
		return item;
	}
	
	public void setItem(Item item) {
		this.item = item;
	}	
}
