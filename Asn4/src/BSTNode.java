/*
 * Dhruwang Kishorbhai Akbari
 * 251186352
 */
public class BSTNode {
	//private variable for storing data
	private BSTNode parent;
	private BSTNode leftChild;
	private BSTNode rightChild;
	private NodeData data;
	
	//constructor for BSTNode, sets everything to null
	public BSTNode(){
		this.parent = null;
		this.leftChild = null;
		this.rightChild = null;
		this.data = null;
	}
	
	//constructor for BSTNode and setting new variables for each instance variable
	public BSTNode(BSTNode newParent, BSTNode newLeftChild, BSTNode newRightChild, NodeData newData){
		this.parent = newParent;
		this.leftChild = newLeftChild;
		this.rightChild = newRightChild;
		this.data = newData;
	}
	
	//getter method to access the parent
	public BSTNode getParent() {
		return this.parent;
	}
	
	//getter method to access the left child of a node
	public BSTNode getLeftChild() {
		return this.leftChild;
	}
	
	//getter method to access the right child of a node
	public BSTNode getRightChild() {
		return this.rightChild;
	}
	
	//getter method to access the data of a node
	public NodeData getData() {
		return this.data;
	}
	
	//setter method to set a parent
	public void setParent(BSTNode newParent) {
		this.parent = newParent;
	}
	
	//setter method to set the left child
	public void setLeftChild(BSTNode newLeftChild) {
		this.leftChild = newLeftChild;
	}
	
	//setter method to set the right child
	public void setRightChild(BSTNode newRightChild) {
		this.rightChild = newRightChild;
	}
	
	//setter method to set the data
	public void setData(NodeData newData) {
		this.data = newData;
	}
	
	//Check whether the node is a leaf
	public boolean isLeaf() {
		if (this.leftChild == null && this.rightChild == null) {
			return true;
		}else return false;
	}
}
