
public class Layout {
	
	// variables to store the data of the Object Layout
	private String boardLayout;
	private int score;
	
	/*
	 * constructor initialises the variables and stores the data
	 */
	public Layout(String boardLayout, int score) {
		this.boardLayout = boardLayout;
		this.score = score;
		
	}
	/*
	 * getter method to get boardLayout
	 */
	public String getBoardLayout() {
		
		return boardLayout;
	}
	/*
	 * getter method to get score
	 */
	public int getScore() {
		
		return score;
	}
	
}
