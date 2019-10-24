package monitors;

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
	}

	public void run() {
		while (running) {
			lcd.clear();
			lcd.setFont(Font.getSmallFont());
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
	 * Draws the 6 by 7 Occupancy grid used to map the environment.
	 */
	private void updateMap() {
		//this is the only way i could stop the lcd screen overlaping text might revisit
		for (int i = 0; i < 9; i++) {
			System.out.println("");
			System.out.flush();
		}
		int rowCounter = 1;
		lcd.drawString("+---+---+---+---+---+---+---+", 0, 0, 0);

		for (int y = grid.getGridHeight() - 1; y >= 0; y--) {
			String rowString = "|";
			
			for (int x = 0; x < grid.getGridWidth(); x++) {
				double probability = grid.getCell(x, y).getOccupancyProbability();
			
				// display the robot's current position
				if (grid.getCurrentCell() == grid.getCell(x, y)) {
					rowString += " R ";
					
				// display if the cell is unknown
				} else if (probability == -1) {
					rowString += " ? ";
					
				// display if the cell is occupied
				} else if (probability > 0.99) {
					rowString += "|||";
					
				// display occupancy probability to 1 significant digit
				} else if (probability == -2) {
					rowString += " X ";
				} else {
					rowString += Math.round(probability * 10) / 10.0;
				}
				rowString += "|";
			}
			
			lcd.drawString(rowString, 0, (rowCounter) * 10, 0);
			lcd.drawString("+---+---+---+---+---+---+---+", 0, (rowCounter + 1) *10, 0);
			rowCounter += 2;
		}
	}

	public final void terminate() {
		running = false;
	}
}
