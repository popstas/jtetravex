package jtetravex;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Domovoy
 * Date: 21.02.2007
 * Time: 16:49:51
 */

public class TetraPanel extends JPanel {//todo learn inspections
	private TetraLayout layout;
	private int side;

	public TetraPanel() {
		super();
		side = Config.side;
		layout = new TetraLayout(side);
		setLayout(layout);

	}

	public void checkWin() {
		Tetra[][] model = layout.getModel()[0];
		for (int x = 0; x < side; x++) {
			for (int y = 0; y < side; y++) {
				if (model[x][y] == null) {
					return;
				}
			}
		}
		MainFrame.getMainFrame().win();
	}

	public Tetra getTetra(String coord) {
		return layout.getTetra(coord);
	}

	public int[] findTetra(int[] nums) {
		Component model[][][] = layout.getModel();
		Tetra tempTetra;
		for (int cz = 0; cz < 2; cz++) {
			for (int cx = 0; cx < side; cx++) {
				for (int cy = 0; cy < side; cy++) {
					if (model[cz][cx][cy] != null) {
						tempTetra = (Tetra) model[cz][cx][cy];
						boolean equal = true;
						for (int i = 0; i < 4; i++) {
							if (tempTetra.getNums()[i] != nums[i]) {
								equal = false;
							}
						}
						if (equal) {
							return new int[]{cz, cx, cy};
						}
					}
				}
			}
		}
		return null;
	}

	public void cheat() {
		MainFrame.getMainFrame().setCheater(true);
		//take random coord from right array
		Random r = new Random();
		int x = r.nextInt(side);
		int y = r.nextInt(side);
		//find tetra for this coord
		Nums rightModel[][] = MainFrame.getMainFrame().getRightModel();
		int nums[] = {rightModel[x][y].left, rightModel[x][y].up,
		              rightModel[x][y].right, rightModel[x][y].down};
		int[] forMove = findTetra(nums);

		//if tetra 0,x,y is not its place - continue
		if ((""+forMove[0]+forMove[1]+forMove[2]).equals("0" + x + y)) {
			cheat();
			return;
		}

		//if this place not free - make it free!
		if (getTetra("0" + x + y) != null) {
			int empty[] = findEmpty();
			moveTetra(new int[]{0,x,y}, empty);
		}

		moveTetra(forMove, new int[]{0, x, y});
		checkWin();
	}

	public void solve(){
		Nums rightModel[][] = MainFrame.getMainFrame().getRightModel();
		Tetra tetras[] = new Tetra[side*side];
		Point points[][] = new Point[side*side][2];
		int index=0;
		for(int x=0; x<side; x++){
			for(int y=0; y<side; y++){
				int nums[] = {rightModel[x][y].left, rightModel[x][y].up,
				              rightModel[x][y].right, rightModel[x][y].down};
				int[] tetraCoord = findTetra(nums);
				tetras[index] = getTetra(""+tetraCoord[0]+tetraCoord[1]+tetraCoord[2]);
				points[index][0] = getPointByCoord(tetraCoord);
				points[index][1] = getPointByCoord(new int[]{0,x,y});
				remove(tetras[index]);//todo еслли на клетке мешалась тетра, то ее удаляли в никуда
				add(tetras[index], "0"+x+y);//todo синхронизировать потоки
				index++;
			}
		}
		Thread moveThread = new MoveComponent(tetras, points, Config.animLength);
		moveThread.start();
		MainFrame.getMainFrame().win();	
	}

	public void moveTetra(int[] from, int[] to){
		Tetra tetra = getTetra(""+from[0]+from[1]+from[2]);
		Point fromPoint = getPointByCoord(from);
		Point toPoint = getPointByCoord(to);
		remove(tetra);
		add(tetra, ""+to[0]+to[1]+to[2]);
		Thread moveThread = new MoveComponent(new Tetra[]{tetra},
		                                      new Point[][]{{fromPoint, toPoint}},
		                                      Config.animLength);
		moveThread.start();
	}

	public Point getPointByCoord(int[] coord) {
		int x = getPoints()[coord[0]].x + layout.getSideSize() * coord[1];
		int y = getPoints()[coord[0]].y + layout.getSideSize() * coord[2];
		return new Point(x, y);
	}

	public int[] findEmpty() {
		Component model[][] = layout.getModel()[1];
		for (int cx = 0; cx < side; cx++) {
			for (int cy = 0; cy < side; cy++) {
				if (model[cx][cy] == null) {
					return new int[]{1, cx, cy};
				}
			}
		}
		return null;
	}

	public Point[] getPoints() {
		return layout.getPoints();
	}

	public boolean isConnect(Tetra tetra, String coord) {
		int z = Integer.parseInt(coord.substring(0, 1));
		if (z == 1) {
			return true;
		}
		int x = Integer.parseInt(coord.substring(1, 2));
		int y = Integer.parseInt(coord.substring(2, 3));
		Component model[][] = layout.getModel()[0];
		Tetra t;
		if (x - 1 >= 0 && model[x - 1][y] != null) {
			t = (Tetra) model[x - 1][y];
			if (t.right != tetra.left) {
				return false;
			}
		}
		if (y - 1 >= 0 && model[x][y - 1] != null) {
			t = (Tetra) model[x][y - 1];
			if (t.down != tetra.up) {
				return false;
			}
		}
		if (x + 1 < side && model[x + 1][y] != null) {
			t = (Tetra) model[x + 1][y];
			if (t.left != tetra.right) {
				return false;
			}
		}
		if (y + 1 < side && model[x][y + 1] != null) {
			t = (Tetra) model[x][y + 1];
			if (t.up != tetra.down) {
				return false;
			}
		}
		return true;
	}

	public void paint(Graphics g) {
//		g.setColor(Color.WHITE);
//		g.fillRect(0,0,getWidth(), getHeight());
		Point points[] = layout.getPoints();
		int sideSize = layout.getSideSize();
		for (int z = 0; z < 2; z++) {
			for (int x = points[z].x; x < points[z].x + sideSize * side; x += sideSize) {
				for (int y = points[z].y; y < points[z].y + sideSize * side; y += sideSize) {
					g.setColor(Color.LIGHT_GRAY);
					g.fillRect(x, y, sideSize, sideSize);

					g.setColor(Color.WHITE);
					g.drawRect(x, y, sideSize - 1, sideSize - 1);
					g.drawRect(x + 1, y + 1, sideSize - 3, sideSize - 3);
					g.drawRect(x + 2, y + 2, sideSize - 5, sideSize - 5);

					g.setColor(Color.GRAY);
					g.drawLine(x, y, x + sideSize - 2, y);
					g.drawLine(x, y + 1, x + sideSize - 3, y + 1);
					g.drawLine(x, y + 2, x + sideSize - 4, y + 2);
					g.drawLine(x, y, x, y + sideSize - 1);
					g.drawLine(x + 1, y, x + 1, y + sideSize - 2);
					g.drawLine(x + 2, y, x + 2, y + sideSize - 3);
				}
			}
		}
		paintComponents(g);
	}

}
