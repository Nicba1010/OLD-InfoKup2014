package base.splash;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import base.SchoolarServer;

public class SplashScreen {
	JFrame splashFrame;

	public SplashScreen(boolean nosplash, final String resource)
			throws Exception {
		if (!nosplash) {
			Runnable splash = new Runnable() {
				public void run() {
					try {
						createAndShowSplashScreen(resource);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			SwingUtilities.invokeAndWait(splash);
			Thread.sleep(500);
			for (float i = 0f; i < 1f; i = i + 0.01f) {
				Thread.sleep(10);
				splashFrame.setOpacity(i);
			}
			Thread.sleep(3000);
			for (float i = 1f; i > 0f; i = i - 0.01f) {
				Thread.sleep(10);
				splashFrame.setOpacity(i);
			}
		}
	}

	public SplashScreen(boolean nosplash, final String resource,
			int fadeLenght, int fadeStep) throws Exception {
		if (!nosplash) {
			Runnable splash = new Runnable() {
				public void run() {
					try {
						createAndShowSplashScreen(resource);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			SwingUtilities.invokeAndWait(splash);
			Thread.sleep(500);
			float numOfSteps = 1f / ((float) fadeStep * 0.01f);
			int lenght = (int) (fadeLenght / numOfSteps);
			for (float i = 0f; i < 1f; i = i + ((float) fadeStep * 0.01f)) {
				Thread.sleep(lenght);
				splashFrame.setOpacity(i);
			}
			Thread.sleep(3000);
			for (float i = 1f; i > 0f; i = i - ((float) fadeStep * 0.01f)) {
				Thread.sleep(lenght);
				splashFrame.setOpacity(i);
			}
		}
	}

	@SuppressWarnings("serial")
	private void createAndShowSplashScreen(String resource) throws Exception {
		Image image = ImageIO.read(getClass().getResource(resource));
		BufferedImage img = (BufferedImage) image;

		splashFrame = new JFrame("Splash");
		splashFrame.setUndecorated(true);
		splashFrame.add(new JLabel(new ImageIcon(image)) {
			{
				setOpaque(false);
			}
		});
		splashFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		splashFrame.setPreferredSize(new Dimension(img.getWidth(), img
				.getHeight()));
		splashFrame.setBounds((int) (java.awt.Toolkit.getDefaultToolkit()
				.getScreenSize().getWidth() / 2 - img.getWidth() / 2),
				(int) (java.awt.Toolkit.getDefaultToolkit().getScreenSize()
						.getHeight() / 2 - img.getHeight() / 2),
				img.getWidth(), img.getHeight());
		RoundRectangle2D r = new RoundRectangle2D.Double(0, 0, img.getWidth(),
				img.getHeight(), 25, 25);
		splashFrame.setShape(r);
		splashFrame.setOpacity(0f);
		splashFrame.setMinimumSize(splashFrame.getPreferredSize());
		splashFrame.setVisible(true);
	}
}
