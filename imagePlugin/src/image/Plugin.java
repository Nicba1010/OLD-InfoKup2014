package image;

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
		String host = "127.0.0.1";
		JPanel pan = new JPanel();
		pan.setLayout(new BorderLayout());
		

		String filePath = "/htdocs/"+clientName+".png";
		String savePath = System.getProperty("java.io.tmpdir") + "screenshot"
				+ clientName + ".png";
 
        String pass="12346789";
		String user="infokup";
		String ftpUrl = "ftp://"+user+":"+pass+"@"+host+"/"+filePath+";type=i";
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
            ex.printStackTrace();
        }
		BufferedImage imgtemp = null;
		try {
			imgtemp = ImageIO.read(new File(System.getProperty("java.io.tmpdir")+"screenshot"+clientName+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedImage imgFinal = null;
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
		new Thread(new Server(pan,clientName,label1, width,host)).start();
	}

	@Override
	public void runClient(String[] args) {
		new Thread(new Client("127.0.0.1", "infokup", "12346789")).start();
	}

	@Override
	public void checkInputFromClient(String input) {
		// TODO Auto-generated method stub
		
	}
}
