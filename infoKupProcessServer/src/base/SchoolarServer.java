package base;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import base.client.Client;
import base.plugins.PluginLoader;
import base.security.RSA;
import base.splash.SplashScreen;
import base.util.Buffer;
import base.util.Settings;

@SuppressWarnings("serial")
public class SchoolarServer extends JFrame {
	static int screenWidth = 1280;
	static int screenHeight = 720;
	JButton settingsButton;
	JButton quitButton;
	JButton closeConButton;
	JButton sendAllButton;
	JButton massMessage;
	JButton mobileClientOn;
	JButton about;
	JPanel defaultButtonPanel;
	public static ServerSocket inSocket;
	private static boolean running = false;
	public static boolean loop = true;
	public static boolean mobileControl;
	public JFrame mainFrame = this;
	public static Socket connectionSocket;
	private static String[] TCPData = new String[2];
	private static String[] TCPDataWithKey = new String[7];

	public static ArrayList<Client> clientList = new ArrayList<Client>();
	public static ArrayList<String> clients = new ArrayList<String>();
	public static ArrayList<String> removedClients = new ArrayList<String>();

	public static Buffer buffer = new Buffer();

	public static JPanel mainPanel;
	public static JPanel infoScrollPanel = new JPanel();;
	public static int socketTCP = 25565;
	public static int cond = 0;
	public static boolean defaultSettings = false, nosplash = false;
	static JScrollPane scrollablePCinfo;
	static JFrame splashFrame;
	static PluginLoader pluginLoader;
	static String ftpServerIP = "127.0.0.1", ftpServerUsername = "infokup",
			ftpServerPassword = "12346789", ftpServerIPRemote = "127.0.0.1";
	static boolean ftpOn;
	private boolean firstTime = true;
	static Object[] objectSettings = new Object[8];
	static Settings settings;
	static RSA encryption = new RSA();
	static boolean encrypt = true;

	public SchoolarServer() {
		settings = new Settings();
		settingsPopup();
		setResizable(false);
		try {
			pluginLoader = new PluginLoader(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		initUI();
		updateBounds();
		firstTime = false;
	}

	public static void getSettings() {
		objectSettings = settings.getSettings();
		screenWidth = (int) objectSettings[0];
		screenHeight = (int) objectSettings[1];
		System.out.println(objectSettings[1].toString());
		socketTCP = (int) objectSettings[2];
		ftpOn = (boolean) objectSettings[3];
		ftpServerIP = (String) objectSettings[4];
		ftpServerIPRemote = (String) objectSettings[5];
		ftpServerUsername = (String) objectSettings[6];
		ftpServerPassword = (String) objectSettings[7];
	}

	public static void setSettings() {
		objectSettings[0] = screenWidth;
		objectSettings[1] = screenHeight;
		objectSettings[2] = socketTCP;
		objectSettings[3] = ftpOn;
		objectSettings[4] = ftpServerIP;
		objectSettings[5] = ftpServerIPRemote;
		objectSettings[6] = ftpServerUsername;
		objectSettings[7] = ftpServerPassword;
		settings.setSettings(objectSettings);
	}

	public void settingsPopup() {
		getSettings();
		JTextField width = new JTextField("" + screenWidth);
		JTextField height = new JTextField("" + screenHeight);
		final JTextField serverIP = new JTextField("" + ftpServerIP);
		final JTextField serverIpRemote = new JTextField("" + ftpServerIPRemote);
		final JTextField serverUser = new JTextField("" + ftpServerUsername);
		final JTextField serverPass = new JTextField("" + ftpServerPassword);
		JCheckBox checkbox = new JCheckBox("Prikaz klijentske slike");
		JTextField TCPsocket = new JTextField("" + socketTCP);
		final JPanel settingsSocketPanel = new JPanel(new GridLayout(0, 1));
		settingsSocketPanel.add(new JLabel("Sirina: "));
		settingsSocketPanel.add(width);
		settingsSocketPanel.add(new JLabel("Visina: "));
		settingsSocketPanel.add(height);
		checkbox.setSelected(ftpOn);
		if (firstTime) {
			settingsSocketPanel.add(new JLabel("Port: "));
			settingsSocketPanel.add(TCPsocket);
			settingsSocketPanel.add(checkbox);
			settingsSocketPanel.add(new JLabel("FTP Server IP"));
			settingsSocketPanel.add(serverIP);
			settingsSocketPanel.add(new JLabel("FTP Remote Server IP"));
			settingsSocketPanel.add(serverIpRemote);
			settingsSocketPanel.add(new JLabel("FTP Server Username"));
			settingsSocketPanel.add(serverUser);
			settingsSocketPanel.add(new JLabel("FTP Server Password"));
			settingsSocketPanel.add(serverPass);
			if (checkbox.isSelected()) {
				serverIP.setEnabled(true);
				serverIpRemote.setEnabled(true);
				serverUser.setEnabled(true);
				serverPass.setEnabled(true);
				settingsSocketPanel.revalidate();
				settingsSocketPanel.repaint();
			} else {
				serverIP.setEnabled(false);
				serverIpRemote.setEnabled(false);
				serverUser.setEnabled(false);
				serverPass.setEnabled(false);
				settingsSocketPanel.revalidate();
				settingsSocketPanel.repaint();
			}
			checkbox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					JCheckBox checkbox = (JCheckBox) event.getSource();
					if (checkbox.isSelected()) {
						serverIP.setEnabled(true);
						serverIpRemote.setEnabled(true);
						serverUser.setEnabled(true);
						serverPass.setEnabled(true);
						settingsSocketPanel.revalidate();
						settingsSocketPanel.repaint();
					} else {
						serverIP.setEnabled(false);
						serverIpRemote.setEnabled(false);
						serverUser.setEnabled(false);
						serverPass.setEnabled(false);
						settingsSocketPanel.revalidate();
						settingsSocketPanel.repaint();
					}
				}
			});
		}
		int input = JOptionPane.showConfirmDialog(null, settingsSocketPanel,
				"Postavke", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (input == JOptionPane.CANCEL_OPTION && firstTime) {
			System.exit(0);
		}
		if (firstTime) {
			if (checkbox.isSelected()) {
				ftpOn = true;
			} else {
				ftpOn = false;
			}
			if (input == JOptionPane.OK_OPTION) {
				socketTCP = Integer.parseInt(TCPsocket.getText());
				ftpServerIP = serverIP.getText();
				ftpServerIPRemote = serverIpRemote.getText();
				ftpServerUsername = serverUser.getText();
				ftpServerPassword = serverPass.getText();
				loop = false;
			} else {
				System.out.println("Using default port!");
			}
		}
		if (input == JOptionPane.OK_OPTION) {
			screenWidth = Integer.parseInt(width.getText());
			screenHeight = Integer.parseInt(height.getText());
		}
		if (!firstTime)
			updateBounds();
		setSettings();
	}

