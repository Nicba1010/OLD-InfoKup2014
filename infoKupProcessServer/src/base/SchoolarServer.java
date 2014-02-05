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
	static boolean resChange = false;
	int screenWidth = 1280, screenHeight = 720;
	JButton quitButton;
	JButton settingButton;
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
	public static int socketTCP;
	public static boolean defaultSettings = false, nosplash = false;
	static JScrollPane scrollablePCinfo;
	static JFrame splashFrame;
	static PluginLoader pluginLoader;

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

	public void settings() {
        
		JTextField width = new JTextField("" + screenWidth);
		JTextField height = new JTextField("" + screenHeight);
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(new JLabel("Širina: "));
		panel.add(width);
		panel.add(new JLabel("Visina: "));
		panel.add(height);
		JOptionPane.showConfirmDialog(null, panel, "Postavke",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		screenWidth = Integer.parseInt(width.getText());
		screenHeight = Integer.parseInt(height.getText());
		System.out.println("" + screenWidth);
		System.out.println("" + screenHeight);
		//Treba sloziti da ponovo pokrene server sa novom rezolucijom a ako je stara da nista ne napravi

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
				settingButton = new JButton("Postavke");
				settingButton.setBounds(mainFrame.getWidth() - 180 - 16,
						mainFrame.getHeight() - 30 * 2 - 8, 100, 30);
				settingButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent event1) {
						settings();

					}
				});

			}
			mainPanel.add(quitButton);
			mainPanel.add(settingButton);
		}
		this.addWindowStateListener(new WindowStateListener() {

			@Override
			public void windowStateChanged(WindowEvent e) {
				System.out.println(e.getNewState());
				quitButton.setBounds(mainFrame.getWidth() - 80 - 16,
						mainFrame.getHeight() - 30 * 2 - 8, 80, 30);
				scrollablePCinfo.setBounds(0, 0, mainFrame.getWidth() - 15, 620);
			}
		});
		scrollablePCinfo = new JScrollPane(infoScrollPanel);
		scrollablePCinfo.setBounds(0, 0, mainFrame.getWidth() - 15, 620);
		scrollablePCinfo
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		mainPanel.add(scrollablePCinfo);
		setVisible(false);

		//color();

	}

	private static void parseArgs(String args[]) {
		if (args.length == 1 && args[0].toString() == "-defaultip") {
			defaultSettings = true;
		} else if (args.length == 2
				&& args[1].toString().equalsIgnoreCase("nosplash")) {
			nosplash = true;
			socketTCP = Integer.parseInt(args[0]);
		} else {
			System.out.println(args.length);
			for (int i = 0; i < args.length; i++) {
				System.out.println(i + ":" + args[i]);
			}
			socketTCP = Integer.parseInt(args[0]);
		}
	}

	public static void main(String args[]) throws Exception {

		parseArgs(args);
		running = true;
		do {
			if (!nosplash)
				new SplashScreen("images/splash.png", 500, 2, 750);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					SchoolarServer server = new SchoolarServer();
					server.setVisible(true);
				}
			});
			start();
		} while (resChange = true);
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

					if (clients.contains(TCPData[0])) {

					} else {
						System.out.println(TCPData[0]);
						processLists.add(new Client(0, 0, 250, 600,
								infoScrollPanel, TCPData[0], pluginLoader));
						clients.add(TCPData[0]);
					}

					for (Client client : processLists) {
						if (client.getName().equalsIgnoreCase(TCPData[0])) {
							client.setData(TCPData[1].split(":"));
						}
					}
					sendResponse();
					connectionSocket.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendResponse() {
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
						outToClient.writeBytes(clientName + " " + arg0 + " "
								+ arg1 + "\n");
						System.out.println(clientName + " " + arg0 + " " + arg1
								+ "\n");
						buffer.remove(i);
						break;
					}
				}
			} else {
				DataOutputStream outToClient = new DataOutputStream(
						connectionSocket.getOutputStream());
				outToClient.writeBytes("OK" + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addPC(Component comp, int index) {
		infoScrollPanel.add(comp, index);
		infoScrollPanel.revalidate();
		infoScrollPanel.repaint();
	}

	public static void addPC(Component comp) {
		infoScrollPanel.add(comp);
		infoScrollPanel.revalidate();
		infoScrollPanel.repaint();
	}
}