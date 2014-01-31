package button;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import base.plugins.PluginBase;

public class Plugin implements PluginBase {

	public Plugin() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addJComponents(JPanel panel) {
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

}
