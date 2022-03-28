import javax.swing.*;
import java.awt.*;

public class DisplayLogipix {

	Logipix puzzle;

	JFrame board;
	JPanel[][] listCell;
	JLayeredPane overlay;

	public DisplayLogipix() {
	}

	public DisplayLogipix(Logipix logipix) {
		this.puzzle = logipix;
		this.board = new JFrame("Logipix Puzzle");
		this.listCell = new JPanel[puzzle.n_lines][puzzle.n_col];
		this.overlay = new JLayeredPane();
		this.overlay.setLayout(new GridLayout(puzzle.n_lines, puzzle.n_col));
	}

	public void display() {
		for (int i = puzzle.n_lines - 1; i >= 0; i--) {
			for (int j = puzzle.n_col - 1; j >= 0; j--) {
				JPanel puzzleBoard = new JPanel();
				JLabel label = new JLabel(String.valueOf(this.puzzle.cellPuzzle[i][j].value), JLabel.CENTER);
				if (this.puzzle.cellPuzzle[i][j].value != 0) {
					label.setForeground(Color.BLUE);
				}
				puzzleBoard.add(label);
				puzzleBoard.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				puzzleBoard.setBackground(Color.WHITE);
				this.listCell[i][j] = puzzleBoard;
				this.overlay.add(puzzleBoard, 0);

			}
		}

		// get the screen size as a java dimension
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int h = screenSize.height * 2 / 3;
		this.board.setPreferredSize(new Dimension(h, h));
		this.board.add(this.overlay);
		this.board.setVisible(true);
		this.board.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.board.pack();
	}

	public void drawBrokenLine(BrokenLine brokenLine, boolean drawBroken) {
		if (puzzle == null) {
			System.out.println("A puzzle is needed to start drawing!");
			return;
		}
		if (this.board == null)
			display();
		this.board.setLocationRelativeTo(null);
		Color lightGreen = new Color(0, 255, 100, 60);
		for (int i = 0; i < brokenLine.line.length; i++) {
			this.listCell[brokenLine.line[i].y][brokenLine.line[i].x].setBackground(lightGreen);
		}

		if (drawBroken) {
			// draw broken line
			Color darkGreen = new Color(0, 30, 0, 120);

			// coordinates

			int[] pointX = new int[brokenLine.line.length];
			int[] pointY = new int[brokenLine.line.length];
			int rect_w = this.listCell[brokenLine.line[0].y][brokenLine.line[0].x].getWidth();
			int rect_h = this.listCell[brokenLine.line[0].y][brokenLine.line[0].x].getHeight();
			for (int i = 0; i < brokenLine.line.length; i++) {
				pointX[i] = this.listCell[brokenLine.line[i].y][brokenLine.line[i].x].getX() + rect_w / 2;
				pointY[i] = this.listCell[brokenLine.line[i].y][brokenLine.line[i].x].getY() + rect_h / 2;
			}

			JComponent polyLine = new JComponent() {
				@Override
				public void paintComponent(Graphics g) {
					// super.paintComponent(g);
					Graphics2D g2 = (Graphics2D) g;
					g2.setStroke(new BasicStroke(10));
					g.setColor(darkGreen);
					g.drawPolyline(pointX, pointY, brokenLine.line.length);
					if (pointX.length == 1)
						g.fillOval(pointX[0] - 7, pointY[0] - 7, 15, 15);
				}
			};
			this.board.add(polyLine, 1);
		}
		this.board.repaint();
		this.board.revalidate();
	}

	public void drawBrokenLine(BrokenLine brokenLine) {
		if (puzzle == null) {
			System.out.println("A puzzle is needed to start drawing!");
			return;
		}
		if (this.board == null)
			display();
		this.board.setLocationRelativeTo(null);
		Color lightGreen = new Color(0, 255, 100, 60);
		for (int i = 0; i < brokenLine.line.length; i++) {
			this.listCell[brokenLine.line[i].y][brokenLine.line[i].x].setBackground(lightGreen);
		}

		this.board.repaint();
		this.board.revalidate();
	}

}