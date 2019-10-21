package main;
import behaviors.ExitBehavior;
import behaviors.MoveBehavior;
import behaviors.ScanBehavior;

import java.io.*;
import java.net.*;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import monitors.PCMonitor;
import monitors.PilotMonitor;

public class Main {
	private static final int PORT = 1234; // server port between pc client and robot
	private static ServerSocket server; // server socket used between robot and pc client.

	public static void main(String[] args) {
		// initalise the grid, robot and monitor
		Grid grid = new Grid();
		PilotRobot myRobot = new PilotRobot();
		PilotMonitor myMonitor = new PilotMonitor(grid);		

		// start server and create PCMonitor thread
		PCMonitor pcMonitor = null;
		try {
			server = new ServerSocket(PORT);
			System.out.println("Awaiting client..");
			Socket client = server.accept();
			pcMonitor = new PCMonitor(client, myRobot, grid);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// start the monitors
		myMonitor.start();
		pcMonitor.start();

		// set up the behaviours for the arbitrator and construct it
		Behavior b1 = new MoveBehavior(myRobot, grid);
		Behavior b2 = new ScanBehavior(myRobot, myMonitor, grid);
		// TODO behaviour for obstacle avoidance
		// TODO behaviour for returning to the starting point once the map is complete
		Behavior b3 = new ExitBehavior(myRobot, myMonitor, server);
		Behavior [] behaviorArray = {b1, b2, b3};
		Arbitrator arbitrator = new Arbitrator(behaviorArray);
		arbitrator.go();
	}
}
