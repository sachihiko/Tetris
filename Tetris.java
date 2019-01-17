/** Name: Sachihiko Kanda
 *  email: sakanda@ucsd.edu*
 *  Contains constructors, instance variables, and methods which code for 
 *  a game of Tetris.
 * 
 * */


import java.util.*;
import java.io.*;

/** The Tetris class models a game of Tetris, taking in user input to move
 *  and rotate pieces as well as save the current game onto a .txt file.
 *  */
public class Tetris {

  public int linesCleared; // how many lines cleared so far

  public boolean isGameover;  // true if the game is over
  // false if the game is not over

  public Piece activePiece;   // holds a Piece object that can be moved
  // or rotated by the player

  public Piece nextPiece; // holds a Piece object that will become the new 
  // activePiece when the activePiece consolidates

  // The following 2 variables are used for the extra credit 
  public boolean usedHold = false;    // set to true if the player already 
  // used hold once since last piece 
  // consolidated

  public Piece storedPiece = null;    // holds the stored piece 

  public char[][] grid;   // contains all consolidated pieces, each tile  
  // represented by a char of the piece's shape
  // a position stores a space char if it is empty


  //Default no-argument constructor
  public Tetris(){
    // creates new grid, initializes to all space characters
    grid = new char[20][10];
    for ( int i = 0 ; i < 20 ; i++ ) {
      for ( int j = 0 ; j < 10 ; j++ ) {
        grid[i][j] = ' ';
      }
    }
    //initializes instance variables
    linesCleared = 0;
    isGameover   = false;  
    activePiece  = new Piece();
    nextPiece    = new Piece();
  }

  //Single argument constructor
  public Tetris (String filename) throws IOException {

    Scanner input   = new Scanner(new File(filename));
    String next_row = new String();
    
    //creates grid, reads in lines from input file
    grid            = new char[20][10];
    linesCleared    = input.nextInt();
    input.nextLine();
    activePiece     = new Piece(input.nextLine().charAt(0));
    nextPiece       = new Piece(input.nextLine().charAt(0));
    isGameover      = false;

   //copies characters from file 
    for ( int i = 0 ; i < 20 ; i++ ) { 
     next_row = input.nextLine();
     for ( int j = 0 ; j < 10 ; j++ ){
        grid[i][j] = next_row.charAt(j);
     }
    }
  }

/**
 * checks if piece has conflict
 * @param Piece object 
 * @return true if there is conflict, false otherwise
 ***********************************************************/
  public boolean hasConflict(Piece piece) {

    for ( int i = 0 ; i < piece.tiles.length ; i++ ) {
      for ( int j = 0 ; j < piece.tiles.length ; j++ ) {
        // checks if tile is visible
        if ( piece.tiles[i][j] == 1 ) {
          //returns true if tile is outside the grid
          if (  piece.rowOffset + i < 0 
              || piece.rowOffset + i > 19 
              || piece.colOffset + j < 0 
              || piece.colOffset + j > 9 )
            return true;
          //returns true if position on grid is already occupied
          else if ( grid[piece.rowOffset + i][piece.colOffset + j] != ' ' )
            return true;
          else 
            continue;
        }
      }
    } 
    return false;
  }

/**
 * 
 * @param - none
 * @return - none
 ***********************************************************/
  public void consolidate() {
    for ( int i = 0 ; i < activePiece.tiles.length ; i++ ) {
      for ( int j = 0 ; j < activePiece.tiles.length ; j++ ) {
        if (activePiece.tiles[i][j] == 1) {
          grid[activePiece.rowOffset + i][activePiece.colOffset + j]
            = activePiece.shape;
          usedHold = false;
        }
        else
          continue;
      }
    }
  }

/**
 * Clears rows on the grid that have been 
 * completely filled with characters
 * @param none
 * @return none
 ***********************************************************/
  public void clearLines() {
    int full_row;
    boolean row_full = true;

    for ( int i = 0 ; i < 20 ; i++ ) {
      for ( int j = 0 ; j < 10 ; j++ ) {
        if ( grid[i][j] == ' ')
          row_full = false;
      }
      if ( row_full ) {
        full_row = i;
        for ( int k = full_row ; k > 0 ; k-- )
          grid[k] = grid[k - 1];
        Arrays.fill(grid[0], ' ');
        linesCleared++;
      }
      row_full = true;
    }
  }

 /** moves active piece according to user input
 * @param Direction 
 * @return true if piece was moved, false otherwise
 ***********************************************************/

