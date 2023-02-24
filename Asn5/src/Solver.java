/*CS2210 Assignment 5
 * Dhruwang Kishorbhai Akbari
 * 251186352
 */
import java.io.*;
import java.util.*;

public class Solver {
	
	private Graph myGraph = null; //Graph 
	private int width, length, k1, k2, start, exit; // values in the text file for the length, width, and no. of bombs
	private Stack<Node> path = new Stack<Node>(); // stack for storing the path of the solution
	
	
	//constructor for initialising the graph with a input file and reading the input file.
	public Solver(String inputFile) throws LabyrinthException {
		try {
				BufferedReader in = new BufferedReader(new FileReader(inputFile));
				in.readLine();
				width = Integer.parseInt(in.readLine());
				length = Integer.parseInt(in.readLine());
				k1 = Integer.parseInt(in.readLine());  //numnber of brick bombs
				k2 = Integer.parseInt(in.readLine());  // number of metal bombs
				
				myGraph = new Graph(length * width); //initalises the graph
				
				String line; 
				boolean room; //checks if the character in the line is a room  
				int row =0;
				while((line = in.readLine()) != null) {
					
					for(int i = 0; i < line.length(); i++) {  //iterates through characters in the line
						room = (row%2 == 0 && i%2 == 0); 
						if(room) {
							if(line.charAt(i) == 'e') { //if it is an entrance
								start = (i/2) + (row/2)*width;
							}else if(line.charAt(i) == 'x') {  // if it is an exit
								exit = (i/2) + (row/2)*width;
							}
						}else { // if its not a room
							if(line.charAt(i) == '-') {  // for a horizontal corridor
								myGraph.insertEdge(myGraph.getNode( (i/2) + (row/2)*width ), myGraph.getNode( (i/2) + (row/2)*width + 1), 1);
							}else if(line.charAt(i) == 'b') { // for a horizontal brick wall
								myGraph.insertEdge(myGraph.getNode( (i/2) + (row/2)*width ), myGraph.getNode( (i/2) + (row/2)*width + 1), 2);
							}else if(line.charAt(i) == 'r') {  // for a horizontal rock wall
								myGraph.insertEdge(myGraph.getNode( (i/2) + (row/2)*width ), myGraph.getNode( (i/2) + (row/2)*width + 1), 3);
							}else if(line.charAt(i) == 'm') {  // for a horizontal metal wall
								myGraph.insertEdge(myGraph.getNode( (i/2) + (row/2)*width ), myGraph.getNode( (i/2) + (row/2)*width + 1), 4);
							}else if(line.charAt(i) == '|') {  // for a vertical corridor
								myGraph.insertEdge(myGraph.getNode( (i/2) + ((row-1)/2)*width ), myGraph.getNode( (i/2) + ((row+1)/2)*width ), 1);
							}else if(line.charAt(i) == 'B') { //for a vertical brick wall
								myGraph.insertEdge(myGraph.getNode( (i/2) + ((row-1)/2)*width ), myGraph.getNode( (i/2) + ((row+1)/2)*width ), 2);
							}else if(line.charAt(i) == 'R') { // for a vertical rock wall
								myGraph.insertEdge(myGraph.getNode( (i/2) + ((row-1)/2)*width ), myGraph.getNode( (i/2) + ((row+1)/2)*width ), 3);
							}else if(line.charAt(i) == 'M') {  // for a vertical metal wall
								myGraph.insertEdge(myGraph.getNode( (i/2) + ((row-1)/2)*width ), myGraph.getNode( (i/2) + ((row+1)/2)*width ), 4);
							}
						}
					} row++;
				}
				in.close();
			}catch(Exception e) {
				throw new LabyrinthException("Input file doesn't exist");
			}
	}
	
	//getter method to get the graph
	public Graph getGraph() throws LabyrinthException{
		if(myGraph == null) {
			throw new LabyrinthException("Graph is undefined");
		}else {
			return myGraph;
		}
	}
	
	//solving the problem by using an algorithm to find the path from entrance to exit with the parameters
	// like brick bombs and metal bombs and backtracking.
	public Iterator solve() {
		try {
			Node begin = myGraph.getNode(start);
			Node end = myGraph.getNode(exit);
			
			if(path(begin, end) == false) { // uses the private method path to find the solution
				return null;
			}
			
			Iterator<Node> iter = path.iterator();
			
			return iter;
		} catch (GraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	//private method for solving the problem uses the algorithm taught in class for path finding with additional parameters
	private boolean path(Node u, Node v) throws GraphException {
		int brickbomb = this.k1;
		int metalbomb = this.k2;
		u.setMark(true);
		path.push(u);
		
		if(u == v) {
			return true;
		}else {
			ArrayList<Edge> edgeList = myGraph.incidentEdges(u);
			Iterator<Edge> temp = edgeList.iterator();
			while(temp.hasNext()) {
				Edge edge = temp.next();
				Node n = edge.secondEndpoint();
				if(n.getName() == u.getName()) {
					n = edge.firstEndpoint();
				}
				
				if(n.getMark() == false) {
					if(edge.getType() == 1) {
						if(path(n,v)==true) {
							return true;
						}
					}else if(edge.getType() == 2) {
						if(k1 > 0) {
							k1 = k1 - 1;
							if(path(n,v) == true) {
								return true;
							}else {
								k1 = brickbomb;
								k2 = metalbomb;
								
							}
						}
					}else if(edge.getType() == 3) {
						if(k1 >= 2) {
							k1 = k1 - 2;
							if(path(n,v) == true) {
								return true;
							}else {
								k1 = brickbomb;
								k2 = metalbomb;
							}
						}
					}else if(edge.getType() == 4) {
						if(k2 > 0) {
							k2 = k2 - 1;
							if(path(n,v) == true) {
								return true;
							}else {
								k1 = brickbomb;
								k2 = metalbomb;
							}
						}
					}
				}
			}
			path.pop();
			u.setMark(false);
			return false;
		}
	}
}
