package base;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;

public class Freeze implements Runnable {
	private boolean freeze = false;

	public Freeze() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		Robot robot = null;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		while (true) {
			if (freeze) {
				robot.mouseMove(width / 2, height / 2);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void toggle() {
		freeze = !freeze;
	}

}
