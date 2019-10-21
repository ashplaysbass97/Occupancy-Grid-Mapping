package behaviors;
import java.io.IOException;
import java.net.*;

import lejos.hardware.Button;
import lejos.robotics.subsumption.Behavior;
import main.Grid;
import main.PilotRobot;
import monitors.PilotMonitor;

public class ExitBehavior implements Behavior{
	
	private boolean suppressed = false;
	private PilotRobot myRobot;
	private PilotMonitor myMonitor;
	private Grid grid;
	private ServerSocket server;
	private boolean isComplete = false;
	
	public ExitBehavior(PilotRobot robot, PilotMonitor monitor, Grid grid, ServerSocket socket){
		this.myRobot = robot;
		this.myMonitor = monitor;
		this.grid = grid;
		this.server = socket;
	}
	
	public final void suppress() {
		suppressed = true;
	}
	
	public final boolean takeControl() {
		isComplete = !grid.areCellsUnknown() && grid.getCurrentCell() == grid.findUsingCoordinate(0, 0);
		return Button.ESCAPE.isDown() || isComplete;
	}

	public final void action() {
		suppressed = false;
		// TODO stop the monitor thread, close the robot and close the server
		System.exit(0);
	}
}
