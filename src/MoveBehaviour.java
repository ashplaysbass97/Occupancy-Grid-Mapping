import java.util.*;

import lejos.robotics.subsumption.Behavior;

public class MoveBehaviour implements Behavior  {
	private boolean suppressed = false;
	private ArrayList<Cell> grid;
	private Cell currentCell;
	private PathFinder pathFinder;
	private ArrayList<Cell> path;
	
	public MoveBehaviour(ArrayList<Cell> grid, Cell startCell){
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
		Cell destination = selectDestination();
		path = pathFinder.findPath(currentCell, destination);
		
		while (path.size() != 0 && !suppressed) {
			Cell nextStep = path.remove(0);
			// TODO move to next step
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
