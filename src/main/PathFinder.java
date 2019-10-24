package main;
import java.util.ArrayList;
import java.util.Collections;

public class PathFinder {
	private ArrayList<Cell> grid;
	private double occupiedCellProbability;
	
	public PathFinder(ArrayList<Cell> grid, double occupiedCellProbability) {
		this.grid = grid;
		this.occupiedCellProbability = occupiedCellProbability;
	}
	
	public ArrayList<Cell> findPath(Cell startCell, Cell destinationCell) {
		ArrayList<Cell> openCells = new ArrayList<Cell>(); // list of currently discovered nodes that are not yet evaluated
		ArrayList<Cell> closedCells = new ArrayList<Cell>(); // list of nodes already evaluated
		ArrayList<Cell> path = new ArrayList<Cell>();
		boolean isPathFound = false;
		
		openCells.add(startCell); // add startCell to openCells
		
		for (Cell cell : grid) {
			// add occupied cells to closedCells
			if (cell.getOccupancyProbability() >= occupiedCellProbability) {
				closedCells.add(cell);
			}
			cell.setPreviousCell(null); // remove all previous cells
		}
		
		while (!isPathFound) {
			if (!openCells.isEmpty()) {
				// select cell with lowest cost estimate as currentCell
				Cell currentCell = openCells.get(0);
				for (int i = 1; i < openCells.size(); i++) {
					if (openCells.get(i).getF() < currentCell.getF()) {
						currentCell = openCells.get(i);
					}
				}
				
				// return path if destinationCell has been reached
				if (currentCell == destinationCell) {
					Cell tmpCell = currentCell;
					path.add(tmpCell);
					while (tmpCell.getPreviousCell() != null && tmpCell.getPreviousCell() != startCell) {
						path.add(tmpCell.getPreviousCell());
						tmpCell = tmpCell.getPreviousCell();
					}
					isPathFound = true;
					Collections.reverse(path);
					return path;
				}
				
				// move cell from openCells to closedCells
				openCells.remove(currentCell);
				closedCells.add(currentCell);
				
				ArrayList<Cell> neighbours = currentCell.getNeighbours();
				
				// build path
				for (int i = 0; i < neighbours.size(); i++) {
					Cell neighbour = neighbours.get(i);
					if (closedCells.contains(neighbour) == false) {
						int tmpG = currentCell.getG() + 1;
						if (openCells.contains(neighbour)) {
							if (tmpG < neighbour.getG()) {
								neighbour.setG(tmpG);
							}
						} else {
							neighbour.setG(tmpG);
							openCells.add(neighbour);
						}
						neighbour.setH(calculateHeuristicDistance(neighbour, destinationCell));
						neighbour.setF(neighbour.getG() + neighbour.getH());
						neighbour.setPreviousCell(currentCell);
					}
				}
			
			// catch if openCells is empty
			} else {
				isPathFound = true;
				return null;
			}
		}
		return null;
	}
	
	private int calculateHeuristicDistance(Cell a, Cell b) {
		return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
	}
}