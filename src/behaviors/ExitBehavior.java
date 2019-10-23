package behaviors;

import lejos.hardware.Button;
import lejos.robotics.subsumption.Behavior;
import main.PilotRobot;
import monitors.PCMonitor;
import monitors.PilotMonitor;

public class ExitBehavior implements Behavior{
	
	private boolean suppressed = false;
	private PilotRobot myRobot;
	private PilotMonitor pilotMonitor;
	
	public ExitBehavior(PilotRobot robot, PilotMonitor pilotMonitor) {
		this.myRobot = robot;
		this.pilotMonitor = pilotMonitor;
	}
	
	public final void suppress() {
		suppressed = true;
	}
	
	public final boolean takeControl() {
		// TODO take control when map finished and back in starting position
		return Button.ESCAPE.isDown();
	}

	public final void action() {
		suppressed = false;
		
		while (Button.ESCAPE.isDown() && !suppressed) {
			pilotMonitor.terminate();
			myRobot.closeRobot();
			
			System.exit(0);
		}
	}
}
