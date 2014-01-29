package infoKupStreamClient;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;



@SuppressWarnings({ "unused", "serial" })


public class Test extends JFrame{
	private BufferedImage splash;
	private JLabel label1;
	
	Test() {
		setLayout(new FlowLayout());
		

		
		try {
			splash = ImageIO.read(new URL("http://baranja.hr/demo/wp-content/uploads/2012/12/Caffe-bar-papagaj.jpg"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		label1 = new JLabel(new ImageIcon(splash));
		add(label1);
		
		
		
	}
	public static void main(String args[]) {
		Test gui = new Test();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setVisible(true);
		gui.pack();
		gui.setTitle("Test!");
	}
	


}
