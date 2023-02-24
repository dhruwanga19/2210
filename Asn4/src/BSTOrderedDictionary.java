/*
 * Dhruwang Kishorbhai Akbari
 * 251186352
 */
import java.util.ArrayList;

public class BSTOrderedDictionary implements BSTOrderedDictionaryADT {
	//instance varibles
	private BSTNode root;
	private int numInternalNodes;
	
	//constructor to initialse the object of this class
	public BSTOrderedDictionary() {
		this.numInternalNodes = 0;
		this.root = new BSTNode();
	}
	// getter method to get the root
	public BSTNode getRoot() {
		return this.root;
	}
	//getter method to get the number of internal nodes
	public int getNumInternalNodes() {
		return this.numInternalNodes;
	}
	//getter method to access the arraylist which stores the data
	public ArrayList<MultimediaItem> get(BSTNode r, String k) {
		// algorithm for get method applied
		if(r.isLeaf()) {
			if(k.equals(r.getData().getName())) {
				return r.getData().getMedia();
			}else return null;
		}else {
			if (k.equals(r.getData().getName())) {
				return r.getData().getMedia();
			}else {
				if(k.compareTo(r.getData().getName()) > 0) {
					return get(r.getRightChild(), k);
				}else return get(r.getLeftChild(), k);
			}
		}
	}
	
	//method to add a node to the tree structure
	public void put(BSTNode r, String key, String content, int type) {
		key = key.toLowerCase(); // converts the key to lowercase before adding the key
		MultimediaItem data = new MultimediaItem(content, type);
		NodeData newNode = new NodeData(key);
		
		if(r.getData() == null) { // makes the root/leaf node of the BST with the key and stores the data.
			r.setData(newNode);
			r.getData().add(data);
			this.numInternalNodes++;
		}else {
			if(key.equals(r.getData().getName())) { // if node has the same key then the data is added to the list
				r.getData().add(data);
			}else {
				if(key.compareTo(r.getData().getName()) > 0) { // searches for the node with a key in the right of BST
					if(r.getRightChild() == null) { // if rightchild is null adds the data and creates a new node
						BSTNode right = new BSTNode();
						r.setRightChild(right);
						right.setParent(r);
						right.setData(newNode);
						right.getData().add(data);
						this.numInternalNodes++;
						
					}else { // if rightchild is not null makes a recursive call on rightchild
						put(r.getRightChild(), key, content, type);
					}
				}else {   // searches for the node with a key in the left of BST
					if(r.getLeftChild() == null) { // is leftchild is null adds the data and creates a new node
						BSTNode left = new BSTNode();
						r.setLeftChild(left);
						left.setParent(r);
						left.setData(newNode);
						left.getData().add(data);
						this.numInternalNodes++;
					}else { // if leftchild is not null makes a recursive call on the leftchild
						put(r.getLeftChild(), key, content, type);
					}
				}
				
			}
		}
	}
	
	//method to remove a node in the tree throws DictionaryException if node is not there in the tree
	public void remove(BSTNode r, String k) throws DictionaryException {
		BSTNode p = getNode(r, k); //stores the node in the tree 
		if (p == null) {  // when there no node with key
			throw new DictionaryException("Node " + k +" not in the tree.");
		}else {
			BSTNode parent = p.getParent(); // gets the parent node
			this.numInternalNodes--;
			//follows the remove method algorithm
			if(p.getLeftChild() == null || p.getRightChild() == null) {
				if(p.getLeftChild() == null) {
					BSTNode c = p.getRightChild();
					if(parent == null) {
						c.setParent(null);
					}else {
						if(parent.getLeftChild() == null) {
							parent.setRightChild(c);
						}else parent.setLeftChild(c);
					}
				}else {
					BSTNode c = p.getLeftChild();
					if(parent == null) {
						c.setParent(null);
					}else {
						if(parent.getRightChild() == null) {
							parent.setLeftChild(c);
						}else parent.setRightChild(c);
					}
				}
			} else {
				NodeData s = smallest(p.getRightChild());
				BSTNode small = getNode(r, s.getName());
				remove(small, s.getName());
				p.setData(s);
			}
		}		
	}
	
