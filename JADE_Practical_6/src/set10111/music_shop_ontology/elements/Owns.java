/**
 * 
 */
package set10111.music_shop_ontology.elements;

import jade.content.Predicate;
import jade.content.onto.annotations.Slot;
import jade.core.AID;

public class Owns implements Predicate {
	private Item item;
	private AID owner;
	
	@Slot (mandatory = true)
	public AID getOwner() {
		return owner;
	}
	
	public void setOwner(AID owner) {
		this.owner = owner;
	}
	
	@Slot (mandatory = true)
	public Item getItem() {
		return item;
	}
	
	public void setItem(Item item) {
		this.item = item;
	}
}
