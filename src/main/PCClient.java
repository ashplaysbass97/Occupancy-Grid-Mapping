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
    static JFrame f; 
  
    // label to display robot data 
    static JLabel lRobotStats; 
    static JLabel[] lOccupancyProbabilities = new JLabel[42];
    static JLabel[] robotStates = new JLabel[42];
    static int currentCell;
    static String[] gridProbabilities;
    static ImageIcon robotIcon = new ImageIcon("RobotIcon.png");
    static ImageIcon empty = new ImageIcon("empty.png");
    
    
    
    
    //Bufferd reader this will connect to the socket PCMonitor.java uses
    static BufferedReader in;
  
    // default constructor 
    PCClient() 
    { 
    } 
	
	public static void main(String[] args) throws IOException {
		
		 // create a new frame to store text field and button 
        f = new JFrame("Robot progress"); 
  
        // create a label to display text 
        lRobotStats = new JLabel(); 
  
        // create a panel 
        JPanel masterPanel = new JPanel();
        masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.LINE_AXIS));
        JPanel robotStats = new JPanel(); 
        robotStats.setPreferredSize(new Dimension(300, 300));
        JPanel occupancyGrid = new JPanel(new GridLayout(6,7));
        JPanel[][] gridPanels = new JPanel[6][7];
        int count = 0;
        for (int i = 0; i < 6; i++) {
          for (int j = 0; j < 7; j++) {
            gridPanels[i][j] = new JPanel(new GridLayout(2,1));
            lOccupancyProbabilities[count] = new JLabel("?");
            robotStates[count] = new JLabel();
            gridPanels[i][j].add(robotStates[count]);
            gridPanels[i][j].add(lOccupancyProbabilities[count]);
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
        f.add(masterPanel); 
  
        // set the size of frame 
        f.setSize(700, 700); 
       
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true); 
        lRobotStats.setText("null");
        //IP of the robot
//		String ip = "192.168.70.163"; 
		String ip = "192.168.0.35"; 
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
        			"<html>\n"
        				+ "<ul>\n"
	    					+ "<li>" + in.readLine() + "\n"
				        	+ "<li>" + in.readLine() + "\n"
				        	+ "<li>" + in.readLine() + "\n"
				        	+ "<li>" + in.readLine() + "\n"
				        	+ "<li>" + in.readLine() + "\n"
				        	+ "<li>" + in.readLine() + "\n"
				        	+ "<li>" + in.readLine() + "\n"
			        	+ "</ul>\n");
        	
        	//fill in display grid with occupation probablities and robot position.
        	gridProbabilities = in.readLine().split(",");
        	String currentCord = in.readLine();
            int x = Integer.parseInt(currentCord.split(",")[0]);
            int y = Integer.parseInt(currentCord.split(",")[1]);
        	for (int i = 0; i < gridProbabilities.length; i++) {
        	  lOccupancyProbabilities[i].setText(gridProbabilities[i]);
        	  if (((y*7) + x) != i) {
        	    robotStates[i].setIcon(null);
        	  } else {
        	    robotStates[i].setIcon(robotIcon);
        	  }
        	  
        	}
        	  
        	
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
}


