/*CS2210 Assignment 5
 * Dhruwang Kishorbhai Akbari
 * 251186352
 */
public class Node {
	private int name; // stores the name of the node
	boolean marked = false; //stores the mark of the node
	
	// constructor for Node class to initialise the variables
	public Node(int nodeName) {
		marked = false;
		name = nodeName;		
	}
	
	//setter method for marking the node
	public void setMark(boolean mark) {
		this.marked = mark;
	}
	
	//getter method to know the mark of the node
	public boolean getMark() {
		return this.marked;
	}
	
	//getter method to get the name of the node
	public int getName() {
		return this.name;
	}
	
	//method checking if two nodes are equal according to their names
	public boolean equals(Node otherNode) {
		return (this.name == otherNode.name);
	}
	
}
