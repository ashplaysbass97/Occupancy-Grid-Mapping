package behaviors;
import java.util.*;

import lejos.hardware.Button;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.Pose;
import lejos.robotics.subsumption.Behavior;
import main.Cell;
import main.Grid;
import main.PathFinder;
import main.PilotRobot;
import monitors.PCMonitor;

public class MoveBehavior implements Behavior {
	private boolean suppressed = false;
	private PilotRobot myRobot;
	private MovePilot myPilot;
	private OdometryPoseProvider opp;
	private Grid grid;
	private PathFinder pathFinder;
	private ArrayList<Cell> path = new ArrayList<Cell>();
	private double occupiedCellProbability;
	private PCMonitor pcMonitor;
	
	private static final int HEADING_NORTH = 0;
	private static final int HEADING_WEST = -90;
	private static final int HEADING_EAST = 90;
	private static final int HEADING_SOUTH = 180;
	
	public MoveBehavior(PilotRobot myRobot, Grid grid, PCMonitor pcMonitor, boolean useSensorModel) {
		this.myRobot = myRobot;
		myPilot = myRobot.getPilot();
		opp = myRobot.getOdometryPoseProvider();
		
		this.grid = grid;
		occupiedCellProbability = useSensorModel ? 0.7 : 1;
		pathFinder = new PathFinder(grid.getGrid(), occupiedCellProbability);
		
		this.pcMonitor = pcMonitor;
	}
	
	public MoveBehavior(PilotRobot myRobot, Grid grid, boolean useSensorModel) {
		this.myRobot = myRobot;
		myPilot = myRobot.getPilot();
		opp = myRobot.getOdometryPoseProvider();
		
		this.grid = grid;
		occupiedCellProbability = useSensorModel ? 0.7 : 1;
		pathFinder = new PathFinder(grid.getGrid(), occupiedCellProbability);
	}
	
	public final void suppress() {
		suppressed = true;
	}
	
	public final boolean takeControl() {
		return true;
	}

	public final void action() {
		suppressed = false;
		Cell destination = null;
		
		// select a destination and build the path
		if (path.isEmpty()) {
			destination = grid.areCellsUnknown() ? selectDestination() : grid.getCell(0, 0);
			path = pathFinder.findPath(grid.getCurrentCell(), destination);
			pcMonitor.setDestination(destination);
			pcMonitor.setPath(path);
		}
		
		if (path.get(0).getOccupancyProbability() >= occupiedCellProbability) {
			destination = grid.areCellsUnknown() ? selectDestination() : grid.getCell(0, 0);
			path = pathFinder.findPath(grid.getCurrentCell(), destination);
			pcMonitor.setDestination(destination);
			pcMonitor.setPath(path);
		}
		
		if (path != null) {
			// move to the next step
			Cell nextStep = path.remove(0);
			followPath(nextStep.getX(), nextStep.getY());
		} else {
			destination.setIsBlocked(true);
		}
	}
	
