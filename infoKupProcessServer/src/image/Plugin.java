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
	public void runServer() {
		
	}

	@Override
	public void addJComponentsToServer(JPanel panel, int width, String clientName) {
//		try {
//			Thread.sleep(6000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		JPanel pan = new JPanel();
		pan.setLayout(new BorderLayout());
		
		BufferedImage imgtemp = null;
		BufferedImage imgFinal = null;
		try {
			imgtemp = ImageIO
					.read(new URL(
							"http://nicba1010.byethost16.com/"+clientName+".png"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int height = ((int) (((float) imgtemp.getHeight() / (float) imgtemp
				.getWidth()) * (float) width));
		imgFinal=imgUtils.getScaledImage(imgtemp, width, height);
		JLabel label1 = new JLabel();
		label1.setIcon(new ImageIcon(imgFinal));
		System.out.println(imgFinal.getWidth());
		System.out.println(imgFinal.getWidth() + ":" + imgFinal.getHeight());
		label1.setPreferredSize(new Dimension(width, height));
		label1.setMaximumSize(label1.getPreferredSize());
		label1.setMinimumSize(label1.getPreferredSize());
		pan.add(label1);
		panel.add(pan);
		new Thread(new Server(pan,clientName,label1, width)).start();
	}

	@Override
	public void runClient() {
		new Thread(new Client()).start();
	}

	@Override
	public void checkInputFromClient(String input) {
		// TODO Auto-generated method stub
		
	}
}
