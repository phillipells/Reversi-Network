//import java.util.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.lang.Runtime;
import java.lang.String;
import java.lang.Integer;
import java.io.Console;

class Tuple { 
	public final int x; 
	public final int y; 
	public Tuple(int x, int y) { 
		this.x = x; 
		this.y = y; 
	} 
} 

class reversi {
	
	// Piece enumerated types with names 
	// represented by their "on screen" characters
	public enum Piece {
		EMPTY(" "), WHITE("O"), BLACK("@"), VALID(":");
		private Piece(String name) {
			this.name = name;
		}
		// toString override
		private final String name;
		public String toString() {
			return name;
		}
		// 
		public Piece getOpposite() {
			if (this == Piece.WHITE) {
				return Piece.BLACK;
			} else if (this == Piece.BLACK) {
				return Piece.WHITE;
			} else {
				return Piece.EMPTY;
			}
		}
		//
		public Piece getColor() {
			if (this == Piece.WHITE) {
				return Piece.WHITE;
			} else if (this == Piece.BLACK) {
				return Piece.BLACK;
			} else {
				return Piece.EMPTY;
			}
		}
	}
	
	// Piece objects
	private final static Piece white = Piece.WHITE;
	private final static Piece black = Piece.BLACK;
	private final static Piece empty = Piece.EMPTY;
	private static Piece player1 = Piece.WHITE;
	private static Piece player2 = Piece.BLACK;
	public static String message1 = "Welcome";
	public static String message2 = "> ";
	public static String messageSide1 = " ";
	public static String messageSide2 = " ";
	
	// game board 
	public static Piece[][][] board = new Piece[8][8][62]; // 3d array of pieces: 8X8 board with a max of 62 moves
	
	public static List<Piece[][]> numberValidTree = new LinkedList<Piece[][]>();
	public static List validMovesList = new LinkedList();
	public static List pickSmallest  = new LinkedList();

	// input scanner
	public static Scanner scanner = new Scanner(System.in); 
	// random number generator
	public static Random randomGenerator = new Random();
	
	// other global variables
	private static boolean playerQuit = false; // true when game is quit
	private static boolean gameOver = false; // true when game is finished
	private static boolean localGame = false; // true for local game, false for AI based game
	private static boolean player1Turn = true; // true when its player 1's turn
	private static boolean playerUndo = false; // true when a player performs an undo
	private static boolean endRecursion = false;
	private static boolean setFinish = false;
	private static boolean levelUp = false;
	private static boolean skipTurn = true;
	private static boolean avoidBranch = false;
	private static boolean takeBranch = false;
	private static boolean playAgain = true;
	private static boolean displayOn = true;
	private static int numberValidMoves= 0;
	private static int childNumber = 0;
	private static int parentNumber = 0;
	private static int parentNumber2 = 0;
	private static int min = 100;
	private static int min2 = 100;
	private static Piece aiColor;
	private static int currentMove; // stores the 3rd dimension value to allow a player to undo and redo moves
	private static int furthestRedo; // is the furthest redo level [k] a player can make
	private static int whiteCount; // number of white pieces on the board
	private static int blackCount; // number of black pieces on the board
	
	// alphabetical char array
	private final static char[] alpha = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h' };
	
