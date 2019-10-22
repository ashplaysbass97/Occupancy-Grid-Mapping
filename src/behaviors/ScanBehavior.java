package behaviors;

import java.util.ArrayList;

import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;
import main.Cell;
import main.Grid;
import main.PilotRobot;
import monitors.PilotMonitor;

public class ScanBehavior implements Behavior {
	private boolean suppressed = false;
	private Grid grid;
	private PilotRobot myRobot;
	private MovePilot myPilot;
	private PilotMonitor myMonitor;
	private OdometryPoseProvider opp;
	
	public ScanBehavior(PilotRobot myRobot, PilotMonitor myMonitor, Grid grid) {
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
				left = grid.findUsingCoordinate(current.getX() - 1, current.getY());
				inFront = grid.findUsingCoordinate(current.getX(), current.getY() + 1);
				right = grid.findUsingCoordinate(current.getX() + 1, current.getY());
			} else if (heading > 45 && heading <= 135) {
				//east
				left = grid.findUsingCoordinate(current.getX(), current.getY() + 1);
				inFront = grid.findUsingCoordinate(current.getX() + 1, current.getY());
				right = grid.findUsingCoordinate(current.getX(), current.getY() - 1);
			} else if (heading > -135 && heading <= -45) {
				//west
				left = grid.findUsingCoordinate(current.getX(), current.getY() - 1);
				inFront = grid.findUsingCoordinate(current.getX() - 1, current.getY());
				right = grid.findUsingCoordinate(current.getX(), current.getY() + 1);
			} else {
				//south
				left = grid.findUsingCoordinate(current.getX() + 1, current.getY());
				inFront = grid.findUsingCoordinate(current.getX(), current.getY() - 1);
				right = grid.findUsingCoordinate(current.getX() - 1, current.getY());
			}
			
			if (left != null) {
				myRobot.rotateSensorLeft();
				if (myRobot.getDistance() < 25) {
					left.setOccupancyProbability(1);
				} else {
					left.setOccupancyProbability(0);
				}
			}
			
			if (right != null) {
				myRobot.rotateSensorRight();
				if (myRobot.getDistance() < 25) {
					right.setOccupancyProbability(1);
				} else {
					right.setOccupancyProbability(0);
				}
			}
			
			if (inFront != null) {
				myRobot.rotateSensorCentre();
				if (myRobot.getDistance() < 25) {
					inFront.setOccupancyProbability(1);
				} else {
					inFront.setOccupancyProbability(0);
				}
			}
			myRobot.setScanRequired(false);
		}
	}
}
