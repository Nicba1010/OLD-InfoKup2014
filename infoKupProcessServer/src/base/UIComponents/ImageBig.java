package base.UIComponents;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImageBig extends JFrame {

	public ImageBig() throws HeadlessException {
		// TODO Auto-generated constructor stub
	}

	public ImageBig(GraphicsConfiguration arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public ImageBig(String arg0, BufferedImage fullImage) throws HeadlessException {
		super(arg0);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		getContentPane().add(panel);
		setVisible(true);
		JLabel label1 = new JLabel();
		label1.setIcon(new ImageIcon(fullImage));
		panel.add(label1);
	}

	public ImageBig(String arg0, GraphicsConfiguration arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
