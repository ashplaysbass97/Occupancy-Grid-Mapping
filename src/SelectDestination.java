import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import lejos.robotics.subsumption.Behavior;

public class SelectDestination implements Behavior  {
	private boolean suppressed;
	private ArrayList<Cell> grid;
	private Cell currentCell;
	
	public SelectDestination(ArrayList<Cell> grid, Cell currentCell){
		this.grid = grid;
		this.currentCell = currentCell;
	}
	
	public void suppress() {
		suppressed = true;
	}
	
	public boolean takeControl() {
		return true;
	}

	public void action() {
		suppressed = false;
		setDistances();
		Cell destination = selectDestination();
		System.out.println("Next destination: (" + destination.getX() + ", " + destination.getY() + ").");
	}
	
	private void setDistances() {
		for (Cell cell : grid) {
			cell.setDistance(currentCell.getX(), currentCell.getY());
		}
	}
	
	private Cell selectDestination() {
		// sort cells on distance from current cell
		Collections.sort(grid, new Comparator<Cell>() {
			  @Override
			  public int compare(Cell a, Cell b) {
				  return Double.compare(a.getDistance(), b.getDistance());
			  }
		});
		
		// sort cells on number of unknown neighbours
		Collections.sort(grid, new Comparator<Cell>() {
			  @Override
			  public int compare(Cell a, Cell b) {
				  return Integer.compare(b.countUnknownNeighbours(), a.countUnknownNeighbours());
			  }
		});
		
		return grid.get(0);
	}
}
