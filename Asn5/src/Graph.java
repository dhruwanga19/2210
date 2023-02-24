/*CS2210 Assignment 5
 * Dhruwang Kishorbhai Akbari
 * 251186352
 */
import java.util.ArrayList;

public class Graph implements GraphADT {
	
	private int numNode; //stores the number of nodes in the graph
	private Edge adjMatx[][];  // matrix to store the edges in the graph
	private Node nodeArr[];  // array for storing the nodes
	
	//constructor to initialise the graph of a certain number of nodes
	public Graph(int n) {
		numNode = n; 
		adjMatx = new Edge [n][n];
		nodeArr = new Node [n];
		
		//stores the nodes in an increasing order in the array
		for(int i = 0; i < n; i++) {
			nodeArr[i] = new Node(i);
		}
	}
	
	//method to insert an edge between two nodes and stores the type of edge
	public void insertEdge(Node u, Node v, int type) throws GraphException {
		if(inGraph(u, v)) { // if nodes are in graph else throws GraphException
			Edge e = adjMatx[u.getName()][v.getName()]; // gets the edge from the matrix
			if (e == null) { //if the spot is empty the edge is made
				adjMatx[u.getName()][v.getName()] = new Edge (u,v, type);
				adjMatx[v.getName()][u.getName()] = new Edge (v, u, type);
			}
			else {  //else the edge is not made and throws GraphException
				throw new GraphException("Edges already in Graph");
			}
		}else {
			throw new GraphException("Nodes not in Graph");
		}
		
	}
	
	//getter method to get the node from the graph
	public Node getNode(int n) throws GraphException {
		if(n < 0 || n >= numNode) { // checks if the node entered is not in the range of numNodes
			throw new GraphException("Node not in Graph");
		}else {
			return nodeArr[n];
		}
		
	}
	
	//method to store the incident edges from a single node in an ArrayList
	public ArrayList<Edge> incidentEdges(Node u) throws GraphException {
		if(inGraph(u)) { //  if the node is in the graph
			ArrayList<Edge> myList = new ArrayList<Edge>();  //creates an empty list
			for(int i =0; i < numNode; i++) { //iterates through the whole matrix and nodes
				if(adjMatx[u.getName()][i] != null) { // 
					myList.add(adjMatx[u.getName()][i]); // adds the nodes adjacent to the node
				}
			}
			return myList;
		}else {
			throw new GraphException ("Node not in Graph");
		}
	}
	
	//getter method to get the edge from Node u to Node v
	public Edge getEdge(Node u, Node v) throws GraphException {
		if (inGraph(u,v)) {
			Edge myEdge = adjMatx[u.getName()][v.getName()]; // checks if there is a edge or is it null
			if (myEdge == null) {
				throw new GraphException("Edge not in Graph");
			}else {
				return myEdge;
			}
		}else {
			throw new GraphException("Node " + u.getName() + " or " + v.getName() + " not in Graph");
		}
	}
	//method to know the if both the nodes are adjacent
	public boolean areAdjacent(Node u, Node v) throws GraphException {
		if(inGraph(u,v)) {
			Edge myEdge = adjMatx[u.getName()][v.getName()]; // if there exists an edge between the nodes they are adjacent
			if(myEdge == null) {
				return false;
			}else {
				return true;
			}
		}else {
			throw new GraphException("Nodes not in Graph");
		}
	}
	
	//private method to see if the nodes are there in the graph
	private boolean inGraph(Node u, Node v) {
		try {
			getNode(u.getName());
			getNode(v.getName());
			
			return true;
		}catch (GraphException e) {
			return false;
		}
	}
	
	//overriden method with one parameter
	private boolean inGraph(Node u) {
		try {
			getNode(u.getName());
			return true;
		}catch(GraphException e) {
			return false;
		}
	}
}
