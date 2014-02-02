package button;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import base.plugins.BasePlugin;

public class Plugin implements BasePlugin {

	public Plugin() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void runServer() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addJComponentsToServer(JPanel panel, int width, String clientName) {
		JPanel modPanel = new JPanel();

		JButton exit = new JButton("Exit");
		modPanel.setLayout(new BoxLayout(modPanel, BoxLayout.X_AXIS));
		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						System.exit(0);
					}
				});
			}
		});
		modPanel.add(exit);
		panel.add(modPanel);
		panel.revalidate();
		panel.repaint();

	}

	@Override
	public void runClient() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkInputFromClient(String input) {
		// TODO Auto-generated method stub
		
	}

}
