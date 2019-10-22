package monitors;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import main.Grid;

public class PilotMonitor extends Thread {
	private volatile boolean running = true;
	private GraphicsLCD lcd;
	private Grid grid;

	public PilotMonitor(Grid grid) {
		this.setDaemon(true);
		lcd = LocalEV3.get().getGraphicsLCD();
		this.grid = grid;
//		initialiseGridStates();
	}

	public void run() {
        while (running) {
        	lcd.clear();
            lcd.setFont(Font.getSmallFont());
            cellCounter = 0;
            updateMap();

    		try {
				sleep(500);
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
	  int rowCounter = 1;
	  lcd.drawString("+---+---+---+---+---+---+---+", 0, 0, 0);

	  int columnCount = 0;
	  //For every row in the occupancy grid fill in the string for row i.
	  for (int i = 0; i < this.gridHeight; i++) {
	     lcd.drawString(getRowString(), 0, (rowCounter)*10, 0);
	     lcd.drawString("+---+---+---+---+---+---+---+", 0, (rowCounter + 1)*10, 0);
	     rowCounter += 2;
	  }

	}

	/**
	 * Constructs the String that contains the occupancy probabilities .
	 * @param row the row index value of the occupancy grid.
	 * @return A string containing the current occupancy probabilities for this row on the grid.
	 */
	public final String getRowString() {
	  String rowString = "|";

	  /*For the width of the occupancy grid construct the string that goes in each column of a row.
	    This column will contain the probability of an obstacle occupying that position on the
	      grid.*/
	  for (int i = 0; i < 7; i++) {
	    double probability = grid.getGrid().get(cellCounter).getOccupancyProbability();
	    cellCounter++;

	    //check if probaility is unknown
	    if (probability == -1) {
	      //Add empty characters before and after the ? symbol so it centers in lcd grid cell.
	      rowString += " ? ";
	    } else {
	      /*only displays 1 decimal place due to limitation of lcd screen size
	      /    and since probability does not go greater than 1 we can assume this will be 3 characters.*/
//	      BigDecimal bd = new BigDecimal(probability);
//	      bd = bd.round(new MathContext(3));
	        //rounds to 1 decimal place
	        rowString += Math.round(probability*10)/10.0;
	    }

	    rowString += "|";
	  }
	  return rowString;
	}

	/**
	 * Constructs the string to be placed inside this cell of the occupancy grid.
	 * @param row the row this string is to be placed in.
	 * @param column the column this string is to be place in.
	 * @return the constructed string to be placed in the disired cell of the occupancy grid.
	 */
//	public final String getColumnString(int row, int column) {
//	  return this.gridStates[row][column] + "|";
//	}

	/**
	 * Set the starting state values of the occupancy grid.
	 */
//	public final void initialiseGridStates() {
//	  for (int y = 0; y < this.gridHeight; y++) {
//	    for (int x = 0; x < this.gridWidth; x++) {
//	      this.gridStates[y][x] = " ? ";
//	    }
//	  }
//	  this.gridStates[0][0] = String.format("%.1f", 0.00);
//	}


	public final void terminate() {
		running = false;
	}
}
