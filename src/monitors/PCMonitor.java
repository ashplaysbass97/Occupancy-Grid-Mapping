package monitors;
import lejos.hardware.Battery;
import main.Cell;
import main.Grid;
import main.PilotRobot;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

// Used to send data about the robot to a PC client interface.
//TODO add grid interface for probabilitys and robots position.
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

	public PCMonitor(Socket client, PilotRobot robot, Grid grid) {
		this.client = client;
		this.robot = robot;
		this.grid = grid;
		try {
			out = new PrintWriter(client.getOutputStream(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//run the thread
	public void run() {
		while (running) {
			//this is the only way i could stop the lcd screen overlaping text might revisit
			for (int i = 0; i < 9; i++) {
				System.out.println("");
				System.out.flush();
			}

			/*output data for
			 * Battery,
			 * Left touch sensor,
			 * right touch sensor,
			 * ultrasound sensor,
			 * gyroscope,
			 * and motor status
			 */
			out.println("Battery: " + robot.getBatteryVoltage());
			out.println("Left touch sensor: " + robot.isLeftBumperPressed());
			out.println("Right touch sensor: " + robot.isRightBumperPressed());
			out.println("Sonar distance: " + robot.getDistance());
			out.println("Gyro angle: " + robot.getAngle());
			if (robot.getPilot().isMoving()) {
				out.println("Motor status: " + "Moving");
			} else {
				out.println("Motor status: " + "Stationary");
			}
			out.println("  type: " + robot.getPilot().getMovement().getMoveType());
			String probabilityData = "";
			for (Cell cell : grid.getGrid()) {
				probabilityData += cell.getOccupancyProbability() + ",";
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

}
