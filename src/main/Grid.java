package main;

import java.util.ArrayList;

public class Grid {
	private ArrayList<Cell> grid = new ArrayList<Cell>();
	private Cell currentCell;
	
	private int GRID_WIDTH = 7;
	private int GRID_HEIGHT = 6;
	
	public Grid() {
		createGrid();
		setNeighbours();
		setCurrentCell(getCell(0, 0));
	}
	
	/**
	 * Creates a grid of a specified height and width.
	 */
	private void createGrid() {
		for (int y = 0; y < GRID_HEIGHT; y++) {
			for (int x = 0; x < GRID_WIDTH; x++) {
				grid.add(new Cell(x, y));
			}
		}
	}

	/**
	 * Populates the neighbours ArrayList for each cell in the grid.
	 */
	private void setNeighbours() {
		for (int y = 0; y < GRID_HEIGHT; y++) {
			for (int x = 0; x < GRID_WIDTH; x++) {
				ArrayList<Cell> neighbours = new ArrayList<Cell>();
				if (x != 0) neighbours.add(getCell(x - 1, y));
				if (x != GRID_WIDTH - 1) neighbours.add(getCell(x + 1, y));
				if (y != GRID_HEIGHT - 1) neighbours.add(getCell(x, y + 1));
				if (y != 0) neighbours.add(getCell(x, y - 1));
				getCell(x, y).setNeighbours(neighbours);
			}
		}
	}

	/**
	 * Finds the corresponding cell for a set of provided coordinates.
	 * @param x the x-coordinate of the cell to find
	 * @param y the y-coordinate of the cell to find
	 * @return the corresponding cell for the coordinates provided
	 */
	public final Cell getCell(int x, int y) {
		for (Cell cell : grid) {
			if (cell.getX() == x && cell.getY() == y) {
				return cell;
			}
		}
		return null;
	}
	
	public final boolean areCellsUnknown() {
		for (Cell cell : grid) {
			if (cell.getOccupancyProbability() == -1 && !cell.isBlocked()) {
				return true;
			}
		}
		return false;
	}
	
	public final ArrayList<Cell> getGrid() {
		return grid;
	}
	
	public final int getGridHeight() {
		return GRID_HEIGHT;
	}
	
	public final int getGridWidth() {
		return GRID_WIDTH;
	}
	
	public final Cell getCurrentCell() {
		return currentCell;
	}
	
	public final void setCurrentCell(Cell currentCell) {
		this.currentCell = currentCell;
		currentCell.setOccupancyProbability(0);
	}
	
	public int noOfOccupiedCells() {
		int i = 0;
		for (Cell cell: grid) {
			if (cell.getOccupancyProbability() >= 0.7 && !cell.isBlocked()) {
				i++;
			}
		}
		return i;
	}
	
	public ArrayList<Cell> getOccupiedCells() {
		ArrayList<Cell> occupiedCells = new ArrayList<Cell>();
		for (Cell cell : grid) {
			if (cell.getOccupancyProbability() >= 0.7) {
				occupiedCells.add(cell);
			}
		}
		return occupiedCells;
	}
}