	//remove method to remove the items in the arraylist of a node
	public void remove(BSTNode r, String k, int type) throws DictionaryException {
		BSTNode data = getNode(r, k); //gets the node with the key
		
		if(data == null) { // when no node is present
			throw new DictionaryException("Node " + k + " not in the tree");
		}else {
			for(int i = 0; i < data.getData().getMedia().size(); i++){
				int j = data.getData().getMedia().get(i).getType();
				if (j == type) {
					data.getData().getMedia().remove(i);
				}else {
					continue;
				}
			}
		}
		 if(data.getData().getMedia().isEmpty()) {
			 remove(r,k);
		 }
	}
	
	//gets the successor of the node with string k
	public NodeData successor(BSTNode r, String k) {
		//follows the successor algorithm
		BSTNode p = getNode(r,k);
		if(r.getData() == null) {
			return null;
		}
		if(p == null) {
			BSTNode n = getPossibleNode(r,k);
			return successor(r, n.getData().getName());
		}
		if(p.getRightChild() !=null) {
			return smallest(p.getRightChild());
		}else {
			BSTNode parent = p.getParent();
			while(parent != null && p == parent.getRightChild()) {
				p = parent;
				parent = parent.getParent();
			} return parent.getData();
		}
	}
	//gets the data stored in the predecessor node
	public NodeData predecessor(BSTNode r, String k) {
		//follows the predecessor algorithm
		BSTNode p = getNode(r,k);
		if(p == null) {
			BSTNode n = getPossibleNode(r,k);
			return predecessor(r, n.getData().getName());
		}
		NodeData pre = null;
		
		if(r.getData() == null) {
			return null;
		}
		
		while(true) {
			
			if(k.compareTo(r.getData().getName()) < 0) {
				r = r.getLeftChild();
			}else if(k.compareTo(r.getData().getName()) > 0) {
				pre = r.getData();
				r = r.getRightChild();
			}else {
				if(r.getLeftChild() != null) {
					pre = largest(r.getLeftChild());
				} break;
			}
			
			if(r.getData() == null) {
				return pre;
			}
		}
		return pre;
		
	}
	
	//finds the smallest node in the tree
	public NodeData smallest(BSTNode r) {
		if(r.getData() == null) {
			return null;
		}
		BSTNode current = r;
		while(current.getLeftChild()!= null) {
			current = current.getLeftChild();
		}
		return current.getData();		
	}
	
	//finds the largest node in the tree
	public NodeData largest(BSTNode r) {
		
		if(r.getData() == null) {
			return null;
		}
		BSTNode current = r;
		while(current.getRightChild() != null) {
			current = current.getRightChild();
		}
		return current.getData();
	}
	
	//private method to get the node storing the key
	private BSTNode getNode(BSTNode r, String k) {
		if(r.isLeaf()) {
			if( k.equals(r.getData().getName())) {
				return r;
			}else return null;
		}else {
			if (k.equals(r.getData().getName())) {
				return r;
			}else {
				if(k.compareTo(r.getData().getName()) > 0) {
					return getNode(r.getRightChild(), k);
				}else return getNode(r.getLeftChild(), k);
			}
		}
	}
	
	//private method to get the possible space for the next node with the key
	private BSTNode getPossibleNode(BSTNode r, String k) {
		if(r.isLeaf()) {
			return r;
		}else {
			if(k.equals(r.getData().getName())) {
				return r;
			}else {
				if(k.compareTo(r.getData().getName()) > 0) {
					return getPossibleNode(r.getRightChild(), k);
				}else return getPossibleNode(r.getLeftChild(), k);
			}
		}
	}
	
	
}
