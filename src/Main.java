import java.util.ArrayList;
import java.io.*;
import java.net.*;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Main {
	//server port between pc client and robot
	public static final int port = 1234;
	
	//Grid storing weather certain cells are unexplored.
	private static ArrayList<Cell> grid = new ArrayList<Cell>();
	private static Cell currentCell;
	
	private static final int GRID_WIDTH = 7;
	private static final int GRID_HEIGHT = 6;
	
	//Server socket used between robot and pc client.
	private static ServerSocket server;
	
	public static void main(String[] args) {
		
		PilotRobot myRobot = new PilotRobot();
		MovePilot myPilot = myRobot.getPilot();
		PilotMonitor myMonitor = new PilotMonitor();	
		
		//Start server and create PCMonitor thread;
		PCMonitor pcMonitor = null;
		try {
			server = new ServerSocket(port);
			System.out.println("Awaiting client..");
			Socket client = server.accept();
			pcMonitor = new PCMonitor(client, myRobot);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//start pcMonitor
		pcMonitor.start();
		
		// start the robot monitor
		myMonitor.start();
		
		// test the pilot
		myPilot.travel(5);
		myPilot.rotate(45);
		
		// create grid and set neighbours
		createGrid();
		setNeighbours();
		currentCell = findUsingCoordinate(0, 0);
		findUsingCoordinate(0, 0).setStatus("clear");
		
		// set up the behaviours for the arbitrator and construct it
		Behavior b1 = new MoveBehavior(myRobot, grid, currentCell);
		// TODO behaviour for scanning the surroundings
		// TODO behaviour for obstacle avoidance
		// TODO behaviour for returning to the starting point once the map is complete
		Behavior b2 = new ExitBehavior(myRobot, myMonitor, server);
		Behavior [] behaviorArray = {b1, b2};
		Arbitrator arbitrator = new Arbitrator(behaviorArray);
		arbitrator.go();
	}
	
	/**
	 * Creates a grid of a specified height and width.
	 */
	private static void createGrid() {
		for (int y = 0; y < GRID_HEIGHT; y++) {
			for (int x = 0; x < GRID_WIDTH; x++) {
				grid.add(new Cell(x, y));
			}
		}
	}
	
	/**
	 * Populates the neighbours ArrayList for each cell in the grid.
	 */
	private static void setNeighbours() {
		for (int y = 0; y < GRID_HEIGHT; y++) {
			for (int x = 0; x < GRID_WIDTH; x++) {
				ArrayList<Cell> neighbours = new ArrayList<Cell>();
				if (x != 0) neighbours.add(findUsingCoordinate(x - 1, y));
				if (x != GRID_HEIGHT - 1) neighbours.add(findUsingCoordinate(x + 1, y));
				if (y != GRID_WIDTH - 1) neighbours.add(findUsingCoordinate(x, y + 1));
				if (y != 0) neighbours.add(findUsingCoordinate(x, y - 1));
				findUsingCoordinate(x, y).setNeighbours(neighbours);
			}
		}
	}
	
	/**
	 * Finds the corresponding cell for a set of provided coordinates.
	 * @param x the x-coordinate of the cell to find
	 * @param y the y-coordinate of the cell to find
	 * @return the corresponding cell for the coordinates provided
	 */
	private static Cell findUsingCoordinate(int x, int y) {
		for (Cell cell : grid) {
			if (cell.getX() == x && cell.getY() == y) { 
				return cell;
			}
		}
		return null;
	}
}
