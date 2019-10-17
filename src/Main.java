import java.util.ArrayList;

import lejos.hardware.Button;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Main {
	private static ArrayList<Cell> grid = new ArrayList<Cell>();
	private static Cell currentCell;
	
	private static final int GRID_WIDTH = 7;
	private static final int GRID_HEIGHT = 6;
	private static final int CELL_SIZE = 25;
	
    public static void main(String[] args) {
        Robot myRobot = new Robot();
        MovePilot myPilot = myRobot.getPilot();
        Monitor myMonitor = new Monitor();	
        
        // start the monitor
     	myMonitor.start();
        
        // test the pilot
     	myPilot.travel(5);
     	myPilot.rotate(45);
     	
     	// create grid and set neighbours
     	createGrid();
		setNeighbours();
		currentCell = findUsingCoordinate(0, 0);
        
     	// set up the behaviours for the arbitrator and construct it
        Behavior b1 = new SelectDestination(grid, currentCell);
        Behavior [] behaviorArray = {b1};
		Arbitrator arbitrator = new Arbitrator(behaviorArray);
		
		// start the arbitrator
		arbitrator.go();
        
        // stop the monitor thread and close the robot
        Button.ESCAPE.waitForPress();
        myMonitor.terminate();
        myRobot.closeRobot();
    }
    
    private static void createGrid() {
		for (int y = 0; y < GRID_HEIGHT; y++) {
			for (int x = 0; x < GRID_WIDTH; x++) {
				grid.add(new Cell(x, y));
			}
		}
	}
	
	private static void setNeighbours() {
		for (int y = 0; y < GRID_HEIGHT; y++) {
			for (int x = 0; x < GRID_WIDTH; x++) {
				ArrayList<Cell> neighbours = new ArrayList<Cell>();
				if (x != 0) neighbours.add(findUsingCoordinate(x - 1, y));
				if (x != 8) neighbours.add(findUsingCoordinate(x + 1, y));
				if (y != 8) neighbours.add(findUsingCoordinate(x, y + 1));
				if (y != 0) neighbours.add(findUsingCoordinate(x, y - 1));
				findUsingCoordinate(x, y).setNeighbours(neighbours);
				findUsingCoordinate(x, y).printNeighbours();
			}
		}
	}
	
	private static Cell findUsingCoordinate(int x, int y) {
		for (Cell cell : grid) {
			if (cell.getX() == x && cell.getY() == y) { 
				return cell;
			}
		}
		return null;
	}
}
