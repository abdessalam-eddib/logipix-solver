import java.util.LinkedList;

public class Cell {

	int value = 0; // cell value
	int x, y; // cell coordinates
	LinkedList<BrokenLine> listBrokenLines; // list of all possible broking lines starting at cell
	int currentBrokenLine = -1; // the index of the current broken line
	boolean occupied = false; // state of the cell
	Cell connectedCell; // the cell to which it's connected

	/*
	 * constructor of the cell
	 */

	public Cell(int v, int x, int y) {
		this.x = x;
		this.y = y;
		value = v;
		if (value > 0) {
			listBrokenLines = new LinkedList<BrokenLine>();
			occupied = true;
		}
	}

	/*
	 * the following function allows us to connect the cell to another cell "c"
	 */

	public void connectToCell(Cell c) {
		this.connectedCell = c;
		c.connectedCell = this;
	}

	/*
	 * the following function allows us to disconnect the cell from another one "c"
	 */

	public void disconnectFromCell(Cell c) {
		this.connectedCell = null;
		c.connectedCell = null;
	}

	/*
	 * the following two functions allow us to calculate the distance of the cell to
	 * another one
	 */

	public int distance(Cell c) {
		return Math.abs(x - c.x) + Math.abs(y - c.y);
	}

	public int distance(int x0, int y0) {
		return Math.abs(x - x0) + Math.abs(y - y0);
	}
}
