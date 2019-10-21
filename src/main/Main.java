package main;
import java.util.ArrayList;

import behaviors.ExitBehavior;
import behaviors.MoveBehavior;
import behaviors.ScanBehavior;

import java.io.*;
import java.net.*;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import monitors.PCMonitor;
import monitors.PilotMonitor;

public class Main {
	private static final int PORT = 1234; // server port between pc client and robot
	private static ServerSocket server; // server socket used between robot and pc client.
	private static String[] occupancyGrid = new String[42]; 

	public static void main(String[] args) {

		// give starting values to occupancyGrid (Grid position 0 is 0.0 since this is where the robot is assumed to start.);
		occupancyGrid[0] = "0.0";
		for (int i = 1; i < 42; i++) {
			occupancyGrid[i] = "?";
		}
		
		// initalise the grid, robot and monitor
		Grid grid = new Grid();
		PilotRobot myRobot = new PilotRobot();
		PilotMonitor myMonitor = new PilotMonitor(occupancyGrid);		

		// start server and create PCMonitor thread
		PCMonitor pcMonitor = null;
		try {
			server = new ServerSocket(PORT);
			System.out.println("Awaiting client..");
			Socket client = server.accept();
			pcMonitor = new PCMonitor(client, myRobot, occupancyGrid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
