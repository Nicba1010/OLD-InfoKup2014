package base.UIComponents;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

import base.SchoolarServer;

@SuppressWarnings("serial")
public class IndividualClient extends JFrame {
	JPanel panel;
	int screenWidth = 265, screenHeight = 644;
	String clientName;
	Client client;
	Client indiClient;
	Component component;
	int id;

	public IndividualClient(String clientName, final Client client,
			Component comp, final int id) throws HeadlessException {
		super(clientName);
		this.clientName = clientName;
		this.client = client;
		this.component = comp;
		this.id=id;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		initGUI();
		dispose();
		Timer timer = new Timer();
		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				SchoolarServer.addPC(component, id);
			}

			@Override
			public void windowClosed(WindowEvent arg0) {

			}

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				indiClient.setData(client.getData());
			}
		}, 0, 2000);
	}

	private void initGUI() {
		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		setSize(new Dimension(screenWidth, screenHeight));
		getContentPane().add(panel);
		indiClient = new Client(0, 0, screenWidth - 15, screenHeight - 44,
				panel, clientName);
		indiClient.removeButton(2);
		indiClient.removeComponent(0);
		indiClient.setData(client.getData());
		setVisible(true);
	}
}
