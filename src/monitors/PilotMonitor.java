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

		for (int y = grid.getGridHeight() - 1; y >= 0; y--) {
			String rowString = "|";
			
			for (int x = 0; x < grid.getGridWidth(); x++) {
					double probability = grid.findUsingCoordinate(x, y).getOccupancyProbability();
			
					// check if probability is unknown
					if (probability == -1) {
						// add empty characters before and after the ? symbol so it centers in lcd grid cell.
						rowString += " ? ";
					} else {
						// only display 1 significant digit and since probaility does not go greater than 1 we can assume this will be 3 characters.
						BigDecimal bd = new BigDecimal(probability);
						bd = bd.round(new MathContext(3));
						rowString += bd.doubleValue();
					}
					rowString += "|";
			}
			
			lcd.drawString(rowString, 0, (rowCounter) * 10, 0);
			lcd.drawString("+---+---+---+---+---+---+---+", 0, (rowCounter + 1) *10, 0);
			rowCounter += 2;
		}
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
