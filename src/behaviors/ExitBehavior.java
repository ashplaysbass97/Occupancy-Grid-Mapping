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
	private PCMonitor pcMonitor;
	
	public ExitBehavior(PilotRobot robot, PilotMonitor pilotMonitor, PCMonitor pcMonitor) {
		this.myRobot = robot;
		this.pilotMonitor = pilotMonitor;
		this.pcMonitor = pcMonitor;
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
			pcMonitor.terminate();
			pilotMonitor.terminate();
			myRobot.closeRobot();
			
			System.exit(0);
		}
	}
}
