package jtetravex;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by IntelliJ IDEA.
 * User: Domovoy
 * Date: 21.02.2007
 * Time: 1:25:04
 */

public class Timer extends Thread {
	private boolean run;
	SimpleDateFormat sdf;
	long delta;
	Date date;

	public Timer() {
		super("Timer");
		run = true;
		start();
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	public long getDelta() {
		return (delta);
	}

	public void run() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		long start = System.currentTimeMillis();
		delta = 0;
		while (run) {
			sdf = new SimpleDateFormat("HH:mm:ss");
			delta = System.currentTimeMillis() - start;
			date = new Date(delta);
			String time = sdf.format(date);
			MainFrame.getMainFrame().setTime(time);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!run) {
				return;
			}
		}
	}
}