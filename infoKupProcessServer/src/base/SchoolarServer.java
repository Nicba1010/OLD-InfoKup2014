package base;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import base.UIComponents.Client;
import base.plugins.PluginLoader;
import base.splash.SplashScreen;
import base.util.Buffer;

@SuppressWarnings("serial")
public class SchoolarServer extends JFrame {
	static int screenWidth = 1280;
	static int screenHeight = 720;
	JButton quitButton;
	JButton settingsButton;
	JScrollPane infoScrollPane;
	JPanel defaultButtonPanel;
	public static ServerSocket inSocket;
	private static boolean running = false;

	public static ArrayList<Client> processLists = new ArrayList<Client>();

	public JFrame mainFrame = this;
	public static Socket connectionSocket;
	private static String[] TCPData = new String[2];

	public static ArrayList<String> clients = new ArrayList<String>();
	public static ArrayList<String> removedClients = new ArrayList<String>();
	public static Buffer buffer = new Buffer();

	public static JPanel mainPanel, infoScrollPanel;
	public static int socketTCP = 25565;
	public static int cond = 0;
	public static boolean defaultSettings = false, nosplash = false;
	static JScrollPane scrollablePCinfo;
	static JFrame splashFrame;
	static PluginLoader pluginLoader;
	static String ftpServerIP = "127.0.0.1", ftpServerUsername = "infokup",
			ftpServerPassword = "12346789", ftpServerIPRemote = "127.0.0.1";

