package set10111.music_shop_ontology.elements;

import jade.content.onto.annotations.Slot;

public class Book extends Item {

	private String name;
	private String author;
	private int length;
	
	@Slot (mandatory = true)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Slot (mandatory = true)
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	
	
}
