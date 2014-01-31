package image;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import base.plugins.BasePlugin;

public class Plugin implements BasePlugin {

	public Plugin() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addJComponents(JPanel panel, int width) {
	
		JPanel pan = new JPanel();
		pan.setLayout(new BorderLayout());
		
		BufferedImage splash = null;
		BufferedImage splash1 = null;
		try {
			splash = ImageIO
					.read(new URL(
							"http://baranja.hr/demo/wp-content/uploads/2012/12/Caffe-bar-papagaj.jpg"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int height = ((int) (((float) splash.getHeight() / (float) splash
				.getWidth()) * (float) width));
		splash1=getScaledImage(splash, width, height);
		JLabel label1 = new JLabel(new ImageIcon(splash1));
		System.out.println(splash1.getWidth());
		System.out.println(splash1.getWidth() + ":" + splash1.getHeight());
		label1.setPreferredSize(new Dimension(width, height));
		label1.setMaximumSize(label1.getPreferredSize());
		label1.setMinimumSize(label1.getPreferredSize());
		pan.add(label1);
		panel.add(pan);
	}

	private BufferedImage getScaledImage(BufferedImage src, int w, int h) {
		int finalw = w;
		int finalh = h;
		double factor = 1.0d;
		if (src.getWidth() > src.getHeight()) {
			factor = ((double) src.getHeight() / (double) src.getWidth());
			finalh = (int) (finalw * factor);
		} else {
			factor = ((double) src.getWidth() / (double) src.getHeight());
			finalw = (int) (finalh * factor);
		}

		BufferedImage resizedImg = new BufferedImage(finalw, finalh,
				BufferedImage.TRANSLUCENT);
		Graphics2D g2 = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(src, 0, 0, finalw, finalh, null);
		g2.dispose();
		return resizedImg;
	}
}