	public SchoolarServer() {
		try {
			pluginLoader = new PluginLoader(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		initUI();
	}

	public static void color() {
		Color color = new Color(105, 105, 105);
		infoScrollPanel.setBackground(color);
		mainPanel.setBackground(color);
		scrollablePCinfo.setBackground(color);

	}

	public static void settingsPopup() {
		settingsSocket();
		settingsUI();
	}

	public static void settingsSocket() {
		JTextField TCPsocket = new JTextField("" + socketTCP);
		JPanel settingSocketPanel = new JPanel(new GridLayout(0, 1));
		settingSocketPanel.add(new JLabel("Port: "));
		settingSocketPanel.add(TCPsocket);
		int input = JOptionPane.showConfirmDialog(null, settingSocketPanel,
				"Postavke konekcije", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (input == JOptionPane.OK_OPTION) {
			socketTCP = Integer.parseInt(TCPsocket.getText());
		} else {
			System.out.println("Using default port!");
		}
		System.out.println("Socket: " + socketTCP);
	}

	public static void settingsUI() {
		JTextField width = new JTextField("" + screenWidth);
		JTextField height = new JTextField("" + screenHeight);
		JPanel settingUIPanel = new JPanel(new GridLayout(0, 1));
		settingUIPanel.add(new JLabel("Sirina: "));
		settingUIPanel.add(width);
		settingUIPanel.add(new JLabel("Visina: "));
		settingUIPanel.add(height);
		int input = JOptionPane.showConfirmDialog(null, settingUIPanel,
				"Postavke", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (input == JOptionPane.OK_OPTION) {
			screenWidth = Integer.parseInt(width.getText());
			screenHeight = Integer.parseInt(height.getText());

		} else {
			System.out.println("Cancelled!");
		}
		System.out.println("Sirina: " + screenWidth);
		System.out.println("Visina: " + screenHeight);
	}

	public void initUI() {
		setTitle("Schoolar Server");
		mainPanel = new JPanel();
		infoScrollPanel = new JPanel();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(mainPanel);
		mainPanel.setLayout(null);
		infoScrollPanel.setLayout(new BoxLayout(infoScrollPanel,
				BoxLayout.X_AXIS));

		setSize(screenWidth, screenHeight);
		setLocationRelativeTo(null);
		{
			quitButton = new JButton("Ugasi");
			quitButton.setBounds(mainFrame.getWidth() - 80 - 16,
					mainFrame.getHeight() - 30 * 2 - 8, 80, 30);

			quitButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					running = false;
					System.exit(0);
				}
			});
			{
				settingsButton = new JButton("Postavke");
				settingsButton.setBounds(mainFrame.getWidth() - 180 - 16,
						mainFrame.getHeight() - 30 * 2 - 8, 100, 30);
				settingsButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent event1) {
						settingsPopup();
						mainFrame.setSize(screenWidth, screenHeight);
					}
				});

			}
			mainPanel.add(quitButton);
			mainPanel.add(settingsButton);
		}
		this.addWindowStateListener(new WindowStateListener() {

			@Override
			public void windowStateChanged(WindowEvent e) {
				System.out.println(e.getNewState());
				quitButton.setBounds(mainFrame.getWidth() - 80 - 16,
						mainFrame.getHeight() - 30 * 2 - 8, 80, 30);
				settingsButton.setBounds(mainFrame.getWidth() - 180 - 16,
						mainFrame.getHeight() - 30 * 2 - 8, 100, 30);
				scrollablePCinfo.setBounds(0, 0, mainFrame.getWidth() - 15, 620);
			}
		});
		scrollablePCinfo = new JScrollPane(infoScrollPanel);
		scrollablePCinfo.setBounds(0, 0, mainFrame.getWidth() - 15,
				mainFrame.getHeight() - 110);
		scrollablePCinfo
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		mainPanel.add(scrollablePCinfo);
		setVisible(false);
		// color();

	}

	private static void parseArgs(String args[]) {
		if (args.length == 1 && args[0].toString().equalsIgnoreCase("nosplash")) {
			nosplash = true;
		} else {
			System.out.println(args.length);
			for (int i = 0; i < args.length; i++) {
				System.out.println(i + ":" + args[i]);
			}

		}
	}

	public static void main(String args[]) throws Exception {
		parseArgs(args);
		running = true;
		if (!nosplash)
			new SplashScreen("images/splash.png", 500, 2, 750);
		settingsPopup();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SchoolarServer server = new SchoolarServer();
				server.setVisible(true);
			}
		});
		start();

	}

	public static void start() {
		String clientSentence;
		try {
			inSocket = new ServerSocket(socketTCP);
			while (running) {
				connectionSocket = inSocket.accept();
				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(connectionSocket.getInputStream()));

				clientSentence = inFromClient.readLine();
				if (clientSentence != null) {
					TCPData = clientSentence.split(";");
					boolean newClient = false;
					if (clients.contains(TCPData[0])) {

					} else {
						System.out.println(TCPData[0]);
						processLists.add(new Client(0, 0, 250, 600,
								infoScrollPanel, TCPData[0], pluginLoader,
								ftpServerIP, ftpServerUsername,
								ftpServerPassword));
						System.out.println(ftpServerIP + ":"
								+ ftpServerUsername + ":" + ftpServerPassword);
						clients.add(TCPData[0]);
						newClient = true;
					}

					for (Client client : processLists) {
						if (client.getName().equalsIgnoreCase(TCPData[0])) {
							client.setData(TCPData[1].split(":"));
						}
					}
					sendResponse(newClient);
					connectionSocket.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendResponse(boolean newClient) {
		try {
			if (buffer.len() > 0) {
				for (int i = 0; i < buffer.len(); i++) {
					String arg0 = buffer.get(i)[0];
					String arg1 = buffer.get(i)[1];
					String clientName = buffer.get(i)[2];
					if (clientName.equalsIgnoreCase(TCPData[0])) {
						System.out
								.println(arg0 + ":" + arg1 + ":" + clientName);
						System.out.println("OK");
						DataOutputStream outToClient = new DataOutputStream(
								connectionSocket.getOutputStream());
						String msg;
						msg = clientName + " " + arg0 + " " + arg1 + "\n";
						outToClient.writeBytes(msg);
						System.out.println(msg);
						buffer.remove(i);
						break;
					}
				}
			} else {
				DataOutputStream outToClient = new DataOutputStream(
						connectionSocket.getOutputStream());
				if (newClient) {
					outToClient.writeBytes("FTP:" + ftpServerIPRemote + ":"
							+ ftpServerUsername + ":" + ftpServerPassword
							+ "\n");
				}
				outToClient.writeBytes("OK" + "\n");
			}
		} catch (Exception e) {
		}
	}

	public static void addPC(Component comp, int index) {
		infoScrollPanel.add(comp, index);
		infoScrollPanel.revalidate();
		infoScrollPanel.repaint();
	}
}