	// functions
	public static void initializeBoard() {
		// all empty...
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				for(int k=0; k<60; k++){
					board[i][j][k] = empty;
				}
			}
		}
		// except for center four squares
		board[3][4][0] = black;
		board[3][3][0] = white;
		board[4][3][0] = black;
		board[4][4][0] = white;	
		currentMove = 0;
		furthestRedo = 0;
	}
	
	public static void fiftyprintlns() {
		// used to clear screen
		if (displayOn) {
			for(int i=0; i<50; i++) {
				System.out.println("");
			}
		}
	}
	
	public static void printBoard() {
		if (displayOn) {
			// display current state of board
			System.out.println("   a   b   c   d   e   f   g   h");
			System.out.println(" +---+---+---+---+---+---+---+---+");
			for(int i=0; i<8; i++) {
				System.out.print((i+1) + "| ");
				System.out.print(board[0][i][currentMove]);
				System.out.print(" | ");
				System.out.print(board[1][i][currentMove]);
				System.out.print(" | ");
				System.out.print(board[2][i][currentMove]);
				System.out.print(" | ");
				System.out.print(board[3][i][currentMove]);
				System.out.print(" | ");
				System.out.print(board[4][i][currentMove]);
				System.out.print(" | ");
				System.out.print(board[5][i][currentMove]);
				System.out.print(" | ");
				System.out.print(board[6][i][currentMove]);
				System.out.print(" | ");
				System.out.print(board[7][i][currentMove]);
				if (i == 2) {
					System.out.println(" |  SCORE");
				} else if (i == 3) {
					System.out.println(" | White: " + whiteCount);
				} else if (i == 4) {
					System.out.println(" | Black: " + blackCount);
				} else if (i == 6) {
					System.out.println(" | " + messageSide1);
				} else if (i == 7) {
					System.out.println(" | " + messageSide2);
				} else {
					System.out.println(" |");
				}
				System.out.println(" +---+---+---+---+---+---+---+---+");
			}
		} else {
			// display is off
			System.out.println("Display is off");
		}
		System.out.println(message1);
		System.out.print(message2);
		
		// reset side messages
		messageSide1 = "";
		messageSide2 = "";
	}
	
	public static void printValidMoves(Piece player) {
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++){
				if (isValidMove(i, j, player, true)) {
					board[i][j][currentMove] = player.VALID;
				}
			}
		}
		printBoard();
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++){
				if (isValidMove(i, j, player, true)) {
					board[i][j][currentMove] = player.EMPTY;
				}
			}
		}
	}
	
		
	public static void printMenu() {
		System.out.println("");
		System.out.println("                     REVERSI!"								);
		System.out.println("=========================================================="	);
		System.out.println(" *Enter \"quit\" to quit."									);
		System.out.println(" *Enter \"display_off\" to turn off the display."			);
		System.out.println(" *Enter \"display_on\" to turn on the display."				);
		System.out.println(" *Enter \"undo\" to undo your previous move."				);
		System.out.println(" *Enter \"redo\" to redo your previous move."				);
		System.out.println(" *Enter the column and row (ex: 'd3')  to place a piece."	);
	}
	
	public static void copyState(){
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++){
				if (board[i][j][currentMove] != empty) {
					board[i][j][currentMove+1] = board[i][j][currentMove];
				}
				else {
					board[i][j][currentMove+1] = empty;
				}
			}
		}
	}

	public static void copyState(Piece[][] fromState, Piece[][] toState){
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++){
				if (fromState[i][j] != empty) {
					toState[i][j] = fromState[i][j];
				}
				else {
					toState[i][j] = empty;
				}
			}
		}
	}
	
	public static void undo(){
		if (localGame) {
			if (playerUndo != true){  //player hasn't performed an undo yet
				furthestRedo = currentMove;
			}
			if (currentMove > 0){
				playerUndo = true;
				currentMove --;
				printBoard();
				player1Turn = !player1Turn; // switch player
			}
			else {
				messageSide2 = "Cannot undo";
			}
		} else {
			// AI based undo
			if (playerUndo != true){ 	
				furthestRedo = currentMove;
			}
			if (currentMove > 1){
				playerUndo = true;
				currentMove -= 2;
				printBoard();
				// go back two moves and keep the same player
			}
			else {
				messageSide2 = "Cannot undo";
			}
		}
	}
	
	public static void redo(){
		if (localGame) {
			if (currentMove+1 <= furthestRedo){
				currentMove++;
				printBoard();
				player1Turn = !player1Turn; // switch player
			}
			else{
				messageSide2 = "Cannot redo";
			}
		} else {
			if (currentMove+2 <= furthestRedo){
				currentMove += 2;
				printBoard();
				// progress forward two moves and keep the same player
			}
			else{
				messageSide2 = "Cannot redo";
			}
		}
	}
	
	public static void display(boolean on) {
		if (displayOn) {
			if (on) {
				messageSide2 = "The display is already ON";
			} else {
				displayOn = false;
			}
		} else {
			if (on) {
				displayOn = true;
			} else {
				System.out.println("The display is already OFF");
			}
		}
	}
	
	public static void getInput(Piece player) {
		String input;
		int row, column;
		message2 = "> ";
		input = scanner.next();
		if (input.equalsIgnoreCase("quit")) {
			playerQuit = true;
			return;
		} else if (input.equalsIgnoreCase("undo")) {
			undo();
			return;
		} else if (input.equalsIgnoreCase("display_off")) {
			display(false);
			return;
		} else if (input.equalsIgnoreCase("display_on")) {
			display(true);
			return;
		} else if (input.equalsIgnoreCase("redo")) {
			redo();
			return;
		} else if (input.length() != 2) {
			printBoard();
			messageSide1 = ("Invalid input: " + input + "; ");
			messageSide2 = "Try Again";
			return;
		} else {
			// interpret entry into row and column
			switch (input.charAt(0)) {
				case 'a':
					column = 0;
					break;
				case 'b':
					column = 1;
					break;
				case 'c':
					column = 2;
					break;
				case 'd':
					column = 3;
					break;
				case 'e':
					column = 4;
					break;
				case 'f':
					column = 5;
					break;
				case 'g':
					column = 6;
					break;
				case 'h':
					column = 7;
					break;
				default:
					messageSide2 = "Invalid Column";
					return;
			}
			switch (input.charAt(1)) {
				case '1':
					row = 0;
					break;
				case '2':
					row = 1;
					break;
				case '3':
					row = 2;
					break;
				case '4':
					row = 3;
					break;
				case '5':
					row = 4;
					break;
				case '6':
					row = 5;
					break;
				case '7':
					row = 6;
					break;
				case '8':
					row = 7;
					break;
				default:
					messageSide2 = "Invalid Row";
					return;
			}
			// perform move
			playerUndo = false;
			furthestRedo = currentMove;
			copyState();
			move(column, row, player);
		}
	}
	
	public static void move(int column, int row, Piece player) {
		// if valid move...
		if (isValidMove(column, row, player, false)) {
			currentMove++;
			board[column][row][currentMove] = player.getColor();
			updateScore();
			player1Turn = !player1Turn; // switch player
		} else {
			messageSide2 = "Invalid move. Try again";
		}
	}
	
	public static boolean checkNeighbors(int column, int row, Piece player, boolean next) {		
		int i = 1;
		boolean anyValid = false;
		
		if (isInBounds(column, row-i)) { // North Side
			if(board[column][row-i][currentMove] == player.getOpposite()) {
				i++; // neighbor is opposite
				while(isInBounds(column, row-i)) {
					if (board[column][row-i][currentMove] == player.getOpposite()) {
						i++;
					} else if (board[column][row-i][currentMove] == player.getColor()) {
						if (next != true){
							flipPieces(column,column,row,row-i);
						}
						anyValid = true;
						break;
					} else { break; }
				}
			}
		}
		i = 1;
		if (isInBounds(column+i, row)) { // East Side
			if(board[column+i][row][currentMove] == player.getOpposite()) {
				i++;
				while(isInBounds(column+i, row)) {
					if (board[column+i][row][currentMove] == player.getOpposite()) {
						i++;
					} else if (board[column+i][row][currentMove] == player.getColor()) {
						if (next != true){
							flipPieces(column,column+i,row,row);
						}
						anyValid = true;
						break;
					} else { break; }
				}
			}
		}
		i = 1;
		if (isInBounds(column, row+i)) { // South Side
			if(board[column][row+i][currentMove] == player.getOpposite()) {
				i++;
				while(isInBounds(column, row+i)) {
					if (board[column][row+i][currentMove] == player.getOpposite()) {
						i++;
					} else if (board[column][row+i][currentMove] == player.getColor()) {
						if (next != true){
							flipPieces(column,column,row,row+i);
						}
						anyValid = true;
						break;
					} else { break; }
				}
			}
		}
		i = 1;
		if (isInBounds(column-i, row)) { // West Side
			if(board[column-i][row][currentMove] == player.getOpposite()) {
				i++;
				while(isInBounds(column-i, row)) {
					if (board[column-i][row][currentMove] == player.getOpposite()) {
						i++;
					} else if (board[column-i][row][currentMove] == player.getColor()) {
						if (next != true){
							flipPieces(column,column-i,row,row);
						}
						anyValid = true;
						break;
					} else { break; }
				}
			}
		}
		return anyValid;
	}
	
	public static boolean checkDiagonals(int column, int row, Piece player, boolean next) {
		int i = 1;
		boolean anyValid = false;
		if (isInBounds(column+i, row-i)) { // Northeast Side
			if(board[column+i][row-i][currentMove] == player.getOpposite()) {
				i++; // neighbor is opposite
				while(isInBounds(column+i, row-i)) {
					if (board[column+i][row-i][currentMove] == player.getOpposite()) {
						i++;
					} else if (board[column+i][row-i][currentMove] == player.getColor()) {
						if (next != true){
							flipPieces(column,column+i,row,row-i);
						}
						anyValid = true;
						break;
					} else { break; }
				}
			}
		}
		i = 1;
		if (isInBounds(column+i, row+i)) { // Southeast Side
			if(board[column+i][row+i][currentMove] == player.getOpposite()) {
				i++; // neighbor is opposite
				while(isInBounds(column+i, row+i)) {
					if (board[column+i][row+i][currentMove] == player.getOpposite()) {
						i++;
					} else if (board[column+i][row+i][currentMove] == player.getColor()) {
						if (next != true){
							flipPieces(column,column+i,row,row+i);
						}
						anyValid = true;
						break;
					} else { break; }
				}
			}
		}
		i = 1;
		if (isInBounds(column-i, row+i)) { // Southwest Side
			if(board[column-i][row+i][currentMove] == player.getOpposite()) {
				i++; // neighbor is opposite
				while(isInBounds(column-i, row+i)) {
					if (board[column-i][row+i][currentMove] == player.getOpposite()) {
						i++;
					} else if (board[column-i][row+i][currentMove] == player.getColor()) {
						if (next != true){
							flipPieces(column,column-i,row,row+i);
						}
						anyValid = true;
						break;
					} else { break; }
				}
			}
		}
		i = 1;
		if (isInBounds(column-i, row-i)) { // Northwest Side
			if(board[column-i][row-i][currentMove] == player.getOpposite()) {
				i++; // neighbor is opposite
				while(isInBounds(column-i, row-i)) {
					if (board[column-i][row-i][currentMove] == player.getOpposite()) {
						i++;
					} else if (board[column-i][row-i][currentMove] == player.getColor()) {
						if (next != true){
							flipPieces(column,column-i,row,row-i);
						}
						anyValid = true;
						break;
					} else { break; }
				}
			}
		}
		return anyValid;
	}
	
	public static boolean checkBranchNeighbors(int column, int row, Piece player, Piece[][] branch) {		
		int i = 1;
		boolean anyValid = false;
		
		if (isInBounds(column, row-i)) { // North Side
			if(branch[column][row-i] == player.getOpposite()) {
				i++; // neighbor is opposite
				while(isInBounds(column, row-i)) {
					if (branch[column][row-i] == player.getOpposite()) {
						i++;
					} else if (branch[column][row-i]  == player.getColor()) {
						flipBranchPieces(column,column,row,row-i,branch);
						anyValid = true;
						break;
					} else { break; }
				}
			}
		}
		i = 1;
		if (isInBounds(column+i, row)) { // East Side
			if(branch[column+i][row]  == player.getOpposite()) {
				i++;
				while(isInBounds(column+i, row)) {
					if (branch[column+i][row]  == player.getOpposite()) {
						i++;
					} else if (branch[column+i][row]  == player.getColor()) {
						flipBranchPieces(column,column+i,row,row,branch);
						anyValid = true;
						break;
					} else { break; }
				}
			}
		}
		i = 1;
		if (isInBounds(column, row+i)) { // South Side
			if(branch[column][row+i]  == player.getOpposite()) {
				i++;
				while(isInBounds(column, row+i)) {
					if (branch[column][row+i]  == player.getOpposite()) {
						i++;
					} else if (branch[column][row+i]  == player.getColor()) {
						flipBranchPieces(column,column,row,row+i,branch);
						anyValid = true;
						break;
					} else { break; }
				}
			}
		}
		i = 1;
		if (isInBounds(column-i, row)) { // West Side
			if(branch[column-i][row]  == player.getOpposite()) {
				i++;
				while(isInBounds(column-i, row)) {
					if (branch[column-i][row]  == player.getOpposite()) {
						i++;
					} else if (branch[column-i][row]  == player.getColor()) {
						flipBranchPieces(column,column-i,row,row,branch);
						anyValid = true;
						break;
					} else { break; }
				}
			}
		}
		return anyValid;
	}
	
	public static boolean checkBranchDiagonals(int column, int row, Piece player, Piece[][] branch) {
		int i = 1;
		boolean anyValid = false;
		if (isInBounds(column+i, row-i)) { // Northeast Side
			if(branch[column+i][row-i]  == player.getOpposite()) {
				i++; // neighbor is opposite
				while(isInBounds(column+i, row-i)) {
					if (branch[column+i][row-i]  == player.getOpposite()) {
						i++;
					} else if (branch[column+i][row-i]  == player.getColor()) {
						flipBranchPieces(column,column+i,row,row-i,branch);
						anyValid = true;
						break;
					} else { break; }
				}
			}
		}
		i = 1;
		if (isInBounds(column+i, row+i)) { // Southeast Side
			if(branch[column+i][row+i]  == player.getOpposite()) {
				i++; // neighbor is opposite
				while(isInBounds(column+i, row+i)) {
					if (branch[column+i][row+i]  == player.getOpposite()) {
						i++;
					} else if (branch[column+i][row+i]  == player.getColor()) {
						flipBranchPieces(column,column+i,row,row+i,branch);
						anyValid = true;
						break;
					} else { break; }
				}
			}
		}
		i = 1;
		if (isInBounds(column-i, row+i)) { // Southwest Side
			if(branch[column-i][row+i]  == player.getOpposite()) {
				i++; // neighbor is opposite
				while(isInBounds(column-i, row+i)) {
					if (branch[column-i][row+i]  == player.getOpposite()) {
						i++;
					} else if (branch[column-i][row+i]  == player.getColor()) {
						flipBranchPieces(column,column-i,row,row+i,branch);
						anyValid = true;
						break;
					} else { break; }
				}
			}
		}
		i = 1;
		if (isInBounds(column-i, row-i)) { // Northwest Side
			if(branch[column-i][row-i]  == player.getOpposite()) {
				i++; // neighbor is opposite
				while(isInBounds(column-i, row-i)) {
					if (branch[column-i][row-i]  == player.getOpposite()) {
						i++;
					} else if (branch[column-i][row-i]  == player.getColor()) {
						flipBranchPieces(column,column-i,row,row-i,branch);
						anyValid = true;
						break;
					} else { break; }
				}
			}
		}
		return anyValid;
	}
	
	public static boolean isValidMove(int column, int row, Piece player, boolean next) { 
		boolean valid;
		if (board[column][row][currentMove] == Piece.VALID) {
			return true;
		} else if (board[column][row][currentMove] != Piece.EMPTY) {
			if (next != true){
				messageSide2 = ("Space (" + alpha[column] + "," + (row+1) + ") is not empty");
			}
			return false;
		} else { 
			valid = checkDiagonals(column, row, player, next); // has to be set here, because || is lazy
			return ( checkNeighbors(column, row, player, next) || valid );
		}
	}
	
	public static boolean isBranchValidMove(int column, int row, Piece player, Piece[][] branch) { 
		boolean valid;
		if (branch[column][row] == Piece.VALID) {
			return true;
		} else if (branch[column][row] != Piece.EMPTY) {
			return false;
		} else { 
			valid = checkBranchDiagonals(column, row, player, branch); // has to be set here, because || is lazy
			return ( checkBranchNeighbors(column, row, player, branch) || valid );
		}
	}		
		
	public static boolean isInBounds(int column, int row) {
		if (column > 7 ||
			row > 7 ||
			column < 0 ||
			row < 0 ) {
				return false;
		} else { 
			return true; 
		}
	}
	
	public static void flipPieces(int startCol, int endCol, int startRow, int endRow) {
		int temp;
		if (startCol == endCol) {
			if (startRow > endRow) {
				temp = startRow;
				startRow = endRow;
				endRow = temp;
			}
			for(int i=(startRow+1); i<endRow; i++) {
				board[startCol][i][currentMove+1] = board[startCol][i][currentMove].getOpposite();
			}
		} else if (startRow == endRow) {
			if (startCol > endCol) {
				temp = startCol;
				startCol = endCol;
				endCol = temp;
			}
			for(int i=(startCol+1); i<endCol; i++) {
				board[i][startRow][currentMove+1] = board[i][startRow][currentMove].getOpposite();
			}
		} else {
			boolean rowAsc = false, colAsc = false;
			if (startRow < endRow) {
				rowAsc = true;
			}if (startCol < endCol) {
				colAsc = true;
			}
			
			if (colAsc && rowAsc) {
				int j = (startRow+1);
				for(int i=(startCol+1); i<(endCol); i++, j++) {
					board[i][j][currentMove+1] = board[i][j][currentMove].getOpposite();
				}
			} else if (colAsc && !rowAsc) {
				int j = (startRow-1);
				for(int i=(startCol+1); i<(endCol); i++, j--) {
					board[i][j][currentMove+1] = board[i][j][currentMove].getOpposite();
				}
			} else if (!colAsc && rowAsc) {
				int j = (startRow+1);
				for(int i=(startCol-1); i>(endCol); i--, j++) {
					board[i][j][currentMove+1] = board[i][j][currentMove].getOpposite();
				}
			} else if (!colAsc && !rowAsc) {
				int j = (startRow-1);
				for(int i=(startCol-1); i>(endCol); i--, j--) {
					board[i][j][currentMove+1] = board[i][j][currentMove].getOpposite();
				}
			}
		}
	}
	
	public static void flipBranchPieces(int startCol, int endCol, int startRow, int endRow, Piece[][] branch) {
		int temp;
		if (startCol == endCol) {
			if (startRow > endRow) {
				temp = startRow;
				startRow = endRow;
				endRow = temp;
			}
			for(int i=(startRow+1); i<endRow; i++) {
				branch[startCol][i] = branch[startCol][i].getOpposite();
			}
		} else if (startRow == endRow) {
			if (startCol > endCol) {
				temp = startCol;
				startCol = endCol;
				endCol = temp;
			}
			for(int i=(startCol+1); i<endCol; i++) {
				branch[i][startRow] = branch[i][startRow].getOpposite();
			}
		} else {
			boolean rowAsc = false, colAsc = false;
			if (startRow < endRow) {
				rowAsc = true;
			}if (startCol < endCol) {
				colAsc = true;
			}
			
			if (colAsc && rowAsc) {
				int j = (startRow+1);
				for(int i=(startCol+1); i<(endCol); i++, j++) {
					branch[i][j] = branch[i][j].getOpposite();
				}
			} else if (colAsc && !rowAsc) {
				int j = (startRow-1);
				for(int i=(startCol+1); i<(endCol); i++, j--) {
					branch[i][j] = branch[i][j].getOpposite();
				}
			} else if (!colAsc && rowAsc) {
				int j = (startRow+1);
				for(int i=(startCol-1); i>(endCol); i--, j++) {
					branch[i][j] = branch[i][j].getOpposite();
				}
			} else if (!colAsc && !rowAsc) {
				int j = (startRow-1);
				for(int i=(startCol-1); i>(endCol); i--, j--) {
					branch[i][j] = branch[i][j].getOpposite();
				}
			}
		}
	}
	
	public static void updateScore() {
		whiteCount = 0;
		blackCount = 0;
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				if (board[i][j][currentMove] == white) {
					whiteCount++;
				} else if (board[i][j][currentMove] == black) {
					blackCount++;
				}
			}
		}
	}
	
	public static void checkGameStatus(Piece player) {
		boolean validMoves = false;
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				if (isValidMove(i,j,player, true)){ //current player has a valid move; 
					gameOver = false;
					return;
				} else { // current player has no valid moves
					gameOver = true;
				}
			}
		}
		player = player.getOpposite(); // switch player
		boardSearch: for(int k=0; k<8; k++){ // check if the next player has any valid moves
			for(int l=0; l<8; l++){
				if (isValidMove(k,l,player, true)){ //next player has a valid move;
					gameOver = false;
					player1Turn = !player1Turn;
					if (player.getOpposite() == Piece.WHITE){ 
						messageSide1 = "White does not have any valid moves";
						messageSide2 = "White loses a turn";
					} else {
						messageSide1 = "Black does not have any valid moves";
						messageSide2 = "Black loses a turn";
					}
					break boardSearch;
				} else { // next player has no valid moves either
					gameOver = true;
				}
			}
		}
	}
	
	public static void checkWinner() {
		if (playerQuit) {
			message1 = "Player quit.";
			return;
		}
		whiteCount = 0;
		blackCount = 0;
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				if (board[i][j][currentMove] == white) {
					whiteCount++;
				} else if (board[i][j][currentMove] == black) {
					blackCount++;
				}
			}
		}
		message1 = ("\n GAME OVER \n===========\n White: " + whiteCount + "\n Black: " + blackCount);
		if (whiteCount > blackCount) {
			if (player1 == white) {
				message2 = "Player 1 wins!\n";
			} else {
				message2 = "Player 2 wins!\n";
			}
		} else if (whiteCount < blackCount) {
			if (player2 == white) {
				message2 = "Player 1 wins!\n";
			} else {
				message2 = "Player 2 wins!\n";
			}
		} else {
			message2 = "Game Is A Draw\n";
		}
	}
	
	// 2 player local
	public static void gameLoop1() {
		gameOver = false;
		while (!playerQuit && !gameOver) {
			if(player1Turn) {
				if (player1 == Piece.WHITE){
					message1 = "Player 1's turn (O)";
				}
				else {
					message1 = "Player 1's turn (@)";
				}
				message2 = "> ";
				printValidMoves(player1);
				updateScore();
				getInput(player1);
				checkGameStatus(player2);
			} else {
				if (player2 == Piece.WHITE){
					message1 = "Player 2's turn (0)";
				}
				else {
					message1 = "Player 2's turn (@)";
				}
				message2 = "> ";
				printValidMoves(player2);
				updateScore();
				getInput(player2); // switch to player1 if you don't ever want to switch players
				checkGameStatus(player1);
			}
			fiftyprintlns();
		}
		checkWinner();
		printBoard();
	}
	
	// easy AI
	public static void gameLoop2() {
		gameOver = false;
		while (!playerQuit && !gameOver) {
			if(player1Turn) {
				if (player1 == Piece.WHITE){
					message1 = "Player 1's turn (O)";
				}
				else {
					message1 = "Player 1's turn (@)";
				}
				message2 = "> ";
				printValidMoves(player1);
				getInput(player1);
				checkGameStatus(player2);
			} else {
				try{
					message1 = "AI playing...";
					message2 = "";
					printValidMoves(player2);
					Thread.sleep(2000);
					aiWorstMove();
					checkGameStatus(player1);
				} catch(InterruptedException ie){}
			}
			fiftyprintlns();
		}
		checkWinner();
		printBoard();
	}
	
	// medium AI
	public static void gameLoop3() {
		gameOver = false;
		while (!playerQuit && !gameOver) {
			if(player1Turn) {
				if (player1 == Piece.WHITE){
					message1 = "Player 1's turn (O)";
				}
				else {
					message1 = "Player 1's turn (@)";
				}
				message2 = "> ";
				printValidMoves(player1);
				getInput(player1);
				checkGameStatus(player2);
			} else {
				try{
					message1 = "AI playing...";
					message2 = "";
					printValidMoves(player2);
					Thread.sleep(2000);
					aiMostFlips();
					checkGameStatus(player1);
				} catch(InterruptedException ie){}
			}
			fiftyprintlns();
		}
		checkWinner();
		printBoard();
	}
	
	// hard AI
	public static void gameLoop4() {
		gameOver = false;
		while (!playerQuit && !gameOver) {
			if(player1Turn) {
				if (player1 == Piece.WHITE){
					message1 = "Player 1's turn (O)";
				}
				else {
					message1 = "Player 1's turn (@)";
				}
				message2 = "> ";
				printValidMoves(player1);
				getInput(player1);
				checkGameStatus(player2);
			} else {
				try {
					message1 = "AI playing...";
					message2 = "";
					printValidMoves(player2);
					long startTime = System.currentTimeMillis();
					aiHardMove(player2);
					long endTime = System.currentTimeMillis();
					int execTime = (int)(endTime-startTime);
					if (execTime < 2000) {
						Thread.sleep(2000 - execTime);
					}
					messageSide1 = ("AI decided in " + execTime + " mS");
					checkGameStatus(player1);
				} catch(InterruptedException ie) {}
			}
			fiftyprintlns();
		}
		checkWinner();
		printBoard();	
	}
	
	// random AI
	public static void gameLoop5() {
		gameOver = false;
		while (!playerQuit && !gameOver) {
			if(player1Turn) {
				if (player1 == Piece.WHITE) {
					message1 = "Player 1's turn (O)";
				} else {
					message1 = "Player 1's turn (@)";
				}
				message2 = "> ";
				printValidMoves(player1);
				getInput(player1);
				checkGameStatus(player2);
			} else {
				try{
					message1 = "AI playing...";
					message2 = "";
					printValidMoves(player2);
					Thread.sleep(2000);
					aiRandomMove();
					checkGameStatus(player1);
				} catch(InterruptedException ie){}
			}
			fiftyprintlns();
		}
		checkWinner();
		printBoard();
	}
	
	// start the game, gets some info from users
	public static void gameStart() {
		initializeBoard();
		int gameSelection = 0;
		String gameStarting = "";
		messageSide2 = "Welcome to Reversi!";
		
		// initial piece counts
		whiteCount = 2;
		blackCount = 2;
		boolean validGameSelection = false;
		
		// selection of game mode
		System.out.println(" Select game mode:");
		System.out.println("+-----------------+");
		System.out.println(" 1. 2 player local");
		System.out.println(" 2. 1 player easy");
		System.out.println(" 3. 1 player medium");
		System.out.println(" 4. 1 player hard");
		System.out.println(" 5. 1 player (Random)");
		
		// get input for game selection
		while (!validGameSelection) {
			try {
				System.out.print("> ");
				gameSelection = scanner.nextInt();
				switch (gameSelection) {
					case 1:
						gameStarting = "2 player: Local starting...";
						validGameSelection = true;
						break;
					case 2:
						gameStarting = "1 player: Easy starting...";
						validGameSelection = true;
						break;
					case 3:
						gameStarting = "1 player: Medium starting...";
						validGameSelection = true;
						break;
					case 4:
						gameStarting = "1 player: Hard starting...";
						validGameSelection = true;
						break;
					case 5:
						gameStarting = "1 player v.s. Random AI starting..";
						validGameSelection = true;
						break;
					default:
						System.out.println("Invalid game choice");
						break;
				}
			} catch (Exception e) {
				System.out.println("You didnt enter an integer, did you?");
				scanner.next();
			}
		}
			
		System.out.print("Player 1, select you're color (W/w - white, B/b - black): ");
		while (true) {
			String colorSelection = scanner.next();
			if (colorSelection.toLowerCase().equals("w")) {
				player1 = Piece.WHITE;
				player2 = Piece.BLACK;
				System.out.println("Player 1 is white.");
				player1Turn = false;
				break;
			} else if (colorSelection.toLowerCase().equals("b")) {
				player1 = Piece.BLACK;
				player2 = Piece.WHITE;
				System.out.println("Player 1 is black.");
				player1Turn = true;
				break;
			} else {
				System.out.print("Invalid Choice. Try again: ");
			}
		}
		System.out.println(gameStarting);
		printMenu();
		playGame(gameSelection);
	}
	
	// ai chooses a random available move
	public static void aiRandomMove() {
		// list of integer coordinates
		List<Tuple> validSpaces = new ArrayList<Tuple>(); 
		
		// add all valid moves to list
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++){
				if (isValidMove(i, j, player2, true)) {
					validSpaces.add(new Tuple(i,j));
				}
			}
		}
		
		// get one at random
		if (validSpaces.size() == 0){ // AI has no available moves
			return;
		}
		
		int rand = randomGenerator.nextInt(validSpaces.size());
		int col = validSpaces.get(rand).x;
		int row = validSpaces.get(rand).y;
		
		// and move
		playerUndo = false;
		furthestRedo = currentMove;
		copyState();
		move(col,row,player2);	
	}
	
	// ai choosing moves determined by most pieces flipped
	public static void aiMostFlips() {
		// list of integer coordinates
		List<Tuple> validSpaces = new ArrayList<Tuple>(); 
		
		// add all valid moves to list
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++){
				if (isValidMove(i, j, player2, true)) {
					validSpaces.add(new Tuple(i,j));
				}
			}
		}
		
		// get one at random
		if (validSpaces.size() == 0){ // AI has no available moves
			return;
		}
		
		Tuple bestMove = new Tuple(0,0);
		int current, max = 0;
		for (Tuple space : validSpaces) {
			current = numPiecesFlipped(space.x, space.y);
			if (current > max) {
				max = current;
				bestMove = new Tuple(space.x,space.y);
			}
		}
		playerUndo = false;
		furthestRedo = currentMove;
		copyState();
		move(bestMove.x, bestMove.y, player2);
		player1Turn = true;
	}
	
	// "bad" ai for easy mode
	public static void aiWorstMove() {
		// list of integer coordinates
		List<Tuple> validSpaces = new ArrayList<Tuple>(); 
		
		// add all valid moves to list
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++){
				if (isValidMove(i, j, player2, true)) {
					validSpaces.add(new Tuple(i,j));
				}
			}
		}
		
		if (validSpaces.size() == 0){ // AI has no available moves
			return;
		}
		
		Tuple worstMove = new Tuple(0,0);
		int current, min = 64;
		for (Tuple space : validSpaces) {
			current = numPiecesFlipped(space.x, space.y);
			if (current < min) {
				min = current;
				worstMove = new Tuple(space.x,space.y);
			}
		}
		playerUndo = false;
		furthestRedo = currentMove;
		copyState();
		move(worstMove.x, worstMove.y, player2);
		player1Turn = true;
	}
	
	// ai chooses move that leaves the least available spaces for the player
	public static void aiHardMove(Piece player){
		aiColor  = player;
		Tuple best = new Tuple (1,1); 
		best = smartMove(player);
		playerUndo = false;
		furthestRedo = currentMove;
		copyState();
		move(best.x, best.y, player);
	}
	
	// smart move
	public static Tuple smartMove(Piece player){
		//copy current board state to branch
		Piece[][] branch = new Piece[8][8];
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++){
				if (branch[i][j] != empty) {
					branch[i][j] = board[i][j][currentMove];
				}
				else {
					branch[i][j] = empty;
				}
			}
		}
		Piece[][] topLevel = branch;
		numberValidTree.add(topLevel);
		int smartCol = 0;
		int smartRow = 0;
		for(int row=0; row<8; row++){
			for(int col=0; col<8; col++) {
				if (isValidMove(col, row, player, true)) {
					//****Always take the corners****
					if ((row==0 && col==0) || // top left corner
						(row==0 && col==7) || // top right corner
						(row==7 && col==0) || // bottom left corner
						(row==7 && col==7) ){ // bottom right corner
							smartCol = col;
							smartRow = row;
							Tuple smartest = new Tuple (smartCol,smartRow);
							return smartest;
					}
					//*******************************
				}
			}
		}
		
		int fullDepth = 6; // number of turns ahead
		int currentDepth = 0;
		recursive (numberValidTree.get(0), player, fullDepth, currentDepth);
		int bestMove = 0;
		int smallest = 100;
		for (int i=0; i<pickSmallest.size(); i++){
			int temp = Integer.parseInt(pickSmallest.get(i).toString());
			if (temp < smallest){
				smallest = temp;
				bestMove = i;
			}
		}
		int counter = 0;
		loop: for(int row=0; row<8; row++){
			for(int col=0; col<8; col++) {
				if (isValidMove(col, row, player, true)) {
					if (bestMove == counter){
						smartCol = col;
						smartRow = row;
						break loop;
					}
					counter++;
				}
			}
		}
		numberValidTree.clear();
		validMovesList.clear();
		pickSmallest.clear();
		Tuple smartest = new Tuple (smartCol,smartRow);
		return smartest;
	}
	
	// sets avoidBranch to true if the valid move is a bad move
	public static void avoidCases(Piece player, int col, int row, int depth, Piece[][] currentBoard){
		if (player == aiColor.getOpposite() && // if player = human; && col/row is a corner
			(depth == 2) &&(
			(row==0 && col==0) || // top left corner
			(row==0 && col==7) || // top right corner
			(row==7 && col==0) || // bottom left corner
			(row==7 && col==7) )){ // bottom right corner
			avoidBranch = true;
		}
		if (player == aiColor &&  // if player = AI; && col/row is a diagonal adjacent to the corner
			(depth == 1) &&(
			(row==1 && col==1) || 
			(row==1 && col==6) || 
			(row==6 && col==1) || 
			(row==6 && col==6) )){
			avoidBranch = true;
		}
		if (player == aiColor &&  // if player = AI; && col/row is an edge next to the corner
			(depth == 1) &&(
			((row==0 && col==1) && currentBoard[2][0] == aiColor.getOpposite() ) || //top left corner; east
			((row==1 && col==0) && currentBoard[0][2] == aiColor.getOpposite() ) || //top left corner; south
			((row==0 && col==6) && currentBoard[5][0] == aiColor.getOpposite() ) || //top right corner; west
			((row==1 && col==7) && currentBoard[7][2] == aiColor.getOpposite() ) || //top right corner; south
			((row==7 && col==1) && currentBoard[2][7] == aiColor.getOpposite() ) || //bottom left corner; east
			((row==6 && col==0) && currentBoard[0][5] == aiColor.getOpposite() ) || //bottom left corner; north
			((row==7 && col==6) && currentBoard[5][7] == aiColor.getOpposite() ) || //bottom right corner; west
			((row==6 && col==7) && currentBoard[7][5] == aiColor.getOpposite() ) )){ //bottom right corner; north
			avoidBranch = true;
		}
	}
	
	// recursive depth search
	public static void recursive(Piece[][] Node, Piece player, int fullDepth, int currentDepth){
		skipTurn = true;
		currentDepth++;
		if ((numberValidTree.size()-1) >= fullDepth){
			numberValidMoves++;
			endRecursion = true;
			skipTurn = false;
			return;
		} else {
			for(int row=0; row<8; row++){
				for(int col=0; col<8; col++) {
					if (endRecursion == false){
						Piece[][] child = new Piece[8][8];
						copyState(Node, child);
						if (isBranchValidMove(col, row, player, child)) {
							avoidCases(player, col, row, currentDepth, child);
							skipTurn = false;
							child[col][row] = player.getColor();
							numberValidTree.add(child);
							player = player.getOpposite();
							recursive(child, player, fullDepth, currentDepth);
							player = player.getOpposite();
						}
					} else {
						endRecursion = false;
						numberValidTree.remove(numberValidTree.size()-1);
						Piece[][] temp = numberValidTree.get(numberValidTree.size()-1);
						Piece[][] child2 = new Piece[8][8];
						copyState(temp, child2);
						if (isBranchValidMove(col, row, player, child2)) {
							avoidCases(player, col, row, currentDepth, child2);
							skipTurn = false;
							child2[col][row] = player.getColor();
							numberValidTree.add(child2);
							player = player.getOpposite();
							recursive(child2, player, fullDepth, currentDepth);
							player = player.getOpposite();
						}
					}
				}
			}
			
			if (skipTurn == true){				
				if (currentDepth == 2){
					parentNumber2 = -1;
				}
				if (currentDepth == 4){
					parentNumber = -1;
				}
				if (currentDepth == 6){
					min = 0;
				}
				if (endRecursion == false){
					Piece[][] child = new Piece[8][8];
					copyState(Node, child);
					numberValidTree.add(child);
					player = player.getOpposite();
					recursive(child, player, fullDepth, currentDepth);
					player = player.getOpposite();
				}
			}
			//*************************
			if (avoidBranch == true){
				parentNumber2 = 100;
				avoidBranch = false;
			}
			//*************************
			
			if (validMovesList.size()>0){
				String childEnd = (validMovesList.get(validMovesList.size()-1)).toString();
				if (numberValidMoves == 0 && 
					childEnd == "end of child"){
					//*********** depth 6
					if (min == 100){
						pickSmallest.add(parentNumber2+min2);
						parentNumber2 = 0;
						min2 = 100;
					}
					if ((parentNumber+min)<min2){
						min2 = (parentNumber+min);
					}
					//*********** depth 6
					validMovesList.add("min = "+min);
					validMovesList.add(parentNumber+10000);
					parentNumber = 0;
					min = 100;
					numberValidTree.remove(numberValidTree.size()-1);
					return;
				}
			}
			
			for (int level = fullDepth; level>=0; level--){
				if (currentDepth == level){
					childNumber++;
					break;
				} else {
					if (fullDepth % 2 == 0) {
						if (currentDepth % 2 == 0) { // if depth is even
							validMovesList.add(childNumber+100);
							childNumber = 0;
						}
					} else {
						if (currentDepth % 2 != 0) { // if depth is odd
							validMovesList.add(childNumber+100);
							childNumber = 0;
						}
					}
					setFinish = true;
					break;
				}
			}
			currentDepth--;
			//*********** depth 6
			if (currentDepth == 2){
				parentNumber2++;
				parentNumber--;
			}
			//*********** depth 6
			numberValidTree.remove(numberValidTree.size()-1);
			if (setFinish == true){
				parentNumber++;
				validMovesList.add("depth = "+currentDepth);
				validMovesList.add("end of child");
			} else {
				validMovesList.add(numberValidMoves);
				if (numberValidMoves < min){
					min = numberValidMoves;
				}
			}
			setFinish = false;
			numberValidMoves = 0;
			//*************************
		}
	}
	
	// returns number of pieces flipped
	public static int numPiecesFlipped(int col, int row) {
		int numFlipped = 0;
		
		// make a move		
		playerUndo = false;
		furthestRedo = currentMove;
		copyState();
		move(col, row, player2);
		
		// get number flipped		
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++){
				if (board[i][j][currentMove] !=  board[i][j][currentMove-1]) {
					numFlipped ++; // compare this move with original state
				}
			}
		}
		//clean up
		for(int i=0; i<8; i++) {
			for(int j=0; j<8; j++){
				board[i][j][currentMove] = Piece.EMPTY;
			}
		}
		currentMove--;
		// actually returns the number of pieces flipped + 1
		// ...whatever
		return numFlipped;
	}
	
	// begins specified game
	public static void playGame(int i) {
		switch (i) {
			case 1:
				localGame = true;
				gameLoop1();
				break;
			case 2:
				gameLoop2();
				break;
			case 3:
				gameLoop3();
				break;
			case 4:
				gameLoop4();
				break;
			case 5:
				gameLoop5();
				break;
			default:
				// Should never be in here...
				break;
		}
	}
	
	public static void playAgain() {
		// ask to play  new game if game is over or quit
		System.out.print("\nWould you like to play again?\n(y\\N): ");
		boolean goodAnswer = false;
		while (!goodAnswer) {
			String input = scanner.next();
			if (input.equalsIgnoreCase("y")) {
				goodAnswer = true;
				playerQuit = false;
			} else if (input.equalsIgnoreCase("n")) {
				playAgain = false;
				goodAnswer = true;
			} else {
				System.out.println("What was that?");
			}
		}
	}

	// main	
	public static void main(String args[]) {
		while (playAgain) {
			gameStart();
			playAgain();
		}
		System.out.println("Goodbye!");
	}
}
