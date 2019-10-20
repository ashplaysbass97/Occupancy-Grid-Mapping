package main;
import java.util.*;

public class Cell {
	private int x;
	private int y;
	private int f = 0; // cost estimate
	private int g = 0; // cost of the path from the start node to the destination
	private int h = 0; // heuristic that estimates the cost of the cheapest path from the start node to the destination
	private double distance;
	private String status = "unknown";
	private Cell previousCell;
	private ArrayList<Cell> neighbours = new ArrayList<Cell>();
	
	public Cell (int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public final int countUnknownNeighbours() {
		int count = 0;
		for (Cell neighbour : neighbours) {
			if (neighbour.getStatus() == "unknown") count++;
		}
		return count;
	}
	
	public final void print() {
		System.out.println(x + ", " + y);
	}
	
	public final void printNeighbours() {
		System.out.println("Cell (" + x + ", " + y + ") has " + neighbours.size() + " neighbour(s):");
		for (Cell neighbour : neighbours) {
			neighbour.print();
		}
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
	
	public final int getX() {
		return x;
	}
	
	public final int getY() {
		return y;
	}
	
	public final double getDistance() {
		return distance;
	}
	
	public final String getStatus() {
		return status;
	}
	
	public final Cell getPreviousCell() {
		return previousCell;
	}
	
	public final ArrayList<Cell> getNeighbours() {
		return neighbours;
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
	
	public final void setDistance(int x, int y) {
		distance = Math.sqrt((y - this.y) * (y - this.y) + (x - this.x) * (x - this.x));
	}
	
	public final void setStatus(String status) {
		this.status = status;
	}
	
	public final void setPreviousCell(Cell previousCell) {
		this.previousCell = previousCell;
	}
	
	public final void setNeighbours(ArrayList<Cell> neighbours) {
		this.neighbours = neighbours;
	}
	
}