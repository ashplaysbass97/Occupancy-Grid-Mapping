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

public class MoveBehavior implements Behavior {
	private boolean suppressed = false;
	private PilotRobot myRobot;
	private MovePilot myPilot;
	private OdometryPoseProvider opp;
	private Grid grid;
	private PathFinder pathFinder;
	private ArrayList<Cell> path = new ArrayList<Cell>();
	
	private static final int HEADING_NORTH = 0;
	private static final int HEADING_WEST = -90;
	private static final int HEADING_EAST = 90;
	private static final int HEADING_SOUTH = 180;
	
	public MoveBehavior(PilotRobot myRobot, Grid grid) {
		this.myRobot = myRobot;
		myPilot = myRobot.getPilot();
		opp = myRobot.getOdometryPoseProvider();
		
		this.grid = grid;
		pathFinder = new PathFinder(grid.getGrid());
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
			destination = grid.areCellsUnknown() ? selectDestination() : grid.findUsingCoordinate(0, 0);
			path = pathFinder.findPath(grid.getCurrentCell(), destination);
		}
		
		if (path.get(0).getOccupancyProbability() == 1) {
			destination = grid.areCellsUnknown() ? selectDestination() : grid.findUsingCoordinate(0, 0);
			path = pathFinder.findPath(grid.getCurrentCell(), destination);
		}
		
		// move to the next step
		Cell nextStep = path.remove(0);
		followPath(nextStep.getX(), nextStep.getY());
	}
	
	/**
	 * Sorts cells based on the distance from the current position and the number of unknown neighbours.
	 * @return the selected destination to travel to.
	 */
	private Cell selectDestination() {
		for (Cell cell : grid.getGrid()) {
			cell.setDistance(grid.getCurrentCell().getX(), grid.getCurrentCell().getY());
		}
		
		ArrayList<Cell> sortableGrid = new ArrayList<>(grid.getGrid());
		Collections.sort(sortableGrid, new Comparator<Cell>() {
			@Override
			public int compare(Cell a, Cell b) {
				return Double.compare(a.getDistance(), b.getDistance());
			}
		});
		
		Collections.sort(sortableGrid, new Comparator<Cell>() {
			@Override
			public int compare(Cell a, Cell b) {
				return Integer.compare(b.countUnknownNeighbours(), a.countUnknownNeighbours());
			}
		});
		
		return sortableGrid.get(0);
	}
	
	/**
	 * Moves towards the coordinates of a neighbouring cell.
	 * @param x the x-coordinate of the cell to travel towards
	 * @param y the y-coordinate of the cell to travel towards
	 */
	public void followPath(int x, int y) {
		if (x - opp.getPose().getX() > 0) {
			myPilot.rotate(getHeadingError(HEADING_EAST));
		} else if (x - opp.getPose().getX() < 0) {
			myPilot.rotate(getHeadingError(HEADING_WEST));
		} else if (y - opp.getPose().getY() > 0) {
			myPilot.rotate(getHeadingError(HEADING_NORTH));
		} else {
			myPilot.rotate(getHeadingError(HEADING_SOUTH));
		}
		
		// TODO figure out the correct distance to travel
		myPilot.travel(25);
		
		// set pose and current cell of grid object
		opp.setPose(new Pose(x, y, opp.getPose().getHeading()));
		grid.setCurrentCell(grid.findUsingCoordinate(x, y));
		
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
