import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Logipix {

	Cell[][] cellPuzzle; // grid that contains all of logipix puzzle cells
	int n_lines, n_col; // logipix puzzle dimensions
	HashMap<Integer, ArrayList<Cell>> mapCellValues = new HashMap<Integer, ArrayList<Cell>>(); // we group the clues by
																								// their values
	ArrayList<Integer> intToConsider = new ArrayList<Integer>(); // list of integer values in the puzzle

	// this is for the solveBacktrackingOrdered method
	ArrayList<Cell> orderedCells = new ArrayList<Cell>();
	ArrayList<Cell> orderedCellsFinal = new ArrayList<Cell>();
	// this is for debugging
	DisplayLogipix dl;

	/*
	 * the following function allows us to load and initialize a logipix puzzle from
	 * a file text
	 */

	public void loadFromTxt(String path) {
		File file = new File(path);
		Scanner sc;
		try {
			sc = new Scanner(file);
			n_col = Integer.parseInt(sc.nextLine());
			n_lines = Integer.parseInt(sc.nextLine());
			cellPuzzle = new Cell[n_lines][n_col];
			String[] line;
			int i = 0;
			int value; // cell value
			while (i < n_lines) {
				line = sc.nextLine().split(" ");
				for (int j = 0; j < line.length; j++) {
					value = Integer.parseInt(line[j]);
					cellPuzzle[i][j] = new Cell(value, j, i);
					// in case we're dealing with a clue and not an empty cell, we add it
					if (value > 0) {
						orderedCells.add(cellPuzzle[i][j]);
						if (mapCellValues.containsKey(value)) {
							mapCellValues.get(value).add(cellPuzzle[i][j]);
						} else {
							mapCellValues.put(value, new ArrayList<Cell>(List.of(cellPuzzle[i][j])));
							intToConsider.add(value);
						}
					}

				}
				i += 1;
			}
			intToConsider.sort(null);
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error has occured!");
			e.printStackTrace();
		}
	}

	/*
	 * the following function generates the list of all broken lines corresponding
	 * to a given cell
	 */

	public void addBrokenLines(Cell c) {
		if (c.value == 1) {
			c.listBrokenLines.add(new BrokenLine(c, c.value, c));
			return;
		}

		// we look for possible cells to connect with
		ArrayList<Cell> possibleCandidates = new ArrayList<Cell>();

		for (Cell cell : mapCellValues.get(c.value)) {
			if (c.distance(cell) <= c.value - 1 && cell != c && cell.connectedCell == null) {
				possibleCandidates.add(cell);
			}
		}

		int startingIndex;

		/*
		 * for each possible candidates we look for the broken lines that connect them
		 * to the cell
		 */
		for (Cell cell : possibleCandidates) {
			startingIndex = c.listBrokenLines.size();
			int stepsLeft = c.value - 1;
			c.listBrokenLines.add(new BrokenLine(c, c.value, cell));
			Cell currentCell;
			int counter;
			BrokenLine currentBL;
			int size;
			while (stepsLeft > 1) {
				size = c.listBrokenLines.size();
				for (int i = startingIndex; i < size; i++) {
					currentBL = c.listBrokenLines.get(i);
					currentCell = currentBL.line[c.value - 1 - stepsLeft];
					counter = 0;

					/*
					 * We check the right neighbor, If it is well defined, we see whether it's not
					 * an occupied cell and not contained already in the broken line, if so we add
					 * it to the broken line then increment the counter We do the same for the other
					 * neighbors, however we check if the counter is 0, if so that means that we
					 * already added the past neighbor to broken line, so if there is a new one we
					 * should copy the content of the current broken line then add the current
					 * neighbor to it and add this new broken line to the list
					 */
					if (currentCell.x + 1 < n_col && cell.distance(currentCell.x + 1, currentCell.y) <= stepsLeft - 1
							&& (!cellPuzzle[currentCell.y][currentCell.x + 1].occupied
									|| cellPuzzle[currentCell.y][currentCell.x + 1] == cell)
							&& !currentBL.contains(cellPuzzle[currentCell.y][currentCell.x + 1])) {
						currentBL.line[c.value - 1 - stepsLeft + 1] = cellPuzzle[currentCell.y][currentCell.x + 1];
						counter += 1;
					}

					// we check the left neighbor
					if (currentCell.x - 1 >= 0 && cell.distance(currentCell.x - 1, currentCell.y) <= stepsLeft - 1
							&& (!cellPuzzle[currentCell.y][currentCell.x - 1].occupied
									|| cellPuzzle[currentCell.y][currentCell.x - 1] == cell)
							&& !currentBL.contains(cellPuzzle[currentCell.y][currentCell.x - 1])) {
						if (counter > 0) {
							c.listBrokenLines.add(currentBL.copyAndAddCell(cellPuzzle[currentCell.y][currentCell.x - 1],
									c.value - 1 - stepsLeft + 1));
						} else {
							currentBL.line[c.value - 1 - stepsLeft + 1] = cellPuzzle[currentCell.y][currentCell.x - 1];
						}
						counter += 1;
					}

					// we check the top neighbor
					if (currentCell.y + 1 < n_lines && cell.distance(currentCell.x, currentCell.y + 1) <= stepsLeft - 1
							&& (!cellPuzzle[currentCell.y + 1][currentCell.x].occupied
									|| cellPuzzle[currentCell.y + 1][currentCell.x] == cell)
							&& !currentBL.contains(cellPuzzle[currentCell.y + 1][currentCell.x])) {
						if (counter > 0) {
							c.listBrokenLines.add(currentBL.copyAndAddCell(cellPuzzle[currentCell.y + 1][currentCell.x],
									c.value - 1 - stepsLeft + 1));
						} else {
							currentBL.line[c.value - 1 - stepsLeft + 1] = cellPuzzle[currentCell.y + 1][currentCell.x];
						}
						counter += 1;
					}

					// we check the down neighbor
					if (currentCell.y - 1 >= 0 && cell.distance(currentCell.x, currentCell.y - 1) <= stepsLeft - 1
							&& (!cellPuzzle[currentCell.y - 1][currentCell.x].occupied
									|| cellPuzzle[currentCell.y - 1][currentCell.x] == cell)
							&& !currentBL.contains(cellPuzzle[currentCell.y - 1][currentCell.x])) {
						if (counter > 0) {
							c.listBrokenLines.add(currentBL.copyAndAddCell(cellPuzzle[currentCell.y - 1][currentCell.x],
									c.value - 1 - stepsLeft + 1));
						} else {
							currentBL.line[c.value - 1 - stepsLeft + 1] = cellPuzzle[currentCell.y - 1][currentCell.x];
						}
						counter += 1;
					}
					if (counter == 0) {
						c.listBrokenLines.remove(i);
						i--;
						size--;
					}
				}
				stepsLeft += -1;
			}
		}
	}

	/*
	 * the following function is quite similar to the previous one, however the list
	 * of broken lines are sorted in decreasing order of the probability of
	 * likelyhood of being part of the solution : prod(distance to the cell/ number
	 * of steps left
	 */

	public void addBrokenLinesWithProb(Cell c) {
		if (c.value == 1) {
			c.listBrokenLines.add(new BrokenLine(c, c.value, c));
			return;
		}
		// we look for possible cells to connect with
		ArrayList<Cell> possibleCandidates = new ArrayList<Cell>();
		for (Cell cell : mapCellValues.get(c.value)) {
			if (c.distance(cell) <= c.value - 1 && cell != c && cell.connectedCell == null) {
				possibleCandidates.add(cell);
			}
		}

		int startingIndex;
		double distance;
		for (Cell cell : possibleCandidates) {
			startingIndex = c.listBrokenLines.size();
			int stepsLeft = c.value - 1;
			c.listBrokenLines.add(new BrokenLine(c, c.value, cell));
			Cell currentCell;
			int counter;
			BrokenLine currentBL;
			int size;
			while (stepsLeft > 1) {
				size = c.listBrokenLines.size();
				for (int i = startingIndex; i < size; i++) {
					currentBL = c.listBrokenLines.get(i);
					currentCell = currentBL.line[c.value - 1 - stepsLeft];
					counter = 0;

					// we check the right neighbor
					distance = cell.distance(currentCell.x + 1, currentCell.y);
					if (currentCell.x + 1 < n_col && distance <= stepsLeft - 1
							&& (!cellPuzzle[currentCell.y][currentCell.x + 1].occupied
									|| cellPuzzle[currentCell.y][currentCell.x + 1] == cell)
							&& !currentBL.contains(cellPuzzle[currentCell.y][currentCell.x + 1])) {
						currentBL.line[c.value - 1 - stepsLeft + 1] = cellPuzzle[currentCell.y][currentCell.x + 1];
						currentBL.prob = currentBL.prob * distance / (stepsLeft - 1);
						counter += 1;
					}

					// we check the left neighbor
					distance = cell.distance(currentCell.x - 1, currentCell.y);
					if (currentCell.x - 1 >= 0 && distance <= stepsLeft - 1
							&& (!cellPuzzle[currentCell.y][currentCell.x - 1].occupied
									|| cellPuzzle[currentCell.y][currentCell.x - 1] == cell)
							&& !currentBL.contains(cellPuzzle[currentCell.y][currentCell.x - 1])) {
						if (counter > 0) {
							c.listBrokenLines.add(currentBL.copyAndAddCell(cellPuzzle[currentCell.y][currentCell.x - 1],
									c.value - 1 - stepsLeft + 1));
							c.listBrokenLines.get(c.listBrokenLines.size() - 1).prob *= distance / (stepsLeft - 1);
						} else {
							currentBL.line[c.value - 1 - stepsLeft + 1] = cellPuzzle[currentCell.y][currentCell.x - 1];
							currentBL.prob = currentBL.prob * distance / (stepsLeft - 1);
						}
						counter += 1;
					}

					// we check the up neighbor
					distance = cell.distance(currentCell.x, currentCell.y + 1);
					if (currentCell.y + 1 < n_lines && distance <= stepsLeft - 1
							&& (!cellPuzzle[currentCell.y + 1][currentCell.x].occupied
									|| cellPuzzle[currentCell.y + 1][currentCell.x] == cell)
							&& !currentBL.contains(cellPuzzle[currentCell.y + 1][currentCell.x])) {
						if (counter > 0) {
							c.listBrokenLines.add(currentBL.copyAndAddCell(cellPuzzle[currentCell.y + 1][currentCell.x],
									c.value - 1 - stepsLeft + 1));
							c.listBrokenLines.get(c.listBrokenLines.size() - 1).prob *= distance / (stepsLeft - 1);
						} else {
							currentBL.line[c.value - 1 - stepsLeft + 1] = cellPuzzle[currentCell.y + 1][currentCell.x];
							currentBL.prob = currentBL.prob * distance / (stepsLeft - 1);
						}
						counter += 1;
					}

					// we check the down neighbor
					distance = cell.distance(currentCell.x, currentCell.y - 1);
					if (currentCell.y - 1 >= 0 && distance <= stepsLeft - 1
							&& (!cellPuzzle[currentCell.y - 1][currentCell.x].occupied
									|| cellPuzzle[currentCell.y - 1][currentCell.x] == cell)
							&& !currentBL.contains(cellPuzzle[currentCell.y - 1][currentCell.x])) {
						if (counter > 0) {
							c.listBrokenLines.add(currentBL.copyAndAddCell(cellPuzzle[currentCell.y - 1][currentCell.x],
									c.value - 1 - stepsLeft + 1));
							c.listBrokenLines.get(c.listBrokenLines.size() - 1).prob *= distance / (stepsLeft - 1);
						} else {
							currentBL.line[c.value - 1 - stepsLeft + 1] = cellPuzzle[currentCell.y - 1][currentCell.x];
							currentBL.prob = currentBL.prob * distance / (stepsLeft - 1);
						}
						counter += 1;
					}
					if (counter == 0) {
						c.listBrokenLines.remove(i);
						i--;
						size--;
					}
				}
				stepsLeft += -1;
			}

		}
		c.listBrokenLines.sort(Comparator.comparingDouble((BrokenLine bl) -> -bl.prob)); // the '-' to get descending
																							// order
		int debug = 0;
	}

	/*
	 * the following function allows to solve a logipix puzzle using backtracking
	 */

	public boolean solveBacktracking() {
		boolean[] done = new boolean[] { false, false };
		solveBackTrackingRecu(0, 0, done);
		return done[1];
	}

	private void solveBackTrackingRecu(int intToConsiderIndex, int clueIndex, boolean[] done) {
		if (done[0])
			return;

		if (intToConsiderIndex == intToConsider.size()) {
			done[0] = true;
			done[1] = true;
			return;
		}

		Cell c = mapCellValues.get(intToConsider.get(intToConsiderIndex)).get(clueIndex);

		if (c.connectedCell != null) {
			if (clueIndex == mapCellValues.get(intToConsider.get(intToConsiderIndex)).size() - 1) {
				// System.out.println("Finished from " +
				// String.valueOf(intToConsider.get(intToConsiderIndex)));
				solveBackTrackingRecu(intToConsiderIndex + 1, 0, done);
			} else
				solveBackTrackingRecu(intToConsiderIndex, clueIndex + 1, done);
		} else {
			addBrokenLines(c);
			//addBrokenLinesWithProb(c);
			for (int i = 0; i < c.listBrokenLines.size(); i++) {
				for (Cell cell : c.listBrokenLines.get(i).line) {
					cell.occupied = true;
				}
				c.connectToCell(c.listBrokenLines.get(i).last);
				c.currentBrokenLine = i;
				if (clueIndex == mapCellValues.get(intToConsider.get(intToConsiderIndex)).size() - 1) {
					// System.out.println("Finished from " +
					// String.valueOf(intToConsider.get(intToConsiderIndex)));
					solveBackTrackingRecu(intToConsiderIndex + 1, 0, done);
				} else
					solveBackTrackingRecu(intToConsiderIndex, clueIndex + 1, done);

				if (done[0])
					return;

				for (Cell cell : c.listBrokenLines.get(i).line) {
					if (cell.value == 0)
						cell.occupied = false;
				}
				c.disconnectFromCell(c.listBrokenLines.get(i).last);
			}
			c.listBrokenLines.clear();
		}
	}

	/*
	 * the following function uses the exclusion technique before doing the
	 * backtracking in order to reduce the number of clues to consider
	 */

	public boolean solveBacktrackingExclusion() throws Exception {
		Cell c;
		for (int i = 0; i < orderedCells.size(); i++) {
			c = orderedCells.get(i);
			addBrokenLines(c);
			if (c.listBrokenLines.size() == 1) {
				c.currentBrokenLine = 0;
				for (Cell cell : c.listBrokenLines.get(0).line) {
					cell.occupied = true;
				}
				orderedCells.remove(c);
				orderedCells.remove(c.listBrokenLines.get(0).last);
				orderedCellsFinal.add(c);
				orderedCellsFinal.add(c.listBrokenLines.get(0).last);
				i--;
				System.out.println("size= " + String.valueOf(orderedCells.size()));
			} else if (c.listBrokenLines.size() == 0) {
				System.out.println("the logipix doesn't have a solution");
				throw new Exception("the error occured from cell at position line= " + String.valueOf(c.y)
						+ " and column= " + String.valueOf(c.x));
			} else
				c.listBrokenLines.clear();
		}
		boolean[] done = new boolean[] { false, false };
		solveBacktrackingOrderedRec(0, done);
		return done[1];
	}

	/*
	 * the following function uses the classic backtracking however instead of
	 * looping through the clues in ascending order, we loop through them line by
	 * line from left to right (their order in the grid)
	 */

	public boolean solveBacktrackingOrdered() {
		boolean[] done = new boolean[] { false, false };
		solveBacktrackingOrderedRec(0, new boolean[] { false });
		return done[1];
	}

	private void solveBacktrackingOrderedRec(int index, boolean[] done) {

		if (done[0])
			return;
		Cell c = null;

		int j = index;

		while (j < orderedCells.size()) {
			if (orderedCells.get(j).connectedCell == null) {
				c = orderedCells.get(j);
				break;
			}
			j += 1;
		}

		if (c == null) {
			done[0] = true;
			done[1] = true;
			return;
		}
		System.out.println("found cell at: " + String.valueOf(c.y) + ", " + String.valueOf(c.x));
		addBrokenLines(c);
		// addBrokenLinesWithProb(c);

		for (int i = 0; i < c.listBrokenLines.size(); i++) {
			for (Cell cell : c.listBrokenLines.get(i).line) {
				cell.occupied = true;
			}
			c.connectToCell(c.listBrokenLines.get(i).last);
			c.currentBrokenLine = i;

			System.out.println("Finished from " + String.valueOf(c.value));
			solveBacktrackingOrderedRec(j + 1, done);

			if (done[0])
				return;

			for (Cell cell : c.listBrokenLines.get(i).line) {
				if (cell.value == 0)
					cell.occupied = false;
			}
			c.disconnectFromCell(c.listBrokenLines.get(i).last);
		}
		c.listBrokenLines.clear();
	}

	static void drawAllLines(Logipix l, DisplayLogipix dl) {
		for (int intToConsiderIndex : l.intToConsider) {
			for (Cell cell : l.mapCellValues.get(intToConsiderIndex)) {
				if (cell.listBrokenLines.size() > 0)
					dl.drawBrokenLine(cell.listBrokenLines.get(cell.currentBrokenLine));
			}
		}
	}
}
