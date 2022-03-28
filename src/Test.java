
public class Test {

	public static void main(String[] args) {
		Logipix logipix = new Logipix();
		// precise the logipix here
		logipix.loadFromTxt("Man.txt");
		DisplayLogipix dl = new DisplayLogipix(logipix);
		dl.display();

		logipix.dl = dl;
		long start = System.currentTimeMillis();
		try {
			// precise the algorithm to use here
			System.out.println("result= " + String.valueOf(logipix.solveBacktrackingOrdered()));
		} catch (Exception e) {
			System.out.println("Code: " + e.getMessage());
		}
		long end = System.currentTimeMillis();
		System.out.println("execution time= " + String.valueOf(end - start));

		drawAllLines(logipix, dl);

	}

	static void drawAllLines(Logipix l, DisplayLogipix dl) {
		for (int intToConsiderIndex : l.intToConsider) {
			for (Cell cell : l.mapCellValues.get(intToConsiderIndex)) {
				if (cell.listBrokenLines.size() > 0) {
					// choose one of the following line and comment the other to show or hide
					// brokenlines
					// dl.drawBrokenLine(cell.listBrokenLines.get(cell.currentBrokenLine), true);
					dl.drawBrokenLine(cell.listBrokenLines.get(cell.currentBrokenLine));
				}
			}
		}
	}

	static void testRecu(int index, boolean[] done) {
		if (done[0])
			return;

		if (index >= 5) {
			done[0] = true;
			return;
		}

		System.out.println(index);
		testRecu(index + 1, done);
		if (!done[0])
			System.out.println(index);
	}

}