	public void updateBounds() {
		this.setSize(screenWidth, screenHeight);
		quitButton.setBounds(screenWidth - 104, screenHeight - 56, 100, 30);
		settingsButton.setBounds(screenWidth - 104, screenHeight - 85, 100, 30);
		closeConButton.setBounds(screenWidth - 504, screenHeight - 56, 200, 30);
		sendAllButton.setBounds(screenWidth - 304, screenHeight - 85, 200, 30);
		massMessage.setBounds(screenWidth - 304, screenHeight - 56, 200, 30);
		mobileClientOn.setBounds(screenWidth - 504, screenHeight - 85, 200, 30);
		about.setBounds(screenWidth - 654, screenHeight - 56, 150, 30);
		scrollablePCinfo.setBounds(0, 0, screenWidth - 3, screenHeight - 100);
		for (Client client : clientList) {
			client.setSize(new Dimension(250, screenHeight - 125));
		}
		infoScrollPanel.repaint();
		infoScrollPanel.revalidate();
		this.repaint();
		this.revalidate();
	}

	public void initUI() {
		int offsetWidth = 5;
		int offsetHeight = -3;
		setTitle("Schoolar Server");
		mainPanel = new JPanel();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(mainPanel);
		mainPanel.setLayout(null);
		infoScrollPanel.setLayout(new BoxLayout(infoScrollPanel,
				BoxLayout.X_AXIS));

		setSize(screenWidth, screenHeight);
		setLocationRelativeTo(null);
		{
			quitButton = new JButton("Ugasi");
			quitButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					running = false;
					System.exit(0);
				}
			});
		}
		{
			settingsButton = new JButton("Postavke");
			settingsButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event1) {
					settingsPopup();
					mainFrame.setSize(screenWidth, screenHeight);
				}
			});
		}
		{
			closeConButton = new JButton("Ugasi sve klijente");
			closeConButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event1) {
					shutdownConnections();
				}
			});
		}
		{
			sendAllButton = new JButton("Posalji komandu svima");
			sendAllButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event1) {
					sendCommandToAll(JOptionPane
							.showInputDialog("Unesi komandu za sva spojena racunala:"));
				}
			});
		}
		{
			massMessage = new JButton("Posalji poruku svima");
			massMessage.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event1) {
					massMessage(JOptionPane
							.showInputDialog("Unesi poruku za sva spojena racunala:"));
				}
			});
		}
		{
			mobileClientOn = new JButton("Omoguci mobilnu kontrolu");
			mobileClientOn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event1) {
					mobileControl = !mobileControl;
					if (mobileControl) {
						mobileClientOn.setText("Onemoguci mobilnu kontrolu");
						setTitle("Schoolar Server - Mobilna kontrola");
					} else {
						mobileClientOn.setText("Omoguci mobilnu kontrolu");
						setTitle("Schoolar Server");
					}

				}
			});
		}
		{
			about = new JButton("O programu");
			about.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event1) {
					String text = "Schoolar - InfoKup 2014\n-----------------------------------\nProgram razvili:\n-Roberto Anic Banic\n-Nikola Krsic TSRB\n";

					JOptionPane pane = new JOptionPane(text,
							JOptionPane.PLAIN_MESSAGE);

					JDialog popupInfo = pane.createDialog("O programu");
					popupInfo.setAlwaysOnTop(true);
					popupInfo.setVisible(true);

				}
			});
		}
		mainPanel.add(sendAllButton);
		mainPanel.add(closeConButton);
		mainPanel.add(quitButton);
		mainPanel.add(settingsButton);
		mainPanel.add(massMessage);
		mainPanel.add(mobileClientOn);
		mainPanel.add(about);

		this.addWindowStateListener(new WindowStateListener() {

			@Override
			public void windowStateChanged(WindowEvent e) {
				updateBounds();
			}
		});
		scrollablePCinfo = new JScrollPane(infoScrollPanel);
		scrollablePCinfo.setBounds(0, 0, mainFrame.getWidth() - offsetWidth,
				mainFrame.getHeight() - 110 - offsetHeight);
		scrollablePCinfo
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		mainPanel.add(scrollablePCinfo);
		setVisible(false);
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
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SchoolarServer server = new SchoolarServer();
				server.setVisible(true);
				Image image = null;
				try {
					image = ImageIO.read(getClass().getResource(
							"images/icon.png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				server.setIconImage(image);
			}
		});
		start();
	}

	public static void start() throws InterruptedException {
		while (loop) {
			Thread.sleep(100);
		}
		String clientSentence;
		try {
			inSocket = new ServerSocket(socketTCP);
			while (running) {
				connectionSocket = inSocket.accept();
				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(connectionSocket.getInputStream()));
				clientSentence = inFromClient.readLine();
				if (clientSentence != null) {
					if (clientSentence.contains("androidMobileDevice")) {
						String[] arr = clientSentence.split(";");
						clientSentence = arr[1];
						if (mobileControl) {
							String toMobileControl = "";
							if (clientSentence.equals("getClients()")) {
								if (clientList.size() > 0) {
									boolean atLeastOneAliveClient = false;
									for (Client client : clientList) {
										if (!client.dead) {
											atLeastOneAliveClient = true;
											toMobileControl += client.getName()
													+ ";";
										}
									}
									if (atLeastOneAliveClient)
										toMobileControl = toMobileControl
												.substring(0, toMobileControl
														.length() - 1);
									else
										toMobileControl = "NOCLIENTS";
								} else {
									toMobileControl = "NOCLIENTS";
								}
								if (arr.length > 2)
									processMobileCommand(arr[2]);
							} else {
								for (Client client : clientList) {
									if (client.getName().equals(clientSentence)
											&& !client.dead) {
										for (String string1 : client.getData()) {
											toMobileControl += string1 + ";";
										}
										toMobileControl = toMobileControl
												.substring(0, toMobileControl
														.length() - 1);
										if (arr.length > 2)
											processMobileCommand(arr[2]);
									}
								}
							}
							toMobileControl += "\n";

							DataOutputStream outToClient = new DataOutputStream(
									connectionSocket.getOutputStream());
							outToClient.writeBytes(toMobileControl);
						} else {
							DataOutputStream outToClient = new DataOutputStream(
									connectionSocket.getOutputStream());
							final String msg = "MOBILE CONTROL NOT ENABLED\n";
							outToClient.writeBytes(msg);
							Thread popupThread = new Thread(new Runnable() {
								public void run() {
					//				JOptionPane pane = new JOptionPane(msg,
					//						JOptionPane.PLAIN_MESSAGE);

					//				JDialog dialog = pane
					//						.createDialog("Poruka");
					//				dialog.setAlwaysOnTop(true);
					//				dialog.setVisible(true);

								}
							});
							popupThread.start();
						}
					} else {
						if (clientSentence != null) {
							boolean newClient = false;
							TCPData = clientSentence.split(";");

							if (clients.contains(TCPData[0])) {
							} else {
								TCPDataWithKey = clientSentence.split("-:-");
								BigInteger modulus = null;
								BigInteger exponent = null;
								String[] info = new String[5];
								if (TCPDataWithKey.length == 8) {
									modulus = new BigInteger(TCPDataWithKey[1]);
									exponent = new BigInteger(TCPDataWithKey[2]);
									info[0] = TCPDataWithKey[3];
									info[1] = TCPDataWithKey[4];
									info[2] = TCPDataWithKey[5];
									info[3] = TCPDataWithKey[6];
									info[4] = TCPDataWithKey[7];
								}
								TCPData = TCPDataWithKey[0].split(";");
								clientList.add(new Client(0, 0, 250,
										screenHeight - 125, infoScrollPanel,
										TCPData[0], pluginLoader, ftpServerIP,
										ftpServerUsername, ftpServerPassword,
										ftpOn, modulus, exponent, info,
										connectionSocket.getInetAddress()));
								clients.add(TCPData[0]);
								newClient = true;
							}

							for (Client client : clientList) {
								if (client.getName().equalsIgnoreCase(
										TCPData[0])) {
									client.setData(TCPData[1].split(":"));
									client.resetLastConnectionTime();
								}
							}
							sendResponse(newClient);
						}
					}
					connectionSocket.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void processMobileCommand(String arr) {
		String[] array = arr.split(":?:");
		System.out.println(arr);
		for (Client c : clientList) {
			if (c.getName().equals(array[0]))
				c.addToBuffer(array[4], array[2]);
		}
	}

	private static void sendCommandToAll(String command) {
		for (Client client : clientList) {
			buffer.addToBuffer("command", command, client.getName());
		}
	}

	private static void massMessage(String msg) {
		if (msg != null) {
			for (Client client : clientList) {
				buffer.addToBuffer("popup", msg, client.getName());
			}
		}

	}

	private static void shutdownConnections() {
		for (Client client : clientList) {
			client.scheduleClientForShutdown();
		}
	}

	private static String getEncryptedData(BigInteger modulus,
			BigInteger exponent, String msg) throws NoSuchAlgorithmException,
			InvalidKeySpecException, IOException {
		byte[] encryptedData = encryption.encryptData(msg, modulus, exponent);
		String bytes = "";
		for (byte b : encryptedData) {
			bytes = bytes + ";" + Byte.toString(b);
		}
		return bytes;
	}

	private static void sendResponse(boolean newClient) {
		try {
			if (buffer.len() > 0) {
				for (int i = 0; i < buffer.len(); i++) {
					String arg0 = buffer.get(i)[0];
					String arg1 = buffer.get(i)[1];
					String clientName = buffer.get(i)[2];
					if (clientName.equalsIgnoreCase(TCPData[0])) {
						BigInteger modulus = null, exponent = null;
						for (Client client : clientList) {
							if (client.getName().equalsIgnoreCase(TCPData[0])) {
								modulus = client.getModulus();
								exponent = client.getExponent();
							}
						}
						DataOutputStream outToClient = new DataOutputStream(
								connectionSocket.getOutputStream());
						String msg;
						msg = clientName + " " + arg0 + " " + arg1
								+ "-:NOTENCRYPTED:-" + "\n";
						if (encrypt)
							outToClient.writeBytes(getEncryptedData(modulus,
									exponent, msg));
						else
							outToClient.writeBytes(msg);
						buffer.remove(i);
						if (arg0.contains("ShutdownClient")) {
							clients.remove(clientName);
							removedClients.remove(clientName);
							for (Client c : clientList) {
								if (c.getName().equalsIgnoreCase(clientName)) {
									System.out.println("LALALAL");
									c.dead = true;
									infoScrollPanel.remove(c.getPanel());
									infoScrollPanel.repaint();
									infoScrollPanel.revalidate();
									c.timeRunnable.die();
									c.individualClient.die();
									clientList.remove(c);
									infoScrollPanel.repaint();
									infoScrollPanel.revalidate();
									buffer.removeAllClientCommands(c.getName());
								}
							}

						}
						break;
					}

				}
			} else {
				BigInteger modulus = null, exponent = null;
				for (Client client : clientList) {
					if (client.getName().equalsIgnoreCase(TCPData[0])) {
						modulus = client.getModulus();
						exponent = client.getExponent();
					}

				}
				DataOutputStream outToClient = new DataOutputStream(
						connectionSocket.getOutputStream());
				String message;
				if (newClient && ftpOn) {
					message = ("FTP:" + ftpServerIPRemote + ":"
							+ ftpServerUsername + ":" + ftpServerPassword
							+ "-:NOTENCRYPTED:-" + "\n");
				} else if (!ftpOn && newClient) {
					message = ("FTPNOTON" + "-:NOTENCRYPTED:-" + "\n");
				} else {
					message = ("OK" + "-:NOTENCRYPTED:-" + "\n");
				}
				if (encrypt)
					outToClient.writeBytes(getEncryptedData(modulus, exponent,
							message));
				else
					outToClient.writeBytes(message);
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