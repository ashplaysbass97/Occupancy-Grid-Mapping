package behaviors;
import java.util.*;

import lejos.robotics.subsumption.Behavior;
import main.Cell;
import main.PathFinder;
import main.PilotRobot;

public class MoveBehavior implements Behavior  {
	private boolean suppressed = false;
	private PilotRobot myRobot;
	private ArrayList<Cell> grid;
	private Cell currentCell;
	private PathFinder pathFinder;
	private ArrayList<Cell> path = new ArrayList<Cell>();
	
	public MoveBehavior(PilotRobot myRobot, ArrayList<Cell> grid, Cell startCell) {
		this.myRobot = myRobot;
		this.grid = grid;
		currentCell = startCell;
		pathFinder = new PathFinder(grid);
	}
	
	public final void suppress() {
		suppressed = true;
	}
	
	public final boolean takeControl() {
		return true;
	}

	public final void action() {
		suppressed = false;
		
		if (path.isEmpty()) {
			Cell destination = selectDestination();
			path = pathFinder.findPath(currentCell, destination);
		}
		
		if (!path.isEmpty() && !suppressed) {
			Cell nextStep = path.remove(0);
			// TODO move to next step
			Thread.yield();
		}
	}
	
	/**
	 * Sorts cells based on the distance from the current position and the number of unknown neighbours.
	 * @return the selected destination to travel to.
	 */
	private Cell selectDestination() {
		for (Cell cell : grid) {
			cell.setDistance(currentCell.getX(), currentCell.getY());
		}
		
		Collections.sort(grid, new Comparator<Cell>() {
			  @Override
			  public int compare(Cell a, Cell b) {
				  return Double.compare(a.getDistance(), b.getDistance());
			  }
		});
		
		Collections.sort(grid, new Comparator<Cell>() {
			  @Override
			  public int compare(Cell a, Cell b) {
				  return Integer.compare(b.countUnknownNeighbours(), a.countUnknownNeighbours());
			  }
		});
		
		return grid.get(0);
	}
}
