package jtetravex;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Domovoy
 * Date: 19.02.2007
 * Time: 21:25:36
 */

public class TetraLayout implements LayoutManager, java.io.Serializable {

	private int size;
	private int prefferedSide = 60;
	public static final int minimumSide = 50;
	private int prefferedWidth;
	private Tetra tetras[][][];
	private Point points[] = new Point[2];

	public TetraLayout() {
		this(3);
	}

	public TetraLayout(int size) {
		this.size = size;
		tetras = new Tetra[2][size][size];
		prefferedWidth = prefferedSide * (size * 2 + 1);
	}

	public void addLayoutComponent(String name, Component comp) {
		int z = Integer.parseInt(name.substring(0, 1));
		int x = Integer.parseInt(name.substring(1, 2));
		int y = Integer.parseInt(name.substring(2, 3));
		if (tetras[z][x][y] != null) {
			removeLayoutComponent(tetras[z][x][y]);
		}
		tetras[z][x][y] = (Tetra)comp;
	}

	public void addLayoutComponent(Component comp, String coord) {
		addLayoutComponent(coord, comp);
	}

	public void removeLayoutComponent(Component comp) {
		for (int z = 0; z < 2; z++) {
			for (int x = 0; x < size; x++) {
				for (int y = 0; y < size; y++) {
					if (comp.equals(tetras[z][x][y])) {
						tetras[z][x][y] = null;
					}
				}
			}
		}
	}

	public void removeLayoutComponent(String coord) {
		int z = Integer.parseInt(coord.substring(0, 1));
		int x = Integer.parseInt(coord.substring(1, 2));
		int y = Integer.parseInt(coord.substring(2, 3));
		tetras[z][x][y] = null;
	}

	public Dimension preferredLayoutSize(Container parent) {
		Dimension dim = new Dimension(0, 0);

		if (parent.getWidth() > prefferedWidth) {
			int maybePrefferedSide = parent.getWidth() / (size * 2 + 1);
			if (parent.getHeight() > maybePrefferedSide * size) {
				prefferedSide = maybePrefferedSide;
				prefferedWidth = prefferedSide * (size * 2 + 1);
			}
		}

		//Always add the container's insets!
		Insets insets = parent.getInsets();
		dim.width = prefferedSide * (size * 2 + 1) + insets.right;
		dim.height = prefferedSide * size + insets.top + insets.bottom;

		return dim;
	}

	/* Required by LayoutManager. */
	public Dimension minimumLayoutSize(Container parent) {
		Dimension dim = new Dimension(0, 0);

		//Always add the container's insets!
		Insets insets = parent.getInsets();
		dim.width = (int) (minimumSide * (size * 2 + 1.5)) + insets.right;
		dim.height = minimumSide * size + insets.top + insets.bottom;

		return dim;
	}

	public void layoutContainer(Container parent) {
		int maybePrefferedWidth = (int) (parent.getWidth() / (size * 2 + 1.5));
		int maybePrefferedHeight = parent.getHeight() / size;
		if (parent.getHeight() > maybePrefferedHeight * size) {
			prefferedSide = maybePrefferedHeight;
		}
		if (parent.getWidth() > maybePrefferedWidth * size && maybePrefferedWidth * size < parent.getHeight()) {
			prefferedSide = maybePrefferedWidth;
		}

		int s = prefferedSide;
		int hs = prefferedSide / 2;
		int yoffs = (parent.getHeight() - (prefferedSide * size)) / 2;
		int xoffs = (int) (parent.getWidth() - prefferedSide * (size * 2 + 1.5)) / 2;
		int x, y;

		for (int cz = 0; cz < 2; cz++) {
			for (int cx = 0; cx < size; cx++) {
				for (int cy = 0; cy < size; cy++) {
					Component c = tetras[cz][cx][cy];
					if (cz == 0) {
						x = cx * s + hs + xoffs;
					} else {
						x = s + s * size + cx * s + xoffs;
					}
					y = cy * s + yoffs;

					if (cx == 0 && cy == 0) {
						points[cz] = new Point(x, y);
					}

					if (c != null) {
						c.setBounds(x, y, s, s);
					}
				}
			}
		}
	}

	public Point[] getPoints() {
		return points;
	}

	public int getSideSize(){
		return prefferedSide;
	}

	public Tetra getTetra(String coord) {
		int z = Integer.parseInt(coord.substring(0, 1));
		int x = Integer.parseInt(coord.substring(1, 2));
		int y = Integer.parseInt(coord.substring(2, 3));
		return tetras[z][x][y];
	}

	public Tetra[][][] getModel() {
		return tetras;
	}

	public Tetra[][][] setModel() {
		return tetras;
	}

	public String toString() {
		String str = "";
		return getClass().getName() + "[size=" + size + str + "]";
	}
}
