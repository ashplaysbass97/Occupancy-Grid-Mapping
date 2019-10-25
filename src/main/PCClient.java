package main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.JPanel;
import java.net.*;

import javax.imageio.ImageIO;
import javax.swing.*;  

public class PCClient extends JFrame {
	
	// java swing window
	static JFrame myFrame = new JFrame("Robot progress"); 
  
	// label to display robot data 
	static JLabel lRobotStats; 
	static JLabel[] lOccupancyProbabilities = new JLabel[42];
	static JLabel[] robotStates = new JLabel[42];
	static int currentCell;
	static String[] gridProbabilities;
	static ImageIcon robotIcon = new ImageIcon("robot.png");
	static ImageIcon empty = new ImageIcon("empty.png");
	
	// bufferd reader this will connect to the socket PCMonitor.java uses
	static BufferedReader in;

	public static void main(String[] args) throws IOException {
		
		// set window size
		myFrame.setResizable(false);
		myFrame.setSize(1280, 640);

		// create master panel
		JPanel masterPanel = new JPanel();
		masterPanel.setLayout(new GridLayout(1, 2));
		
		// create robot stats panel
		JPanel robotStats = new JPanel();
		lRobotStats = new JLabel(); 
		
		// create the occupancy grid panel
		JPanel occupancyGrid = new JPanel(new GridLayout(6,7));
		JPanel[][] gridPanels = new JPanel[6][7];
		
		int count = 0;
		for (int i = 5; i >= 0; i--) {
			for (int j = 0; j < 7; j++) {
				gridPanels[i][j] = new JPanel(new GridLayout(3, 1));
				lOccupancyProbabilities[count] = new JLabel("?");
				robotStates[count] = new JLabel();
				gridPanels[i][j].add(robotStates[count]);
				gridPanels[i][j].add(lOccupancyProbabilities[count]);
				
				gridPanels[i][j].add(new JLabel("(" + j + ", " + i + ")"));
				gridPanels[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
				occupancyGrid.add(gridPanels[i][j]);
				count++;
			}
		}
		
		// add label to panel 
		robotStats.add(lRobotStats); 
		masterPanel.add(robotStats);
		masterPanel.add(occupancyGrid);
		
		// add panel to frame 
		myFrame.add(masterPanel); 
		
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setVisible(true); 
		lRobotStats.setText(
				"<html><h1>Robot 14</h1>\n"
					+ "Sensor data:<ul>"
						+ "<li>Sonar distance: </li>"
						+ "<li>Gyro angle: </li>"
						+ "<li>Left colour: </li>"
						+ "<li>Right colour: </li>"
					+ "</ul>Movement information:<ul>"
						+ "<li>Status: </li>"
						+ "<li>Type: </li>"
						+"<li>Heading: </li>"
					+ "</ul>Navigation strategy:<ul>"
						+ "<li>Next destination: </li>"
						+ "<li>Current path: </li>"
					+ "</ul>");
		
		//IP of the robot
		String ip = "192.168.70.163"; 
		// String ip = "192.168.0.35";
		
		if(args.length > 0)
			ip = args[0];
		
		//Create a new socket connection with the robots PCMonitor.java.
		Socket sock = new Socket(ip, 1234);
		System.out.println("Connected");
		
		//Get Input from PCMonitor.
		in = new BufferedReader(
					new InputStreamReader(sock.getInputStream()));
		
		//Constantly update values.
		while (true) {
			updateValues();
		}
	}
	
	/**
	 * Displays data about the robot as an HTML list in a java swing window.
	 */
	public static void updateValues() {
		try {
			lRobotStats.setText(
					"<html><h1>Robot 14</h1>\n"
						+ "Sensor data:<ul>"
							+ "<li>Sonar distance: " + in.readLine() + "</li>"
							+ "<li>Gyro angle: " + in.readLine() + "</li>"
							+ "<li>Corrected Gyro angle: " + in.readLine() + "</li>"
							+ "<li>Left colour: " + in.readLine() + "</li>"
							+ "<li>Right colour: " + in.readLine() + "</li>"
						+ "</ul>Movement information:<ul>"
							+ "<li>Status: " + in.readLine() + "</li>"
							+ "<li>Type: " + in.readLine() + "</li>"
							+"<li>Heading: " + in.readLine() + "</li>"
						+ "</ul>Navigation strategy:<ul>"
							+ "<li>Next destination: " + in.readLine() + "</li>"
							+ "<li>Current path: " + in.readLine() + "</li>"
						+ "</ul>");
			
			// fill in display grid with occupation probablities and robot position.
			gridProbabilities = in.readLine().split(",");
			
			String currentCord = in.readLine();
			int x = Integer.parseInt(currentCord.split(",")[0]);
			int y = Integer.parseInt(currentCord.split(",")[1]);
			
			for (int i = 0; i < gridProbabilities.length; i++) {
				if (gridProbabilities[i] == "-1.0") {
					lOccupancyProbabilities[i].setText("?");
				} else if (gridProbabilities[i] == "-2.0") {
					lOccupancyProbabilities[i].setText("Unvisitable");
				} else {
					lOccupancyProbabilities[i].setText(gridProbabilities[i]);
				}
				
				if (i == x + (5 - y) * 7) {
					robotStates[i].setIcon(robotIcon);
				} else {
					robotStates[i].setIcon(empty);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}


