package jtetravex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 * User: Domovoy
 * Date: 16.02.2007
 * Time: 17:22:53
 */

public class Tetra extends JComponent implements MouseListener, MouseMotionListener {
	private final Color colors[] = {Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.WHITE,
	                                Color.PINK, Color.RED, Color.YELLOW, Color.BLACK, Color.GRAY};
	public final int left;
	public final int up;
	public final int right;
	public final int down;
	private Point delta;
	private String tetraCenter;

	public Tetra(Nums nums) {
		this(nums.left, nums.up, nums.right, nums.down);
	}

	public Tetra(int left, int up, int right, int down) {
		super();
		this.left = left;
		this.up = up;
		this.right = right;
		this.down = down;
		addMouseListener(this);
		addMouseMotionListener(this);
		setMinimumSize(new Dimension(50, 50));
		setMaximumSize(new Dimension(200, 200));
		setPreferredSize(new Dimension(60, 60));
	}

	public void paint(Graphics g) {
		int p1 = getWidth() / 4;
		int p2 = getWidth() / 2;
		int p3 = (int) (getWidth() * 0.75);

		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (Config.view == Config.NUMS) {
			g.setColor(Color.BLACK);
			g.setFont(new Font(null, Font.BOLD, getWidth() / 4));
			FontMetrics fm = g.getFontMetrics();
			int w = fm.charWidth('0') / 2;
			int h = fm.getAscent() / 2;
			int p4 = getWidth() / 5;
			int p5 = (int) (getWidth() * 0.8);
			int x1 = p4 - w;
			int x2 = p2 - w;
			int x3 = p5 - w;
			int y1 = p2 + h;
			int y2 = p4 + h;
			int y3 = p5 + h;
			g.drawString(String.valueOf(left), x1, y1);
			g.drawString(String.valueOf(up), x2, y2);
			g.drawString(String.valueOf(right), x3, y1);
			g.drawString(String.valueOf(down), x2, y3);
		} else if (Config.view == Config.COLORS_RECTANGLE) {
			int x1[] = {0, p2, 0};
			int x2[] = {0, p2, getWidth()};
			int x3[] = {getWidth(), p2, getWidth()};
			int y1[] = {0, p2, getHeight()};
			int y2[] = {0, p2, 0};
			int y3[] = {getHeight(), p2, getHeight()};

			g.setColor(colors[left]);
			g.fillPolygon(x1, y1, 3);

			g.setColor(colors[up]);
			g.fillPolygon(x2, y2, 3);

			g.setColor(colors[right]);
			g.fillPolygon(x3, y1, 3);

			g.setColor(colors[down]);
			g.fillPolygon(x2, y3, 3);

		} else if (Config.view == Config.COLORS_CIRCLE) {
			g.setColor(colors[left]);
			g.fillArc(-p1, p1, p2, p2, 270, 180);

			g.setColor(colors[up]);
			g.fillArc(p1, -p1, p2, p2, 180, 180);

			g.setColor(colors[right]);
			g.fillArc(p3, p1, p2, p2, 90, 180);

			g.setColor(colors[down]);
			g.fillArc(p1, p3, p2, p2, 0, 180);
		}

		g.setColor(Color.GRAY);
		g.drawRect(1, 1, getWidth() - 3, getWidth() - 3);
		g.drawRect(2, 2, getWidth() - 5, getWidth() - 5);
		g.drawRect(3, 3, getWidth() - 7, getWidth() - 7);
		g.drawLine(4, 4, getWidth(), getWidth());
		g.drawLine(4, 5, getWidth() - 1, getWidth());
		g.drawLine(4, 6, getWidth() - 2, getWidth());
		g.drawLine(-1, getWidth(), getWidth() - 5, 4);
		g.drawLine(-2, getWidth(), getWidth() - 6, 4);
		g.drawLine(-3, getWidth(), getWidth() - 7, 4);

		g.setColor(Color.WHITE);
		g.drawLine(1, 1, getWidth() - 3, 1);
		g.drawLine(1, 2, getWidth() - 4, 2);
		g.drawLine(1, 3, getWidth() - 5, 3);
		g.drawLine(1, 1, 1, getWidth() - 3);
		g.drawLine(2, 1, 2, getWidth() - 4);
		g.drawLine(3, 1, 3, getWidth() - 5);

		g.drawLine(5, 4, getWidth() - 5, getWidth() - 6);
		g.drawLine(6, 4, getWidth() - 5, getWidth() - 7);
		g.drawLine(7, 4, getWidth() - 5, getWidth() - 8);
		g.drawLine(5, getWidth() - 5, getWidth() - 5, 5);
		g.drawLine(6, getWidth() - 5, getWidth() - 5, 6);
		g.drawLine(7, getWidth() - 5, getWidth() - 5, 7);

		g.setColor(Color.BLACK);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		super.paint(g);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (Config.lock) {
			return;
		}
		e.translatePoint(getLocation().x, getLocation().y);
		delta = new Point(e.getX() - getLocation().x, e.getY() - getLocation().y);
		tetraCenter = getCoord(e.getX() - delta.x + getWidth() / 2, e.getY() - delta.y + getHeight() / 2);
		MainFrame.getMainFrame().setTopTetra(this);
	}

	public void mouseReleased(MouseEvent e) {
		if (Config.lock) {
			return;
		}
		e.translatePoint(getLocation().x - delta.x + getWidth() / 2, getLocation().y - delta.y + getHeight() / 2);
		String coord = getCoord(e.getX(), e.getY());
		MainFrame.getMainFrame().clearGlassPane();
		if (coord != null && MainFrame.getMainFrame().getTetraPanel().getTetra(coord) == null &&
		    MainFrame.getMainFrame().getTetraPanel().isConnect(this, coord)) {
			MainFrame.getMainFrame().getTetraPanel().remove(this);
			MainFrame.getMainFrame().getTetraPanel().add(this, coord);
		} else {
			MainFrame.getMainFrame().getTetraPanel().add(this, tetraCenter);
		}
		MainFrame.getMainFrame().clearGlassPane();
		MainFrame.getMainFrame().validate();
		MainFrame.getMainFrame().repaint();
		MainFrame.getMainFrame().getTetraPanel().checkWin();
	}

	private String getCoord(int x, int y) {
		String coord = "";
		Point point[] = MainFrame.getMainFrame().getTetraPanel().getPoints();
		int s = getHeight() * Config.side;

		for (int i = 0; i < 2; i++) {
			if ((x > point[i].x && x < point[i].x + s) && (y > point[i].y && y < point[i].y + s)) {
				coord += i;
				int relX = x - point[i].x;
				int relY = y - point[i].y;
				coord += relX / getWidth();
				coord += relY / getHeight();
				return coord;
			}
		}
		return null;
	}

	public int[] getNums() {
		return new int[]{left, up, right, down};
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		if (Config.lock) {
			return;
		}
		e.translatePoint(getLocation().x, getLocation().y);
		setLocation(e.getX() - delta.x, e.getY() - delta.y);
	}

	public void mouseMoved(MouseEvent e) {
	}

	public String toString() {
		return this.getClass().getName() + ": left=" + left + ", up=" + up + ", right=" + right + ", down=" + down;
	}
}
