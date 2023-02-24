
import java.util.LinkedList;

public class Dictionary implements DictionaryADT {
	
	//this array stores LinkedList in each of the elements
	private LinkedList<Layout> [] array; 
	
	/*
	 * Constructor of the class 
	 * initialises the array with linkedlist in each element
	 */
	public Dictionary(int size) {
		array = new LinkedList[size];
		for(int i = 0; i < array.length; i++) {
			array[i] = new LinkedList<Layout>();
		}
	}

	/*
	 * this method inserts object in the dictionary according to the key
	 * throws an exception if the key already exists in the dictionary
	 */
	public int put(Layout data) throws DictionaryException{
		
		if( array[hash(data.getBoardLayout())].size() != 0) {
			for(int i = 0; i < array[hash(data.getBoardLayout())].size(); i++) {
				
				Layout node = array[hash(data.getBoardLayout())].get(i);
				
				if(node.getBoardLayout().equals(data.getBoardLayout()))
				
					throw new DictionaryException();
					
			}
				array[hash(data.getBoardLayout())].add(data);
				return 1;
			
			
		}else {
			array[hash(data.getBoardLayout())].add(data);
			return 0;
		}
	}

	/*
	 * this method removes the object from the dictionary with
	 * a specific key provided
	 * throws exception if there is no key in the dictionary
	 */
	public void remove(String boardLayout) throws DictionaryException {
		
		boolean removed = false;
		
		for(int i = 0; i < array[hash(boardLayout)].size(); i++) {
			if (array[hash(boardLayout)].get(i).getBoardLayout().equals(boardLayout)){
				array[hash(boardLayout)].remove(i);
				removed = true;
			}
		}
		if(removed == false) {
			throw new DictionaryException();
		}
		
			
		
	}
	
	/*
	 * gets the data item stored with a specified key
	 */
	public int getScore(String boardLayout) {
		
		int hash = hash(boardLayout);
		int ret = 0;
		
		if(array[hash].size() != 0) {
			for(int i = 0; i < array[hash].size(); i++) {
				if(!array[hash].get(i).getBoardLayout().equals(boardLayout)) {
					ret = -1;
				}
			}
			
			for(int i =0; i < array[hash].size(); i++) {
				if(array[hash].get(i).getBoardLayout().equals(boardLayout)) {
					ret = array[hash].get(i).getScore();
				}
			}
			
		}else {
			ret = -1;
		}
		return ret;
	}
	
	/*
	 * this is the hash function tells the index of
	 * array to put the object into the dictionary
	 */
	private int hash(String s) {
		
		int hashVal = (int)s.charAt(s.length() - 1);
		
		for(int i = s.length() - 2; i >= 0; i--) {
			hashVal = (hashVal * 31 + (int)(s.charAt(i))) % array.length;
		}
		return hashVal;
	}
	
}
