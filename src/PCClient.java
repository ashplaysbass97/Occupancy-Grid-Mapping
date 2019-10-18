import java.io.*;
import java.net.*;
import javax.swing.*;  

public class PCClient extends JFrame {
	
	 // java swing window
    static JFrame f; 
  
    // label to display robot data 
    static JLabel lRobotStats; 
    
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
        JPanel p = new JPanel(); 
  
        // add label to panel 
        p.add(lRobotStats); 
  
        // add panel to frame 
        f.add(p); 
  
        // set the size of frame 
        f.setSize(300, 300); 
  
        f.setVisible(true); 
        
        //IP of the robot
		String ip = "192.168.70.163"; 
		
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
			        	+ "</ul>\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
}


