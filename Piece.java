/** Name: Sachihiko Kanda
 *  email: sakanda@ucsd.edu
 *  Contains the constructors, instance variables, and methods for a Piece Object in the game Tetris.
 *
 *
 * 
 * */

import java.util.*;
//import java.lang.*;

/** The Piece class models the seven available pieces in Tetris and
 *  their functions. The instance variables rowOffset and colOffset 
 *  define the position of the upper-left corner of the piece on the grid. 
 *  The instance variable tiles is a 2x2 array that models the shape 
 *  of the piece.
 * */
public class Piece {

  // all possible char representation of a piece
  public static final char[] possibleShapes = 
  {'O', 'I', 'S', 'Z', 'J', 'L', 'T'}; 

  // initial state of all possible shapes, notice that this array's 
  // content shares index with the possibleShapes array 
  public static final int[][][] initialTiles = {
    {{1,1},
      {1,1}}, // O

    {{0,0,0,0},
      {1,1,1,1},
      {0,0,0,0},
      {0,0,0,0}}, // I

    {{0,0,0},
      {0,1,1},
      {1,1,0}}, // S

    {{0,0,0},
      {1,1,0},
      {0,1,1}}, // Z

    {{0,0,0},
      {1,1,1},
      {0,0,1}}, // J

    {{0,0,0},
      {1,1,1},
      {1,0,0}}, // L

    {{0,0,0},
      {1,1,1},
      {0,1,0}} // T
  };  

  // random object used to generate a random shape
  public static Random random = new Random(); 

  // char representation of shape of the piece, I, J, L, O, S, Z, T
  public char shape;

  // the position of the upper-left corner of the tiles array 
  // relative to the Tetris grid
  public int rowOffset;
  public int colOffset;

  // used to determine 2-state-rotations for shapes S, Z, I
  // set to true to indicate the next call to rotate() should
  // rotate clockwise
  public boolean rotateClockwiseNext = false;

  // an array marking where the visible tiles are
  // a 1 indicates there is a visible tile in that position
  // a 0 indicates there is no visible tile in that position
  public int[][] tiles;


  //Default no-argument constructor
  public Piece(){
    int shape_number = random.nextInt(7);

    shape = possibleShapes[shape_number];

    if (shape == 'O'){
      rowOffset = 0;
      colOffset = 4;
    }

    else {
      rowOffset = -1;
      colOffset = 3;
    }

    //Gets length of tile side
    int tile_length = initialTiles[shape_number].length; 

    //Creates empty 2x2 array onto which the piece is copied
    tiles = new int[tile_length][tile_length];

    for (int i = 0 ; i < tile_length ; i++ ) {
      for (int j = 0 ; j < tile_length ; j++ ){
        tiles[i][j] = initialTiles[shape_number][i][j];
      }
    }
  }

  //Single-argument constructor which takes in a char as the argument 
  public Piece(char shape) {

    int shape_number = 0;

    this.shape = shape;

    //Gets index of shape in possibleShapes
    switch(shape){ // O I S Z J L T
      case 'O': break;
      case 'I': shape_number = 1;
                break;
      case 'S': shape_number = 2;
                break;
      case 'Z': shape_number = 3;
                break;
      case 'J': shape_number = 4;
                break;
      case 'L': shape_number = 5;
                break;
      case 'T': shape_number = 6;
    }

    //Gets length of tile side
    int tile_length = initialTiles[shape_number].length;

    if ( shape == 'O' ) {
      rowOffset = 0;
      colOffset = 4;
    }
    else {
      rowOffset = -1;
      colOffset = 3;
    }

    //Creates empty 2x2 array onto which piece is copied
    tiles = new int[tile_length][tile_length];

    for (int i = 0 ; i < tile_length ; i++ ) {
      for (int j = 0 ; j < tile_length ; j++ ){
        tiles[i][j] = initialTiles[shape_number][i][j];
      }
    }
  }

  //Copy constructor which takes in another Piece Object as the argument
  public Piece ( Piece other ) {
    shape     = other.shape;
    rowOffset = other.rowOffset;
    colOffset = other.colOffset;

    //Gets length of argument Piece object tile side
    int tile_length = other.tiles.length;
    //Creates empty 2x2 array onto which piece is copied
    tiles = new int[tile_length][tile_length];

    for (int i = 0 ; i < tile_length ; i++) {
      for (int j = 0 ; j < tile_length ; j++) {
        tiles[i][j] = other.tiles[i][j];
      }
    }
  }


  /**
   * This method rotates the instance variable tiles of the
   * Piece Object according to Tetris rules. O, T, L, and J
   * pieces always rotate clockwise, while other pieces 
   * alternate between counterclockwise and clockwise rotations
   * @param none
   * @return none
   ***********************************************************/

  public void rotate(){

    //Rotates all 'O', 'T', 'L', and 'J' piece clockwise
    if (shape == 'O' || shape == 'T' || shape == 'L' || shape == 'J')
      rotateClockwise();
    //Alternates between counterclockwise and clockwise rotations
    //for all other pieces
    else {
      if (rotateClockwiseNext) {
        rotateClockwise();
        rotateClockwiseNext = false; 
      }
      else{
        rotateCounterClockwise();
        rotateClockwiseNext = true;
      }
    }
  }

  /** Rotates piece 90 degrees clockwise
   * @param none
   * @return none
   ************************************************************/
  public void rotateClockwise() {

    //Creates 2x2 arrays to store pieces in intermediate phases
    int[][] transposed_tiles = new int[tiles.length][tiles.length];
    int[][] rotated_tiles    = new int[tiles.length][tiles.length];

    //Transpose tiles, reflect about vertical axis
    for (int i = 0 ; i < tiles.length ; i++) {
      for (int j = 0 ; j < tiles.length ; j++) {
        transposed_tiles[i][j] = tiles[j][i]; 
        rotated_tiles[i][tiles.length - 1 - j] = transposed_tiles[i][j];
      }
    }
    //copies rotated_tiles to tiles
    for (int i = 0 ; i < tiles.length ; i++ )
      for (int j = 0 ; j < tiles.length ; j++)
        tiles[i][j] = rotated_tiles[i][j];
  }

  /** Rotates piece 90 degrees counterclockwise
   * @param none
   * @return none
   ************************************************************/
  public void rotateCounterClockwise() {

    //Creates 2x2 arrays to store pieces in intermediate phases
    int[][] transposed_tiles = new int[tiles.length][tiles.length];
    int[][] rotated_tiles    = new int[tiles.length][tiles.length];

    for (int i = 0 ; i < tiles.length ; i++) {
      for (int j = 0 ; j < tiles.length ; j++) {
        transposed_tiles[i][j] = tiles[j][i]; 
        rotated_tiles[tiles.length - 1 - i][j]= transposed_tiles[i][j];
      }
    }

    for (int i = 0 ; i < tiles.length ; i++ )
      for (int j = 0 ; j < tiles.length ; j++)
        tiles[i][j] = rotated_tiles[i][j];
  }

  /** This method takes in the Direction enumerator and changes
   * the position of the Piece Object on the board accordingly
   * @param direction
   * @return none
   ***********************************************************/
  public void move(Direction direction) {

    switch (direction) {
      case DOWN : rowOffset++; //Moves down one row if DOWN
                  break;
      case LEFT : colOffset--; //Moves left one col if LEFT
                  break;
      case RIGHT: colOffset++; //Moves right one col if RIGHT
                  break;
    }
  }
}
