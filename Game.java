/**
 * This program implements a GUI application for the game
 * Tetris. A Grid of squares is displayed in a window, changing colors along
 * with the current status of the game.
 */

import javafx.application.*;
import javafx.scene.control.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import java.util.*;
import java.io.*;

/**
 * class GuiTetris
 * This class extends Application and implements a GUI which displays the 
 * elements of a game of Tetris.
 * 
 * This is done by the private handler class, 
 * myKeyHandler, which registers user-inputted key commands. The various 
 * methods defined in this class cause certain squares in the interface to
 * change colors and are called by the handler. The private class 
 * MoveDownWorker simulates a downwards keypress every half second and
 * completes the Tetris game mechanics.
 *
 * @author Sachihiko Kanda
 */

public class Game extends Application {

  private static final int PADDING = 10;
  private static final int TILE_GAP = 2;
  private static final int FONT_SIZE = 30;
  private static final int TILE_SIZE = 25;

  private Tetris tetris;
  private GridPane pane;
  private MyKeyHandler myKeyHandler;

  //references to access and update values and colors
  private Text title;
  private Text linesCleared;
  private Color color;	
  private Rectangle[][] topGrid = new Rectangle[10][4];  //next and stored
  private Rectangle[][] botGrid = new Rectangle[10][20]; 	//play area
  private Piece shadowPiece; 

  /**
   * Assigns color to color instance variable 
   * 
   * @param shape
   * @return color 
   */
  public Color getColor(char shape){
    Color color;
    switch(shape){
      case 'O': color = Color.PALETURQUOISE;
                break;
      case 'I': color = Color.DARKCYAN;
                break;      	
      case 'S': color = Color.TURQUOISE;
                break;      	
      case 'Z': color = Color.DARKTURQUOISE;
                break;      	
      case 'T': color = Color.LIGHTBLUE;
                break;      	
      case 'J': color = Color.LIGHTCYAN;
                break;
      case 'L': color = Color.CYAN;
                break;
      default : color = Color.WHITE;
                break;
    }
    return color;
  }
  /**
   * Updates tiles to display next and stored pieces. The stored piece is
   * displayed in the top left area of the scene and the next piece is
   * displayed in the top right area. 
   */

  public void displayNextAndStored(){

    //Updates grid to display color stored piece
    color = getColor(tetris.nextPiece.shape); 

    for (int i = 0; i < tetris.nextPiece.tiles.length; i++){
      for (int j = 0; j < tetris.nextPiece.tiles.length; j++){
        if (tetris.nextPiece.tiles[j][i] == 1) {
          if (tetris.nextPiece.shape == 'O')
            topGrid[i + 7][j + 1].setFill(color);
          else
            topGrid[i + 6][j].setFill(color);
          //pane.add(rectangle, i + 6, j + 1);
        }
      }
    }

    //Updates grid to display stored piece

    if (tetris.storedPiece != null) {
      color = getColor(tetris.storedPiece.shape); 

      for (int i = 0; i < tetris.storedPiece.tiles.length; i++){
        for (int j = 0; j < tetris.storedPiece.tiles.length; j++){
          if (tetris.storedPiece.tiles[j][i] == 1) {
            if (tetris.storedPiece.shape == 'O')
              topGrid[i + 1][j + 1].setFill(color);
            else
              topGrid[i][j].setFill(color);
            //pane.add(rectangle, i + 6, j + 1);
          }
        }
      }
    }

  }

  /**
   * Updates the tiles to display the active piece in its location on the board
   */

  public void displayActive(){
    color = getColor(tetris.activePiece.shape);

    /*put the active piece in the grid*/
    for (int i = 0; i < tetris.activePiece.tiles.length; i++)
      for (int j = 0; j < tetris.activePiece.tiles.length; j++)
        if (tetris.activePiece.tiles[i][j] == 1) {
          this.botGrid[j + tetris.activePiece.colOffset]
            [i + tetris.activePiece.rowOffset].setFill(color);
        }
  }
  /**
   * Resets colors of tiles displaying active and shadow piece to default color
   *
   * Called before active piece location is changed and displayActive is called
   */

  public void erase(){
    for (int i = 0; i < tetris.activePiece.tiles.length; i++)
      for (int j = 0; j < tetris.activePiece.tiles.length; j++)
        if (tetris.activePiece.tiles[i][j] == 1) {
          this.botGrid[j + tetris.activePiece.colOffset]
            [i + tetris.activePiece.rowOffset].setFill(Color.SILVER);
          this.botGrid[j + shadowPiece.colOffset]
            [i + shadowPiece.rowOffset].setFill(Color.SILVER);
        }

  }
  /**
   * Resets the colors of the entire board to default color. 
   *
   * Called before lines are cleared and consolidated pieces are updated.
   */
  public void clearGrid(){
    for (int i = 0; i < 10; i++){
      for (int j = 0; j < 24; j++){
        if (j < 4) //top grid occupies first four rows
          this.topGrid[i][j].setFill(Color.BLACK);
        else
          this.botGrid[i][j - 4].setFill(Color.SILVER);
      }
    }

  }
  /**
   * Resets the color of the entire board to default color. 
   *
   * Called before lines are cleared and consolidated pieces are updated.
   */

