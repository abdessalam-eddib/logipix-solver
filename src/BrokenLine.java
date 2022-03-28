public class BrokenLine {

	Cell first, last; // first and last element of the broken line
	Cell[] line; // list of cells composing the broken line
	double prob = 1; // probability of likelihood of being part of the solution of the puzzle

	/*
	 * constructors of a broken line
	 */

	public BrokenLine(Cell f, int length) {
		line = new Cell[length];
		first = f;
		line[0] = f;
	}

	public BrokenLine(Cell f, int length, Cell l) {
		line = new Cell[length];
		first = f;
		last = l;
		line[0] = f;
		line[line.length - 1] = l;
	}

	/*
	 * the following function allows us to create a copy of a broken line on which
	 * we change a cell value to the one given
	 */

	public BrokenLine copyAndAddCell(Cell toAdd, int addIndex) {
		BrokenLine bl = new BrokenLine(first, line.length, last);
		bl.prob = prob;
		for (int i = 0; i < line.length; i++) {
			bl.line[i] = line[i];
		}
		bl.line[addIndex] = toAdd;
		return bl;
	}

	/*
	 * the following function allows us to check whether a cell is contained in the
	 * broken line
	 */
	
	public boolean contains(Cell cell) {
		for (Cell c : line) {
			if (cell.equals(c))
				return true;
		}
		return false;
	}

}
