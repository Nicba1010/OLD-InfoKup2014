package image;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import base.util.ImageUtils;

public class Server implements Runnable {
	ImageUtils imgUtils = new ImageUtils();
	JPanel panel;
	String clientName;
	JLabel label;
	int width;

	public Server(JPanel panel, String clientName, JLabel label, int width) {
		this.panel = panel;
		this.clientName = clientName;
		this.label = label;
		this.width = width;
	}

	@Override
	public void run() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				System.out.println("Loading Image");
				loadImage();
				System.out.println("Loaded Image");
			}
		}, 0, 10000);
	}

	public void loadImage() {

		BufferedImage imgtemp = null;
		BufferedImage imgFinal = null;
		try {
			imgtemp = ImageIO.read(new URL("http://nicba1010.byethost16.com/"
					+ clientName + ".png"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int height = ((int) (((float) imgtemp.getHeight() / (float) imgtemp
				.getWidth()) * (float) width));
		imgFinal = imgUtils.getScaledImage(imgtemp, width, height);
		label.setIcon(new ImageIcon(imgFinal));

//	    label.setIcon(null);
		label.repaint();
		label.revalidate();
		panel.repaint();
		panel.revalidate();
	}

}
