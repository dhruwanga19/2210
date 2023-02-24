public class Odd {
	
	private int count = 0;
	
  public int numOdd(Node r) {
  /* Input: Root r of a tree
     Output: true if all internal nodes of the tree have odd degree; false otherwise
     
     You can use the following methods from class Node:
        - numChildren() returns the number of children of a node.
        - isLeaf(): returns true if a node is a leaf and returns false otherwise
        
     To translate the following pseudocode
     
        for each child u of r do { ... }
        
     use the following java code:
     
        Node[] children = r.getChildren();
        for (Node u : children) { ... }
   */
	  
	  int k = r.numChildren();
	  if (k % 2 == 1) {
		  count++;
	  }
	  Node[] children = r.getChildren();
	  for(Node u : children) {
		  if (u.numChildren()!=0) {
			  numOdd(u);
		  }
	  }
	  return count;
  }
}