	/**
	 * Sorts cells based on the distance from the current position and the number of unknown neighbours.
	 * @return the selected destination to travel to.
	 */
	private Cell selectDestination() {
		for (Cell cell : grid.getGrid()) {
			cell.setValue(grid.getCurrentCell().getX(), grid.getCurrentCell().getY());
		}
		
		ArrayList<Cell> sortableGrid = new ArrayList<>(grid.getGrid());
		Collections.sort(sortableGrid, new Comparator<Cell>() {
			@Override
			public int compare(Cell a, Cell b) {
				return Double.compare(a.getValue(), b.getValue());
			}
		});
		
		for (Cell cell : sortableGrid) {
			if (cell.getOccupancyProbability() < occupiedCellProbability && !cell.isBlocked() && !(cell.getOccupancyProbability() == 0 && cell.countUnknownNeighbours() == 0)) {
				return cell;
			}
		}
		
		return null;
	}
	//If the gyroscope has accumilated an angle greater than 360 or less then -360 its returns a reset value.
	public float correctedGyroReading() {
		float gyroAngle = myRobot.getAngle();
		if (gyroAngle > 0) {
			if (gyroAngle/360 > 1) {
				gyroAngle -= 360*(gyroAngle%360);
			}
		} else {
			if (gyroAngle/360 < -1) {
				gyroAngle += 360*(gyroAngle%360);
			}
		}
		return gyroAngle;
	}
	/**
	 * Moves towards the coordinates of a neighbouring cell.
	 * @param x the x-coordinate of the cell to travel towards
	 * @param y the y-coordinate of the cell to travel towards
	 */
	public void followPath(int x, int y) {
		float gyroAngle;
		//reset gyroScope since it accumilates degrees in direction of rotation.

		if (x - opp.getPose().getX() > 0) {
			myPilot.rotate(getHeadingError(HEADING_EAST));
			gyroAngle = correctedGyroReading();
			//correct for error
			if (!((gyroAngle > 89 && gyroAngle < 91)||(gyroAngle > -271 && gyroAngle < -269))){
				if (gyroAngle > 0) {
					myPilot.rotate(90-gyroAngle);
				} else {
					myPilot.rotate(-270-gyroAngle);
				}
			}
		} else if (x - opp.getPose().getX() < 0) {
			myPilot.rotate(getHeadingError(HEADING_WEST));
			gyroAngle = correctedGyroReading();
			//correct for error
			if (!((gyroAngle < -89 && gyroAngle > -91)||(gyroAngle < 271 && gyroAngle > 269))){
				if (gyroAngle > 0) {
					myPilot.rotate(-90-gyroAngle);
				} else {
					myPilot.rotate(270-gyroAngle);
				}
			}
		} else if (y - opp.getPose().getY() > 0) {
			myPilot.rotate(getHeadingError(HEADING_NORTH));
			gyroAngle = correctedGyroReading();
			//correct for error
			if (!((gyroAngle > 359 || gyroAngle < 1)||(gyroAngle > -1 && gyroAngle < -359))){
				if (gyroAngle > 0) {
					myPilot.rotate(0-gyroAngle);
				} else {
					myPilot.rotate(-360-gyroAngle);
				}
			}
		} else {
			myPilot.rotate(getHeadingError(HEADING_SOUTH));
			gyroAngle = correctedGyroReading();
			//correct for error
			if (!((gyroAngle > 179 && gyroAngle < 181)||(gyroAngle < -179 && gyroAngle > -181))){
				if (gyroAngle > 0) {
					myPilot.rotate(180-gyroAngle);
				} else {
					myPilot.rotate(-180-gyroAngle);
				}
			}
		}
		//get a distance to enaest object for error correction
//		double distance1 = myRobot.getDistance();
		// TODO figure out the correct distance to travel
		myPilot.travel(25);
		//take new distance afetr travel find the diffrence and see how far of 25cm it was.
//		double distance2 = myRobot.getDistance();
//		myPilot.travel(distance1-distance2-25);
		// set pose and current cell of grid object
		opp.setPose(new Pose(x, y, opp.getPose().getHeading()));
		grid.setCurrentCell(grid.getCell(x, y));
		
		// only scan neighbours if cell hasn't already been visited
		if (!grid.getCurrentCell().hasBeenVisited()) {
			myRobot.setScanRequired(true);
		}
	}

	/**
	 * Calculates the rotation required to be heading in the correct direction for a given destination.
	 * @param destination the heading of the destination from the current position
	 * @return the amount of rotation required
	 */
	public double getHeadingError(int destination) {
		double initial = opp.getPose().getHeading();
		double diff = destination - initial;
		double absDiff = Math.abs(diff);

		if (absDiff <= 180) {
			return absDiff == 180 ? absDiff : diff;
		} else if (destination > initial) {
			return absDiff - 360;
		} else {
			return 360 - absDiff;
		}
	}
}
