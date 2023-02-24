
public class Board implements BoardADT {
	
	// variables to store the data
	private char theBoard [][];
	private int size;
	
	/*
	 * constructor to initialise the board to all empty spaces
	 */
	public Board(int board_size, int empty_positions, int max_levels) {
		
		this.size = board_size;
		this.theBoard = new char[size][size];
		
		for (int i = 0; i < board_size; i++) {
			for(int j = 0; j < board_size; j++) {
				theBoard[i][j] = 'e';
			}
		}
		
	}
	/*
	 * makes a dictionary of a large size
	 */
	public Dictionary makeDictionary() {
		
		return new Dictionary(9887);
	}
	
	/*
	 * method to get the score of the a Layout object
	 * stored in the dictionary
	 */
	public int repeatedLayout(Dictionary dict) {
		String s = "";
		
		for (int row = 0; row < theBoard[0].length; row++) {
			for(int col = 0; col < theBoard[1].length; col++) {
				s += theBoard[row][col];
			}
		}
		
		return dict.getScore(s);
		
	}
	
	/*
	 * this method stores the object of Layout in the dictionary 
	 */
	public void storeLayout(Dictionary dict, int score) {
		String s = "";
		
		for (int row = 0; row < theBoard[0].length; row++) {
			for(int col = 0; col < theBoard[1].length; col++) {
				s += theBoard[row][col];
			}
		}
		
		Layout obj = new Layout(s, score); // creates a new Layout object and stores in dictioanry
		try {
			dict.put(obj);
		} catch (DictionaryException e) {
			
		}
		
	}
	/*
	 * stores the character symbol in the board
	 */
	public void saveTile(int row, int col, char symbol) {
		
		theBoard[row][col] = symbol;
	}
	/*
	 * checks if the specified position in the board is empty
	 */
	public boolean positionIsEmpty(int row, int col) {
		
		return theBoard[row][col] == 'e';
		
	}
	/*
	 * checks if the specified position in the board is a computer tile
	 */
	public boolean isComputerTile(int row, int col) {
		
		return theBoard[row][col] == 'c';
		
	}
	/*
	 * checks if the specified position in the board is a human tile
	 */
	public boolean isHumanTile(int row, int col) {
	
		return theBoard[row][col] == 'h';
	}
	
	/*
	 * checks the winner if the same character appears in a straight line
	 */
	public boolean winner(char symbol) {
		boolean win = false;
		
		// checks for rows
		for(int row = 0; row < size; row++) {
			int count = 0;
			for(int col = 0; col < size; col++) {
				
				if(theBoard[row][col] == symbol) count++;
			} if(count == size) win = true;
		}
		// checks for columns
		for(int col = 0; col < size; col++) {
			int count = 0;
			for (int row = 0; row < size; row++) {
				
				if(theBoard[row][col] == symbol) count++;
			} if (count == size) win = true;
			
		}
		//checks diagonals from top left to bottom right
		int tLeft = 0;
		for(int index = 0; index < size; index++) {
			if(theBoard [index][index] == symbol) tLeft++;
		} if(tLeft == size) win = true;
		
		// checks diagonals from top right to bottom left
		int tRight = 0;
		for(int row = 0, col = size-1; row < size; row++, col--) {
			if(theBoard[row][col] == symbol) tRight++; 
		} if (tRight == size) win = true;
		
		return win;
	}
	
	/*
	 * checks if the game is drawn or can the computer or human wins
	 */
	public boolean isDraw(char symbol, int empty_positions) {
		
		boolean draw = false;
		int space = 0;  // checks for filled spaces in the board
		for(int row = 0; row < size; row++) {
			for(int col = 0; col < size; col++) {
				if(theBoard[row][col] != 'e') space++; 
			}
		}
		
		if(empty_positions == 0 && space == (size*size)) draw = true;
		else if(empty_positions > 0 && space == (size*size) - empty_positions){
			// checks for the adjacent elements in the board if they are equal to symbol
			for(int row = 0; row < size; row++) {
				for(int col = 0; col < size; col++) {
					if(theBoard[row][col] == 'e') {
					
						try {
							if(theBoard[row+1][col] == symbol) draw = false;
						}catch(IndexOutOfBoundsException e) {
						}
						try {
							if(theBoard[row-1][col] == symbol) draw = false;
						}catch(IndexOutOfBoundsException e) {
						}
						try {
							if(theBoard[row][col+1] == symbol) draw = false;
						}catch(IndexOutOfBoundsException e) {
						}
						try {
							if(theBoard[row][col-1] == symbol) draw = false;
						}catch(IndexOutOfBoundsException e) {
						}
						try {
							if(theBoard[row+1][col+1] == symbol) draw = false;
						}catch(IndexOutOfBoundsException e) {
						}
						try {
							if(theBoard[row+1][col-1] == symbol) draw = false;
						}catch(IndexOutOfBoundsException e) {
						}
						try {
							if(theBoard[row-1][col+1] == symbol) draw = false;
						}catch(IndexOutOfBoundsException e) {
						}
						try {
							if(theBoard[row-1][col-1] == symbol) draw = false;
						}catch(IndexOutOfBoundsException e) {
						}
						
					}else draw = true;
				}
			}
		}
		return draw;
	}
	
	/*
	 * evaluates who won after in each position of the game
	 */
	public int evaluate(char symbol, int empty_positions) {
		
		if (winner('c')) {
			
			return 3;
		
		}else if(isDraw(symbol, empty_positions)) {
			
			return 2;
		
		}else if(winner('h')) {
		
			return 0;
		
		}else {
		
			return 1;
		}
		
	}
	
}	
