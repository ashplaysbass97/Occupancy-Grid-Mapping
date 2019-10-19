import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;

public class PilotMonitor extends Thread {
	private volatile boolean running = true;
	private GraphicsLCD lcd;
	private String[][] gridStates;
	private final int gridWidth = 7;
	private final int gridHeight= 6;
	
	public PilotMonitor() {
		this.setDaemon(true);
		lcd = LocalEV3.get().getGraphicsLCD();
		this.gridStates = new String[6][7];
		initialiseGridStates();
	}
	
	public void run() {
        while (running) {
        	lcd.clear();
        	// display whether the bumpers have been pressed and the distance from the ultrasonic sensor
            lcd.setFont(Font.getSmallFont());
            updateMap();
//        	lcd.drawString("Left Bumper: " + myRobot.isLeftBumperPressed(), 0, 20, 0);
//        	lcd.drawString("Right Bumper: " + myRobot.isRightBumperPressed(), 0, 30, 0);
//        	lcd.drawString("Distance: " + myRobot.getDistance(), 0, 40, 0); 
//        	lcd.drawString("Angle: "+myRobot.getAngle(), 0, 50, 0);   
//        	lcd.drawString("Motion: "+myRobot.getPilot().isMoving(), 0, 60, 0);
//            lcd.drawString("  type: "+myRobot.getPilot().getMovement().getMoveType(), 0, 70, 0);
            
    		try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				running = false;
			}
        }
	}
	
	/**
	 * WIP
	 * Draws the 6 by 7 Occupancy grid used to map the environment.
	 */
	public final void updateMap() {
	  int rowCounter = 0;
	  lcd.clear();
	  lcd.drawString("+---+---+---+---+---+---+---+", 0, 0, 0);
	  
	  //used to keep track of which row on the LCD screen is being drawn
	  rowCounter++;
	  
	  //For every row in the occupancy grid fill in the string for row i.
	  for (int i = 0; i < this.gridHeight; i++) {
	     lcd.drawString(getRowString(i), 0, (rowCounter)*10, 0);
	     lcd.drawString("+---+---+---+---+---+---+---+", 0, (rowCounter + 1)*10, 0);
	     rowCounter += 2;
	  }

	}
	
	/**
	 * Constructs the String that contains the occupancy probabilities . 
	 * @param row the row index value of the occupancy grid.
	 * @return A string containing the current occupancy probabilities for this row on the grid.
	 */
	public final String getRowString(int row) {
	  String rowString = "|";
	  
	  /*For the width of the occupancy grid construct the string that goes in each column of a row.
	    This column will contain the probability of an obstacle occupying that position on the 
	      grid.*/
	  for (int i = 0; i < this.gridWidth; i++) {
	    rowString += getColumnString(row, i);
	  }
	  return rowString;
	}
	
	/**
	 * Constructs the string to be placed inside this cell of the occupancy grid.
	 * @param row the row this string is to be placed in.
	 * @param column the column this string is to be place in.
	 * @return the constructed string to be placed in the disired cell of the occupancy grid.
	 */
	public final String getColumnString(int row, int column) {
	  return this.gridStates[row][column] + "|";
	}
	
	/**
	 * Set the starting state values of the occupancy grid.
	 */
	public final void initialiseGridStates() {
	  for (int y = 0; y < this.gridHeight; y++) { 
	    for (int x = 0; x < this.gridWidth; x++) {
	      this.gridStates[y][x] = " ? ";
	    }
	  }
	  this.gridStates[0][0] = String.format("%.1f", 0.00);
	}
	
	
	public final void terminate() {
		running = false;
	}
}
