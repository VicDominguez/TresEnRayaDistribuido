import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("serial")

/**
 * @since 12/11/2019
 * @version 1.0
 * Versión local en la que un jugador juega contra la computadora facilitada en la asignatura
 */

public class TicTacToeSD extends JPanel implements MouseListener {

	/**
	 * Who goes first in the next game?
	 */
	boolean first = true;

	// The computer is white.
	// This variable stores all moves of the white gamer
	int white;

	// The user is black.
	// This variable stores all moves of the black gamer

	int black;

	// The squares in order of importance...
	final int moves[] = { 4, 0, 2, 6, 8, 1, 3, 5, 7 };

	// won stores all the winning positions
	// won is an boolean array of 2^9 elements = 512 booleans
	boolean won[] = new boolean[1 << 9];
	final int DONE = (1 << 9) - 1; // 511
	final int ONGOING = 0;
	final int WIN = 1;
	final int LOSE = 2;
	final int STALEMATE = 3;

	BufferedImage blackTile;
	BufferedImage whiteTile;

	TicTacToeSD() {
		// Initialize all winning positions in the won array
		this.setWinningPositions();
		// Load the gamer image tiles
		try {
			blackTile = ImageIO.read(new File("resources/tictactoe/circle2.gif"));
			whiteTile = ImageIO.read(new File("resources/tictactoe/cross2.gif"));

		} catch (IOException e) {
			e.printStackTrace();
		}

		// When you extends JPanel your default layout is the FlowLayout.
		// We prefer the BorderLayout.
		this.setLayout(new BorderLayout());

		// Set the listener to get the player mouse movements
		addMouseListener(this);
		this.setVisible(true);
	}

	public Dimension getPreferredSize() {
		// Set the JFrame layout with a preferred JPanel pixel size
		return new Dimension(250, 200);
	}

	void setWinningPositions() {

		// Horizontal lines
		this.isWon((1 << 0) | (1 << 1) | (1 << 2));
		this.isWon((1 << 3) | (1 << 4) | (1 << 5));
		this.isWon((1 << 6) | (1 << 7) | (1 << 8));
		// vertical lines
		this.isWon((1 << 0) | (1 << 3) | (1 << 6));
		this.isWon((1 << 1) | (1 << 4) | (1 << 7));
		this.isWon((1 << 2) | (1 << 5) | (1 << 8));
		// diagonal lines
		this.isWon((1 << 0) | (1 << 4) | (1 << 8));
		this.isWon((1 << 2) | (1 << 4) | (1 << 6));
	}

	/**
	 * Mark all positions with these bits set as winning.
	 */
	void isWon(int pos) {
		for (int i = 0; i < DONE; i++) {
			if ((i & pos) == pos) {
				won[i] = true;
			}
		}
	}

	/**
	 * Figure what the status of the game is.
	 */
	int status() {
		if (won[white]) { // computer
			System.out.println("status: computer wins");

			return WIN;
		}
		if (won[black]) { // user
			System.out.println("status: user wins");

			return LOSE;
		}
		if ((black | white) == DONE) {
			System.out.println("status:stalemate");

			return STALEMATE;
		}
		return ONGOING;
	}

	/**
	 * User move.
	 * 
	 * @return true if legal
	 */
	boolean usrMove(int m) {
		if ((m < 0) || (m > 8)) {
			return false;
		}
		if (((black | white) & (1 << m)) != 0) {
			return false;
		}
		black |= 1 << m;
		return true;
	}

	/**
	 * Computer move.
	 * 
	 * @return true if legal
	 */
	boolean computerMove() {
		// The bitwise | operator performs a bitwise inclusive OR operation.
		if ((black | white) == DONE) {
			System.out.println("computerMove  done");

			return false;
		}
		int best = bestMove(white, black);
		white |= 1 << best;
		return true;
	}

	/**
	 * Compute the best move for white. (The computer).
	 * 
	 * @return the square to take
	 */
	int bestMove(int white, int black) {
		int bestmove = -1;

		loop: for (int i = 0; i < 9; i++) {
			int mw = moves[i];
			if (((white & (1 << mw)) == 0) && ((black & (1 << mw)) == 0)) {
				int pw = white | (1 << mw);
				if (won[pw]) {
					// white wins, take it!
					return mw;
				}
				for (int mb = 0; mb < 9; mb++) {
					if (((pw & (1 << mb)) == 0) && ((black & (1 << mb)) == 0)) {
						int pb = black | (1 << mb);
						if (won[pb]) {
							// black wins, take another
							continue loop;
						}
					}
				}
				// Neither white nor black can win in one move, this will do.
				if (bestmove == -1) {
					bestmove = mw;
				}
			}
		}
		if (bestmove != -1) {
			return bestmove;
		}

		// No move is totally satisfactory, try the first one that is open
		for (int i = 0; i < 9; i++) {
			int mw = moves[i];
			if (((white & (1 << mw)) == 0) && ((black & (1 << mw)) == 0)) {
				return mw;
			}
		}

		// No more moves
		return -1;
	}

