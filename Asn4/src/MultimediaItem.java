/*
 * Dhruwang Kishorbhai Akbari
 * 251186352
 */
public class MultimediaItem {
	//instance variables 
	private String content;
	private int type;
	
	//constructor to initialise the object of this class
	public MultimediaItem(String newContent, int newType) {
		this.content = newContent;
		this.type = newType;
	}
	
	//getter method to access the content
	public String getContent() {
		return content;
	}
	
	//getter method to get the type.
	public int getType() {
		return type;
	}

}
