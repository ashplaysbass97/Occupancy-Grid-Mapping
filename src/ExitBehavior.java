import java.io.IOException;
import java.net.*;

import lejos.hardware.Button;
import lejos.robotics.subsumption.Behavior;

public class ExitBehavior implements Behavior{
	
	private boolean suppressed = false;
	private ServerSocket server;
	Robot myRobot;
	Monitor myMonitor;
	
	public ExitBehavior(Robot robot, Monitor monitor, ServerSocket socket){
		this.myRobot = robot;
		this.myMonitor = monitor;
		this.server = socket;
	}
	
	public final void suppress() {
		suppressed = true;
	}
	
	public final boolean takeControl() {
		return Button.ESCAPE.isDown();
	}

	public final void action() {
		suppressed = false;
        // stop the monitor thread and close the robot and close the server
		System.exit(0);
//		System.out.println("Next destination: (" + destination.getX() + ", " + destination.getY() + ").");
	}
}
