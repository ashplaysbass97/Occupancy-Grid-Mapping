package behaviors;

import java.util.ArrayList;

import lejos.hardware.Button;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;
import main.Cell;
import main.Grid;
import main.PilotRobot;
import main.Sensor;
import monitors.PilotMonitor;

public class ScanBehavior implements Behavior {
	private boolean suppressed = false;
	private Grid grid;
	private PilotRobot myRobot;
	private MovePilot myPilot;
	private PilotMonitor myMonitor;
	private OdometryPoseProvider opp;
	private Sensor ultrasound;
	
	public ScanBehavior(PilotRobot myRobot, PilotMonitor myMonitor, Grid grid) {
		this.myRobot = myRobot;
		this.myMonitor = myMonitor;
		this.grid = grid;
		this.ultrasound = new Sensor(myRobot, grid);
		
		myPilot = myRobot.getPilot();
		opp = myRobot.getOdometryPoseProvider();
	}
	
	public final void suppress() {
		suppressed = true;
	}
	
	public final boolean takeControl() {
		return myRobot.isScanRequired();
	}

	public final void action() {
		suppressed = false;
		if (!suppressed) {
			
			OdometryPoseProvider opp = myRobot.getOdometryPoseProvider();
			double heading = opp.getPose().getHeading();
			Cell current = grid.getCurrentCell();
			Cell left, inFront, right;
			
			if (heading > -45 && heading <= 45) {
				//north
				left = grid.getCell(current.getX() - 1, current.getY());
				inFront = grid.getCell(current.getX(), current.getY() + 1);
				right = grid.getCell(current.getX() + 1, current.getY());
			} else if (heading > 45 && heading <= 135) {
				//east
				left = grid.getCell(current.getX(), current.getY() + 1);
				inFront = grid.getCell(current.getX() + 1, current.getY());
				right = grid.getCell(current.getX(), current.getY() - 1);
			} else if (heading > -135 && heading <= -45) {
				//west
				left = grid.getCell(current.getX(), current.getY() - 1);
				inFront = grid.getCell(current.getX() - 1, current.getY());
				right = grid.getCell(current.getX(), current.getY() + 1);
			} else {
				//south
				left = grid.getCell(current.getX() + 1, current.getY());
				inFront = grid.getCell(current.getX(), current.getY() - 1);
				right = grid.getCell(current.getX() - 1, current.getY());
			}
			
			if (left != null) {
				ultrasound.sensorRotateLeft();
				ultrasound.calculateCellsInSonarCone(heading-90);
			}
			
			if (right != null) {
				ultrasound.sensorRotateRight();
				ultrasound.calculateCellsInSonarCone(heading+90);
			}
			
			if (inFront != null) {
				ultrasound.sensorRotateCentre();
				ultrasound.calculateCellsInSonarCone(heading);
			}
			myRobot.setScanRequired(false);
			
//			while (!Button.ENTER.isDown()) {
//				
//			}
		}
	}
}
