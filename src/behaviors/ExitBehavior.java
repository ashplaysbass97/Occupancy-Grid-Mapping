package behaviors;
import java.io.IOException;
import java.net.*;

import lejos.hardware.Button;
import lejos.robotics.subsumption.Behavior;
import main.PilotRobot;
import monitors.PilotMonitor;

public class ExitBehavior implements Behavior{
	
	private boolean suppressed = false;
	private ServerSocket server;
	PilotRobot myRobot;
	PilotMonitor myMonitor;
	
	public ExitBehavior(PilotRobot robot, PilotMonitor monitor, ServerSocket socket){
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
		// TODO stop the monitor thread, close the robot and close the server
		System.exit(0);
	}
}
