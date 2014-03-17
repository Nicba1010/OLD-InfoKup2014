package base.client;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import base.UIComponents.ImageBig;
import base.util.ImageUtils;

public class Server implements Runnable {
	ImageUtils imgUtils = new ImageUtils();
	JPanel panel;
	String clientName;
	JLabel label;
	int width;
	String ftpServerIP;
	String ftpServerUsername;
	String ftpServerPassword;
	BufferedImage fullImage;
	public Server(JPanel panel, final String clientName, JLabel label, int width,
			String ftpServerIP, String ftpServerUsername,
			String ftpServerPassword) {
		this.panel = panel;
		this.clientName = clientName;
		this.label = label;
		this.width = width;
		this.ftpServerIP = ftpServerIP;
		this.ftpServerUsername = ftpServerUsername;
		this.ftpServerPassword = ftpServerPassword;
		label.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				new ImageBig(clientName,fullImage);
			}
		});
	}

	@Override
	public void run() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				System.out.println("Loading Image");
				try {
					loadImage();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Loaded Image");
			}
		}, 0, 4000);
	}

	public void loadImage() throws IOException {
		String filePath = "/" + clientName + ".png";
		String savePath = System.getProperty("java.io.tmpdir") + "screenshot"
				+ clientName + ".png";

		String ftpUrl = "ftp://" + ftpServerUsername + ":" + ftpServerPassword
				+ "@" + ftpServerIP + "/" + filePath + ";type=i";
		System.out.println("URL: " + ftpUrl);

		try {
			URL url = new URL(ftpUrl);
			URLConnection conn = url.openConnection();
			InputStream inputStream = conn.getInputStream();

			FileOutputStream outputStream = new FileOutputStream(savePath);

			byte[] buffer = new byte[4096];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.close();
			inputStream.close();

			System.out.println("File downloaded");
		} catch (IOException ex) {
		}
		BufferedImage imgtemp = ImageIO.read(new File(System
				.getProperty("java.io.tmpdir")
				+ "screenshot"
				+ clientName
				+ ".png"));
		BufferedImage imgFinal = null;
		if (imgtemp != null) {
			int height = ((int) (((float) imgtemp.getHeight() / (float) imgtemp
					.getWidth()) * (float) width));
			imgFinal = imgUtils.getScaledImage(imgtemp, width, height);
			fullImage = imgtemp;
			label.setIcon(new ImageIcon(imgFinal));
			label.repaint();
			label.revalidate();
			panel.repaint();
			panel.revalidate();
		} else {
		}
	}

}