  public void displayConsolidated(){
    clearGrid();
    for (int i = 0 ; i < 20 ; i++){
      for (int j = 0 ; j < 10 ; j++) {
        if (tetris.grid[i][j] != ' '){
          color = getColor(tetris.grid[i][j]);
          this.botGrid[j][i].setFill(color);
        }
      }
    }
  }
  /**
   * Updates tiles to display shadow piece. 
   */
  public void displayShadow(){
    shadowPiece = new Piece(tetris.activePiece);
    Piece testShadowPiece = new Piece(tetris.activePiece);

    //moves test piece to see if there is conflict, moves shadow 
    //down until it has reached the lowest possible position
    testShadowPiece.rowOffset++;

    while(!tetris.hasConflict(testShadowPiece)){
      shadowPiece.rowOffset++;
      testShadowPiece.rowOffset++;
    }

    //updates color
    for (int col = 0; col < shadowPiece.tiles.length ; col++)
      for (int row = 0; row < shadowPiece.tiles.length; row++)
        if (shadowPiece.tiles[row][col] == 1)
          this.botGrid[col + shadowPiece.colOffset]
            [row + shadowPiece.rowOffset]
            .setFill(Color.BLACK);

  }

  /**
   * Starts the application
   *
   * @param primaryStage the primary stage onto which application scene is set
   * @throws Exception
   */

  @Override
  public void start(Stage primaryStage) {
    this.tetris = new Tetris();

    pane = new GridPane();
    pane.setAlignment(Pos.CENTER);
    pane.setPadding(new Insets(PADDING,PADDING,PADDING,PADDING));
    pane.setStyle("-fx-background-color: rgb(255,255,255)");
    pane.setHgap(TILE_GAP); 
    pane.setVgap(TILE_GAP);


    //initilize GUI elements
    title = new Text("Tetris");
    title.setFont(Font.font("Consolas", FontWeight.BOLD, FONT_SIZE));
    pane.add(title, 0, 0, 8, 1);

    linesCleared = new Text(Integer.toString(tetris.linesCleared));
    linesCleared.setFont(Font.font("Consolas", FontWeight.BOLD, FONT_SIZE));

    pane.add(linesCleared, 8, 0, 2, 1);

    //initialize pane
    for (int i = 0 ; i < 10  ; i++)
      for (int j = 0 ; j < 4 ; j++){
        topGrid[i][j] = new Rectangle(TILE_SIZE, TILE_SIZE, Color.BLACK);
      }
    for (int i = 0 ; i < 10  ; i++)
      for (int j = 0 ; j < 20 ; j++){
        botGrid[i][j] = new Rectangle(TILE_SIZE, TILE_SIZE, Color.SILVER);
      }
    for (int i = 0 ; i < 10 ; i++)
      for (int j = 0 ; j < 24 ; j++){
        if (j < 4)
          pane.add(topGrid[i][j], i , j + 1);
        else
          pane.add(botGrid[i][j - 4], i, j + 1);
      } 

    //displays pieces 
    displayNextAndStored();
    displayActive();
    displayShadow();

    //creates Scene object to add to primary stage
    Scene scene = new Scene(pane);
    primaryStage.setTitle("Tetris");
    primaryStage.setScene(scene);
    primaryStage.show();

    //creates and registers handler
    myKeyHandler = new MyKeyHandler();
    scene.setOnKeyPressed(myKeyHandler);
    MoveDownWorker worker = new MoveDownWorker();
    worker.start();
  }



  /**
   * class MyKeyHandler
   *
   * This class implements the EventHandler interface and defines a handler
   * object that handles key events
   * 
   * @author Sachihiko Kanda
   */

  private class MyKeyHandler implements EventHandler<KeyEvent> {

    /**
     * Overrides handle method defined in EventHandler interface
     * 
     * @param KeyEvent
     */

    @Override
    public void handle(KeyEvent e){
      // TODO handle key events here
      if(!tetris.isGameover){

        //resets color of active piece and shadow
        erase();	

        //gets key code from key event and updates GUI
        switch(e.getCode()){
          //rotates piece 
          case UP   : tetris.rotate();
                      break;
          case DOWN : tetris.move(Direction.DOWN);
                      displayConsolidated();
                      displayNextAndStored();
                      break;
          case LEFT : tetris.move(Direction.LEFT);
                      break;
          case RIGHT: tetris.move(Direction.RIGHT);
                      break;
          case SPACE: tetris.drop();
                      tetris.move(Direction.DOWN);
                      displayConsolidated();
                      displayNextAndStored();
                      break;
          case Z    : tetris.hold();
                      displayConsolidated();
                      displayNextAndStored();
                      break;	     
          case O    : try { tetris.outputToFile(); }
                      catch(IOException x){}
                      break;
        }

        //updates colors to display new location of
        //active piece and shadow
        displayShadow();
        displayActive();

        //updates lines cleared
        linesCleared.setText(Integer.toString(tetris.linesCleared));
      }
      else {
        //updates title when game over
        title.setText("Game Over!");
      }
    }
  }

  /**
   * private class GuiTetris.MoveDownWorker
   * Thread that simulates a downwards keypress every interval
   */
  private class MoveDownWorker extends Thread{

    private static final int DROP_INTERVAL = 500; // millisecond
    private int move_down_timer; 

    /**
     * method run
     * Called when the thread begins, decrements the timer every iteration
     * of a loop, reset the timer and sends a keydown when timer hits 0
     */
    @Override
    public void run(){

      // loop continues until returned
      while (true) {
        // stops thread if the game is over
        if (tetris.isGameover) return; 

        // wait 1ms per iteration
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          break;
        }

        move_down_timer -= 1;
        if (move_down_timer <= 0 ) {

          // simulate one keydown by calling the 
          // handler.handle()
          myKeyHandler.handle(
              new KeyEvent(null, "", "", KeyCode.DOWN, 
                false, false, false, false)
              );

          move_down_timer = DROP_INTERVAL;
        }
      }
    }
  } 

}

