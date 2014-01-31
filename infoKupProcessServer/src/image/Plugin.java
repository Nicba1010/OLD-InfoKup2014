package image;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import base.plugins.BasePlugin;
import base.util.ImageUtils;

public class Plugin implements BasePlugin {
	ImageUtils imgUtils = new ImageUtils();
	public Plugin() {
		
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
		splash1=imgUtils.getScaledImage(splash, width, height);
		JLabel label1 = new JLabel(new ImageIcon(splash1));
		System.out.println(splash1.getWidth());
		System.out.println(splash1.getWidth() + ":" + splash1.getHeight());
		label1.setPreferredSize(new Dimension(width, height));
		label1.setMaximumSize(label1.getPreferredSize());
		label1.setMinimumSize(label1.getPreferredSize());
		pan.add(label1);
		panel.add(pan);
	}
}
