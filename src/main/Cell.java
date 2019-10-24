package main;
import java.util.*;

public class Cell {
	private int x;
	private int y;
	private double value;
	private boolean isBlocked = false;
	private boolean hasBeenVisited = false;
	private double occupancyProbability = -1; // -1: unknown, 0: empty, 1: occupied
	private ArrayList<Cell> neighbours = new ArrayList<Cell>();
	
	private int f = 0; // cost estimate
	private int g = 0; // cost of the path from the start node to the destination
	private int h = 0; // heuristic that estimates the cost of the cheapest path from the start node to the destination
	private Cell previousCell;
	private int noOfSensorReadings = 0;
	
	public Cell (int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public final int countUnknownNeighbours() {
		int count = 0;
		for (Cell neighbour : neighbours) {
			if (neighbour.getOccupancyProbability() == -1) count++;
		}
		return count;
	}
	
	public final String toString() {
		return x + "," + y;
	}
	
	public final int getX() {
		return x;
	}
	
	public final int getY() {
		return y;
	}
	
	public final double getValue() {
		return value;
	}
	
	public final boolean hasBeenVisited() {
		return hasBeenVisited;
	}
	
	public final double getOccupancyProbability() {
		return occupancyProbability;
	}
	
	public final ArrayList<Cell> getNeighbours() {
		return neighbours;
	}
	
	public final int getF() {
		return f;
	}
	
	public final int getG() {
		return g;
	}
	
	public final int getH() {
		return h;
	}
	
	public final Cell getPreviousCell() {
		return previousCell;
	}
	
	public final void setValue(int x, int y) {
		double distance = Math.sqrt((y - this.y) * (y - this.y) + (x - this.x) * (x - this.x));
		value = 4 - countUnknownNeighbours() + distance;
	}
	
	public final void visit() {
		hasBeenVisited = true;
	}
	
	public final void setOccupancyProbability(double occupancyProbability) {
		this.occupancyProbability = occupancyProbability;
	}
	
	public final void setNeighbours(ArrayList<Cell> neighbours) {
		this.neighbours = neighbours;
	}
	
	public final void setF(int f) {
		this.f = f;
	}
	
	public final void setG(int g) {
		this.g = g;
	}
	
	public final void setH(int h) {
		this.h = h;
	}
	
	public final void setPreviousCell(Cell previousCell) {
		this.previousCell = previousCell;
	}
	
	public final int getNoOfSensorReadings() {
		return noOfSensorReadings;
	}
	
	public final void addSensorReading( ) {
		 noOfSensorReadings++;
	}
	
	public final boolean isBlocked() {
		return isBlocked;
	}
	
	public final void setIsBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
		setOccupancyProbability(-2);
	}
}