  public boolean move( Direction direction ) {
    Piece test_piece = new Piece( activePiece );
    switch ( direction ) {
      case DOWN : test_piece.rowOffset++;
                  if ( !hasConflict( test_piece ) ){
                    activePiece.rowOffset++;
                    return true;
                  }
                  else { 
                    consolidate();
                    clearLines();
                    activePiece = nextPiece;
                    nextPiece   = new Piece();
                    if ( hasConflict( nextPiece ) )
                      isGameover = true;
                  }
                  break;
      case LEFT : test_piece.colOffset--;
                  if ( !hasConflict( test_piece ) ) {
                    activePiece.colOffset--;
                    return true;
                  }
                  break;
      case RIGHT: test_piece.colOffset++;
                  if ( !hasConflict( test_piece ) ) {
                    activePiece.colOffset++;
                    return true;
                  }
                  break;
    }
    return false;
  }

 /** Drops the active piece to the lowest possible
 * position on the grid without causing conflict
 * @param none
 * @return none
 ***********************************************************/
  public void drop() {
    Piece test_piece = new Piece(activePiece);
    test_piece.rowOffset++;

    while(!hasConflict(test_piece)){
      move(Direction.DOWN);
      test_piece.rowOffset++;
    }
  }

 /**
 * Rotates piece
 * @param - none
 * @return - none
 ***********************************************************/
  public void rotate() {
    Piece test_piece = new Piece(activePiece);
    test_piece.rotate();
    if (!hasConflict(test_piece))
      activePiece.rotate();
  }

/**
 * Outputs current game to file
 * @param - none
 * @return - none
 ***********************************************************/
  public void outputToFile() throws IOException {

    File file = new File("output.txt");
    PrintWriter output = new PrintWriter(file);

    output.println(linesCleared);
    output.println(activePiece.shape);
    output.println(nextPiece.shape);

    for ( int i = 0 ; i < 20 ; i++ ) {
      for (int j = 0 ; j < 10 ; j++ ) {
        output.print(grid[i][j]);
      }
      output.println();
    }
    output.close();
  }

/**
 * Plays Tetris game and calls various methods according to
 * user input.
 * @param - none
 * @return - none
 ***********************************************************/
  public void play () {

    Scanner input   = new Scanner(System.in);
    String user_input = new String();

    while (!isGameover){
      System.out.print(toString());
      System.out.print("> ");
      user_input = input.nextLine();

      switch (user_input) {
        case "a": move(Direction.LEFT);
                  break;
        case "d": move(Direction.RIGHT);
                  break;
        case "s": move(Direction.DOWN);
                  break;
        case "w": rotate();
                  break;
        case "z": hold();
                  break;
        case " ": drop();
                  break;
        case "o":try {
                   outputToFile();
        } 
        catch(IOException e){}
        break;
        case "q": return;
        default : break;
      }
    }
  }

  /**
   * returns the string representation of the Tetris game state in the 
   * following format:
   *  Lines cleared: [number]
   *  Next piece: [char]  (Stored piece: [char])
   *  char[20][10]
   * @return string representation of the Tetris game
   */
  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();

    str.append("\nLines cleared: " + this.linesCleared + '\n');

    str.append("Next piece: " + this.nextPiece.shape);
    if (this.storedPiece == null) str.append("\n");
    else str.append("  Stored piece: " + this.storedPiece.shape + '\n');

    str.append("| - - - - - - - - - - |\n");

    /*deep copy the grid*/
    char[][] temp_grid = new char[this.grid.length][this.grid[0].length];
    for (int row=0; row<this.grid.length; row++)
      for (int col=0; col<this.grid[0].length; col++)
        temp_grid[row][col] = this.grid[row][col];

    /*put the active piece in the temp grid*/
    for (int row=0; row<this.activePiece.tiles.length; row++)
      for (int col=0; col<this.activePiece.tiles[0].length; col++)
        if (activePiece.tiles[row][col] == 1)
          temp_grid[row+activePiece.rowOffset]
            [col+activePiece.colOffset] = 
            activePiece.shape;

    /*print the temp grid*/
    for (int row=0; row<temp_grid.length; row++){
      str.append('|');
      for (int col=0; col<temp_grid[0].length; col++){
        str.append(' ');
        str.append(temp_grid[row][col]);
      }
      str.append(" |\n");
    }

    str.append("| - - - - - - - - - - |\n");
    return str.toString();        
  }


  public void hold() {

    if (!usedHold) {
      activePiece = new Piece(activePiece.shape);
      if (storedPiece == null) {
        storedPiece = activePiece;
        activePiece = nextPiece;
        nextPiece   = new Piece();
      }
      else {
        Piece temp  = new Piece(storedPiece);
        storedPiece = activePiece;
        activePiece = temp;
      }
      usedHold = true;
    }


    // TODO extra credit
  }

  /**
   * first method called during program execution
   * @param args: an array of String when running the program from the 
   * command line, either empty, or contains a valid filename to load
   * the Tetris game from
   */
  public static void main(String[] args) {

    if (args.length != 0 && args.length != 1) {
      System.err.println("Usage: java Tetris / java Tetris <filename>");
      return;
    }
    try {
      Tetris tetris;
      if (args.length == 0) tetris = new Tetris();
      else tetris = new Tetris(args[0]);
      tetris.play();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
