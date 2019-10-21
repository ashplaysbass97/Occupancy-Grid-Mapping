package behaviors;

import java.util.ArrayList;

import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;
import main.Cell;
import main.PilotRobot;
import monitors.PilotMonitor;

public class ScanBehavior implements Behavior {
	private boolean suppressed = false;
	private ArrayList<Cell> grid;
	private PilotRobot myRobot;
	private MovePilot myPilot;
	private PilotMonitor myMonitor;
	private OdometryPoseProvider opp;
	
	public ScanBehavior(PilotRobot myRobot, PilotMonitor myMonitor, ArrayList<Cell> grid) {
		this.myRobot = myRobot;
		this.myMonitor = myMonitor;
		this.grid = grid;
		
		myPilot = myRobot.getPilot();
		opp = myRobot.getOdometryPoseProvider();
	}
	
	public final void suppress() {
		suppressed = true;
	}
	
	public final boolean takeControl() {
		// TODO if in new cell that hasn't been visited before
		return true;
	}

	public final void action() {
		suppressed = false;
	}
}
