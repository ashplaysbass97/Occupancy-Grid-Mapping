package main;
import behaviors.ExitBehavior;
import behaviors.MoveBehavior;
import behaviors.ScanBehavior;

import java.io.*;
import java.net.*;

import lejos.hardware.Sound;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import monitors.PCMonitor;
import monitors.PilotMonitor;

public class Main {
	private static final int PORT = 1234; // server port between pc client and robot
	private static ServerSocket server; // server socket used between robot and pc client.
	
	private static boolean useSensorModel = false;
	private static boolean usePCMonitor = true;

	public static void main(String[] args) {
		// initalise the grid, robot and monitor
		Grid grid = new Grid();
		PilotRobot myRobot = new PilotRobot();
		PilotMonitor myMonitor = new PilotMonitor(grid);		

		// start server and create PCMonitor thread
		PCMonitor pcMonitor = null;
		if (usePCMonitor) {
			try {
				server = new ServerSocket(PORT);
				System.out.println("Awaiting client..");
				Socket client = server.accept();
				pcMonitor = new PCMonitor(client, myRobot, grid);
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		
		// start the monitors
		myMonitor.start();
		if (usePCMonitor) {
			pcMonitor.start();
		}

		// set up the behaviours for the arbitrator and construct it
		Behavior b1 = usePCMonitor ? new MoveBehavior(myRobot, grid, pcMonitor, useSensorModel) : new MoveBehavior(myRobot, grid, useSensorModel);
		Behavior b2 = new ScanBehavior(myRobot, myMonitor, grid, useSensorModel);
		Behavior b3 = usePCMonitor ? new ExitBehavior(myRobot, myMonitor, pcMonitor, grid) : new ExitBehavior(myRobot, myMonitor);
		Behavior [] behaviorArray = {b1, b2, b3};
		Arbitrator arbitrator = new Arbitrator(behaviorArray);
		File file=new File("soundEffects/turret_deploy.wav");
		Sound.playSample(file);
		arbitrator.go();
	}
}
