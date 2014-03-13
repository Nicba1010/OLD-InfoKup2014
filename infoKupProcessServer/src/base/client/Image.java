package base.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import base.util.ImageUtils;

public class Image {
	ImageUtils imgUtils = new ImageUtils();

	public Image() {

	}

	public void runServer() {

	}

	public void addJComponentsToServer(JPanel panel, int width,
			String clientName, String ftpServerIP, String ftpServerUsername,
			String ftpServerPassword) {
		String host = ftpServerIP;
		JPanel pan = new JPanel();
		pan.setLayout(new BorderLayout());

		String filePath = "/" + clientName + ".png";
		String savePath = System.getProperty("java.io.tmpdir") + "screenshot"
				+ clientName + ".png";

		String pass = ftpServerPassword;
		String user = ftpServerUsername;
		String ftpUrl = "ftp://" + user + ":" + pass + "@" + host + "/"
				+ filePath + ";type=i";
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
		BufferedImage imgtemp = null;
		try {
			imgtemp = ImageIO.read(new File(System
					.getProperty("java.io.tmpdir")
					+ "screenshot"
					+ clientName
					+ ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedImage imgFinal = null;
		JLabel label1 = new JLabel();
		if (imgtemp == null) {
			pan.add(label1);
			panel.add(pan);
			new Thread(new Server(pan, clientName, label1, width, ftpServerIP,
					ftpServerUsername, ftpServerPassword)).start();
		} else {
			int height = ((int) (((float) imgtemp.getHeight() / (float) imgtemp
					.getWidth()) * (float) width));
			imgFinal = imgUtils.getScaledImage(imgtemp, width, height);
			label1.setIcon(new ImageIcon(imgFinal));
			System.out.println(imgFinal.getWidth());
			System.out
					.println(imgFinal.getWidth() + ":" + imgFinal.getHeight());
			label1.setPreferredSize(new Dimension(width, height));
			label1.setMaximumSize(label1.getPreferredSize());
			label1.setMinimumSize(label1.getPreferredSize());
			pan.add(label1);
			panel.add(pan);
			new Thread(new Server(pan, clientName, label1, width, ftpServerIP,
					ftpServerUsername, ftpServerPassword)).start();
		}
	}
}
