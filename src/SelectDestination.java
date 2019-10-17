import java.util.*;

import lejos.robotics.subsumption.Behavior;

public class SelectDestination implements Behavior  {
	private boolean suppressed;
	private ArrayList<Cell> grid;
	private Cell currentCell;
	
	public SelectDestination(ArrayList<Cell> grid, Cell currentCell){
		this.grid = grid;
		this.currentCell = currentCell;
	}
	
	public final void suppress() {
		suppressed = true;
	}
	
	public final boolean takeControl() {
		return true;
	}

	public final void action() {
		suppressed = false;
		setDistances();
		Cell destination = selectDestination();
		System.out.println("Next destination: (" + destination.getX() + ", " + destination.getY() + ").");
	}
	
	/**
	 * Updates the distance to the current cell for all cells in the grid.
	 */
	private void setDistances() {
		for (Cell cell : grid) {
			cell.setDistance(currentCell.getX(), currentCell.getY());
		}
	}
	
	/**
	 * Sorts cells based on the distance from the current position and the number of unknown neighbours.
	 * @return the selected destination to travel to.
	 */
	private Cell selectDestination() {
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
