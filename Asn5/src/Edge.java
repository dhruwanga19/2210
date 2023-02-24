/*CS2210 Assignment 5
 * Dhruwang Kishorbhai Akbari
 * 251186352
 */
public class Edge {
	
	private Node firstEndpoint;  //starting point of the edge
	private Node secondEndpoint;  // ending point of the edge
	private int type;  // type the edge is 1-corridor, 2-brick wall, 3-rock wall, 4-metal wall
	
	//constructor to initialise the variables
	public Edge(Node u, Node v, int edgeType) {
		this.firstEndpoint = u;
		this.secondEndpoint = v;
		this.type = edgeType;
	}
	
	//getter method to get the starting point of the edge
	public Node firstEndpoint() {
		return this.firstEndpoint;
	}
	
	//getter method to get the end point of the edge
	public Node secondEndpoint() {
		return this.secondEndpoint;
	}
	
	//getter method to get the type of edge
	public int getType() {
		return this.type;
	}
	
	//setter method set the edge type
	public void setType(int newType) {
		this.type = newType;
	}
	
	//method to check if two edges are equal according to their starting and ending point names
	public boolean equals(Edge otherEdge) {
		return ((this.firstEndpoint.getName() == otherEdge.firstEndpoint.getName() && this.secondEndpoint.getName() == otherEdge.secondEndpoint.getName())
				|| this.firstEndpoint.getName() == otherEdge.secondEndpoint.getName() && this.secondEndpoint.getName() == otherEdge.firstEndpoint.getName());
	}
}
