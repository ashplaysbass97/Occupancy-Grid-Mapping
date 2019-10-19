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
		
		Robot myRobot = new Robot();
		MovePilot myPilot = myRobot.getPilot();
		Monitor myMonitor = new Monitor();	
		
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
		Behavior b1 = new MoveBehaviour(grid, currentCell);
		Behavior b2 = new ExitBehavior(myRobot, myMonitor, server);
		Behavior [] behaviorArray = {b1, b2};
		Arbitrator arbitrator = new Arbitrator(behaviorArray);
		
		// start the arbitrator
		arbitrator.go();
	}
	
	private static void createGrid() {
		for (int y = 0; y < GRID_HEIGHT; y++) {
			for (int x = 0; x < GRID_WIDTH; x++) {
				grid.add(new Cell(x, y));
			}
		}
	}
	
	/**
	 * Jamie - fixed a bug where limits for setting neighbours when x = 8 or y = 8
	 * 	was producing a nullPointerException x limit is now 6 and y limit is 5.
	 */
	private static void setNeighbours() {
		for (int y = 0; y < GRID_HEIGHT; y++) {
			for (int x = 0; x < GRID_WIDTH; x++) {
				ArrayList<Cell> neighbours = new ArrayList<Cell>();
				if (x != 0) neighbours.add(findUsingCoordinate(x - 1, y));
				if (x != 6) neighbours.add(findUsingCoordinate(x + 1, y));
				if (y != 5) neighbours.add(findUsingCoordinate(x, y + 1));
				if (y != 0) neighbours.add(findUsingCoordinate(x, y - 1));
				findUsingCoordinate(x, y).setNeighbours(neighbours);
				// findUsingCoordinate(x, y).printNeighbours();
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
