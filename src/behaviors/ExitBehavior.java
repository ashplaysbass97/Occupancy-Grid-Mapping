package behaviors;

import lejos.hardware.Button;
import lejos.robotics.subsumption.Behavior;
import main.Grid;
import main.PilotRobot;
import monitors.PCMonitor;
import monitors.PilotMonitor;

public class ExitBehavior implements Behavior{
	
	private boolean suppressed = false;
	private PilotRobot myRobot;
	private PilotMonitor pilotMonitor;
	private PCMonitor pcMonitor = null;
	private Grid grid;
	
	public ExitBehavior(PilotRobot robot, PilotMonitor pilotMonitor, PCMonitor pcMonitor,Grid grid) {
		this.myRobot = robot;
		this.pilotMonitor = pilotMonitor;
		this.pcMonitor = pcMonitor;
		this.grid = grid;
	}
	
	public ExitBehavior(PilotRobot robot, PilotMonitor pilotMonitor) {
		this.myRobot = robot;
		this.pilotMonitor = pilotMonitor;
	}
	
	public final void suppress() {
		suppressed = true;
	}
	
	public final boolean takeControl() {
		// TODO take control when map finished and back in starting position
		
		return (!grid.areCellsUnknown() && grid.noOfOccupiedCells() <=4);
	}

	public final void action() {
		suppressed = false;
		myRobot.getBrick().getAudio().systemSound(0);
		while (Button.ESCAPE.isDown() && !suppressed) {
			pilotMonitor.terminate();
			myRobot.closeRobot();
			if (pcMonitor != null) {
				pcMonitor.terminate();
			}
			System.exit(0);
		}
	}
}
