package monitors;
import main.Cell;
import main.Grid;
import main.PilotRobot;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

// Used to send data about the robot to a PC client interface.
public class PCMonitor extends Thread {

	//Server socket between robot and client
	private Socket client;

	//checks if thread is running.
	private volatile boolean running = true;

	//Data output stream
	private PrintWriter out;

	//The actual robot.
	private PilotRobot robot;

	private Grid grid;
	
	private Cell destination = null;
	private ArrayList<Cell> path = new ArrayList<Cell>();

	public PCMonitor(Socket client, PilotRobot robot, Grid grid) {
		this.client = client;
		this.robot = robot;
		this.grid = grid;
		
		try {
			out = new PrintWriter(client.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
			running = false;
		}
	}

	//run the thread
	public void run() {
		while (running) {
			
			// output sensor information
			out.println(robot.getDistance());
			out.println(robot.getAngle());
			out.println(robot.getCorrectedGyro());
			out.println(robot.getLeftColor()[0]);
			out.println(robot.getRightColor()[0]);
			
			// ouptut movement information
			if (robot.getPilot().isMoving()) {
				out.println("Moving");
			} else {
				out.println("Stationary");
			}
			out.println(robot.getPilot().getMovement().getMoveType());
			out.println(robot.getOdometryPoseProvider().getPose().getHeading());
			// output the destination
			if (destination != null) {
				out.println("(" + destination.getX() + ", " + destination.getY() + ")");
			} else {
				out.println("null");
			}
			
			// output the path
			if (path != null && !path.isEmpty()) {
				String pathOutput = "";
				for (Cell cell : path) {
					pathOutput += "(" + cell.getX() + ", " + cell.getY() + "), ";
				}
				out.println(pathOutput.substring(0, pathOutput.length() - 2));
			} else {
				out.println("null");
			}
			
			// output probability data and current cell
			String probabilityData = "";
			for (int y = grid.getGridHeight() - 1; y >= 0; y--) {
				for (int x = 0; x < grid.getGridWidth(); x++) {
					probabilityData +=  ((double)Math.round(grid.getCell(x, y).getOccupancyProbability()*1000)/1000) + ",";
				}
			}
			out.println(probabilityData);
			out.println(grid.getCurrentCell().getX() + "," + grid.getCurrentCell().getY());
			out.flush();
			
			try {
				sleep(400);
			} catch (InterruptedException e) {
				e.printStackTrace();
				running = false;
			}
		}
	}
	
	public final void setDestination (Cell destination) {
		this.destination = destination;
	}
	
	public final void setPath (ArrayList<Cell> path) {
		this.path = path;
	}
	
	public final void terminate() {
		running = false;
	}
}
