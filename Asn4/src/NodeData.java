/*
 * Dhruwang Kishorbhai Akbari
 * 251186352
 */
import java.util.ArrayList;

public class NodeData {
	//instance variables of the class to store the data
	private String name;
	private ArrayList<MultimediaItem> media;
	
	//constructor to initialise the class object
	public NodeData(String newName) {
		this.name = newName;
		media = new ArrayList<MultimediaItem>();
	}
	
	// method to add items to the array list
	public void add(MultimediaItem newItem) {
		media.add(newItem);
	}
	
	//getter method to access the name
	public String getName() {
		return this.name;
	}
	
	//getter method to access the arraylist
	public ArrayList<MultimediaItem> getMedia(){
		return this.media;
	}
}
