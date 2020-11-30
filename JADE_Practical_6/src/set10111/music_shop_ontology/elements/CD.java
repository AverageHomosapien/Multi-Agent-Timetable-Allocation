/**
 * 
 */
package set10111.music_shop_ontology.elements;

import java.util.ArrayList;
import java.util.List;

import jade.content.onto.annotations.AggregateSlot;
import jade.content.onto.annotations.Slot;

// need to be a java bean - need default constructor too
public class CD extends Item {
	private String name;
	private List<Track> tracks;
	
	@Slot(mandatory = true)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@AggregateSlot(cardMin = 1)
	public List<Track> getTracks() {
		return tracks;
	}
	
	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
	}
}