	/*************************************
	 * User Graphics interface methods
	 ************************************/

	/**
	 * The user has clicked in the table. Figure out where and see if the move is
	 * legal If it is a legal move, respond with a legal move (if possible).
	 */

	public void getCoordenates(int x, int y) {

		// Figure out the row/column
		// Dimension width and height are specified in pixels.
		Dimension d = getSize();

		// column number = the number of times x contains d.height (0, 1, 2)
		// row number = the number of times y contains d.width (0, 1, 2)
		int c = (x * 3) / d.width;
		int r = (y * 3) / d.height;
		System.out.println("x " + x + " y " + y);
		System.out.println("c " + c + " r " + r);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// The invocation of super.paintComponent(g) passes the graphics
		// context off to the component's UI delegate, which paints the
		// panel's background.

		Dimension d = getSize();
		g.setColor(Color.black);
		int xoff = d.width / 3;
		int yoff = d.height / 3;
		g.drawLine(xoff, 0, xoff, d.height);
		g.drawLine(2 * xoff, 0, 2 * xoff, d.height);
		g.drawLine(0, yoff, d.width, yoff);
		g.drawLine(0, 2 * yoff, d.width, 2 * yoff);

		int i = 0;
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 3; c++, i++) {
				// left shift operator "<<" shifts a bit pattern
				if ((white & (1 << i)) != 0) {
					// g.drawImage(blackTile, c * xoff + 1, r * yoff + 1, null);
					g.drawImage(blackTile, c * xoff + 1, r * yoff + 1, xoff, yoff, null);

				} else if ((black & (1 << i)) != 0) {
					// g.drawImage(whiteTile, c * xoff + 1, r * yoff + 1, null);
					g.drawImage(whiteTile, c * xoff + 1, r * yoff + 1, xoff, yoff, null);

				}
			}
		}
	}

	/**
	 * The user has clicked in the board game. Figure out where and see if a legal
	 * move is possible. If it is a legal move, respond with a legal move (if
	 * possible).
	 */
	public void mouseReleased(int x, int y) {
		switch (status()) {
		case WIN:
		case LOSE:
		case STALEMATE:
			white = black = 0;
			if (first) {
				white |= 1 << (int) (Math.random() * 9);
			}
			first = !first;
			repaint();
			return;
		}

		// Figure out the row/column
		Dimension d = getSize();
		int c = (x * 3) / d.width;
		int r = (y * 3) / d.height;

		if (usrMove(c + r * 3)) {
			repaint();

			switch (status()) {
			case WIN:
				System.out.println(" win!!!");
				break;
			case LOSE:
				System.out.println(" lose!!!");
				break;
			case STALEMATE:
				System.out.println(" stalemate !!!");
				break;

			default:
				if (computerMove()) {
					repaint();
					switch (status()) {
					case WIN:
						System.out.println(" win!!!");
						break;
					case LOSE:
						System.out.println(" lose!!!");
						break;
					case STALEMATE:
						System.out.println(" stalemate !!!");
						break;
					default:
					}
				} else {
					System.out.println(" beep !!!");
				}
			}
		} else {
			System.out.println(" beep !!!");
		}

	}

	public void mouseReleased(MouseEvent e) {
		this.mouseReleased(e.getX(), e.getY());
	}

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	private static void createAndShowGUI() {
		// A Frame is a top-level window with a title and a border.
		JFrame frame = new JFrame("TicTacToe-SD");
		// The default layout for a frame is BorderLayout that determines
		// the sizes of components and positions them within a container in up
		// to five areas:
		// top, bottom, left, right, and center

		// A JFrame can contain several containers JPanels.
		// A BorderLayout requires the specification of the area to which the
		// component should be added.
		frame.add(new TicTacToeSD(), BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Display the window frame.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event dispatching thread:
		// creating and showing this application's GUI.

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

}
