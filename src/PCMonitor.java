import lejos.hardware.Battery;
import java.io.*;
import java.net.*;

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
	
	public PCMonitor(Socket client, PilotRobot robot) {
		this.client = client;
		this.robot = robot;
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
