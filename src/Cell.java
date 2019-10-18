import java.util.*;

public class Cell {
	private int x;
	private int y;
	private double distance;
	private String status = "unknown";
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
	
	public final void setDistance(int x, int y) {
		distance = Math.sqrt((y - this.y) * (y - this.y) + (x - this.x) * (x - this.x));
	}
	
	public final void setNeighbours(ArrayList<Cell> neighbours) {
		this.neighbours = neighbours;
	}
}
