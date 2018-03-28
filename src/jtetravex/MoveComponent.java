package jtetravex;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Domovoy
 * Date: 22.02.2007
 * Time: 11:50:21
 */

public class MoveComponent extends Thread {
	private int length;
	private Tetra[] tetras;
	private Point[][] points;

	public MoveComponent(Tetra[] tetras, Point[][] points, int length) {
		super("Move component(s)");
		this.tetras = tetras;
		this.points = points;
		this.length = length;
	}

	public void run() {
		long start = System.currentTimeMillis();
		double[] sx;
		double[] sy;
		double[] locX;
		double[] locY;
		Config.isMove = true;
		MainFrame.getMainFrame().getMoveMenu().setEnabled(false);
		int steps = 50;
		Config.lock = true;
		int delay = length / steps;
		sx = new double[tetras.length];
		sy = new double[tetras.length];
		locX = new double[tetras.length];
		locY = new double[tetras.length];

		for (int i = 0; i < tetras.length; i++) {
			sx[i] = (double) (points[i][1].x - points[i][0].x) / steps;
			sy[i] = (double) (points[i][1].y - points[i][0].y) / steps;
			locX[i] = tetras[i].getLocation().x;
			locY[i] = tetras[i].getLocation().y;
		}

		for (int s = 0; s < steps; s++) {
			for (int i = 0; i < tetras.length; i++) {
				locX[i] += sx[i];
				locY[i] += sy[i];
				tetras[i].setLocation((int) (locX[i]), (int) (locY[i]));

			}
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < tetras.length; i++) {
			tetras[i].setLocation(points[i][1]);
		}
		
		Config.lock = false;
		Config.isMove = false;
		if(tetras.length!=Config.side*Config.side){
			MainFrame.getMainFrame().getMoveMenu().setEnabled(true);
		}
		long delta = System.currentTimeMillis()-start;
		MainFrame.getMainFrame().validate();
		MainFrame.getMainFrame().delta += delta;
//		System.out.println("solved for "+delta+" millis");
	}
}
