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
import base.plugins.PluginLoader;

@SuppressWarnings("serial")
public class IndividualClient extends JFrame {
	JPanel panel;
	int screenWidth = 265, screenHeight = 644;
	String clientName;
	Client client;
	Client indiClient;
	Component component;
	int id;
	PluginLoader pluginLoader;
	JFrame mainFrame = this;

	public IndividualClient(final String clientName, final Client client,
			Component comp, final int id, PluginLoader pluginLoader)
			throws HeadlessException {
		super(clientName);
		this.clientName = clientName;
		this.client = client;
		this.component = comp;
		this.id = id;
		this.pluginLoader = pluginLoader;
		SchoolarServer.removedClients.add(clientName);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		initGUI();
		dispose();
		Timer timer = new Timer();
		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				SchoolarServer.addPC(component, id);
				SchoolarServer.removedClients.remove(clientName);
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
			}
		});
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				indiClient.setData(client.getData());
			}
		}, 0, 2000);
	}

	public IndividualClient(String clientName, final Client client,
			Component comp, PluginLoader pluginLoader) throws HeadlessException {
		super(clientName);
		this.clientName = clientName;
		this.client = client;
		this.component = comp;
		this.pluginLoader = pluginLoader;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		initGUI();
		dispose();
		Timer timer = new Timer();
		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				SchoolarServer.addPC(component);
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
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
				panel, clientName, pluginLoader);
		indiClient.removeButton(2);
		indiClient.removeComponent(0);
		indiClient.setData(client.getData());
		setVisible(true);
	}
}
