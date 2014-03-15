package base.client;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.math.BigInteger;
import java.net.InetAddress;
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
	private String ftpServerIP;
	private String ftpServerUsername;
	private String ftpServerPassword;
	private boolean ftpOn;
	BigInteger modulus, publicExponent;
	private String osInfo;
	private String javaInfo;
	private String javaPath;
	private String homeDir;
	private String extIp;
	private String[] info;
	private InetAddress inetAddress;
	

	public IndividualClient(final String clientName, final Client client,
			Component comp, final int id, PluginLoader pluginLoader,
			String ftpServerIP, String ftpServerUsername,
			String ftpServerPassword, boolean ftpOn, BigInteger modulus,
			BigInteger exponent, String[] info, InetAddress inetAddress) throws HeadlessException {
		super(clientName);
		this.clientName = clientName;
		this.client = client;
		this.component = comp;
		this.id = id;
		this.pluginLoader = pluginLoader;
		this.ftpServerIP = ftpServerIP;
		this.ftpServerUsername = ftpServerUsername;
		this.ftpServerPassword = ftpServerPassword;
		this.ftpOn = ftpOn;
		this.modulus = modulus;
		this.publicExponent = exponent;
		this.info = info;
		this.osInfo = info[0];
		this.javaInfo = info[1];
		this.javaPath = info[2];
		this.homeDir = info[3];
		this.extIp = info[4];
		this.inetAddress = inetAddress;
		for (int i = 0; i < info.length; i++) {
			System.out.println(info[i]);
		}
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

	private void initGUI() {
		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		setSize(new Dimension(screenWidth, screenHeight));
		getContentPane().add(panel);
		indiClient = new Client(0, 0, screenWidth - 15, screenHeight - 44,
				panel, clientName, pluginLoader, ftpServerIP,
				ftpServerUsername, ftpServerPassword, ftpOn, modulus,
				publicExponent, info, inetAddress);
		indiClient.removeButtonP1(2);
		indiClient.removeComponent(0);
		indiClient.setData(client.getData());
		setVisible(true);

	}

	public void die() {
		dispose();
		
	}
}
