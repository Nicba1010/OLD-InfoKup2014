package base.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.PublicKey;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import base.SchoolarServer;
import base.UIComponents.ClientPanel;
import base.UIComponents.TextFieldPopupButton;
import base.plugins.PluginLoader;

public class Client {
	boolean freezed = false;
	int x, y, width, height, currentSelectedIndex = -1;
	String currentSelectedProcess, clientName;
	String[] processArray = new String[] { "not connected" };
	JButton sendCommandButton, popupButton, individual, shutdownClient,
			infoButton, disable;
	JLabel name, timeLabel;
	JList<String> processListJList;
	JPanel panel, cmdButtonPanel, popupButtonPanel, mainButtonPanel,
			defaultButtonPanel1, defaultButtonPanel2, namePanel, timePanel,
			topPanel;
	public ClientPanel clientPanel;
	JPopupMenu popup;
	JScrollPane processesScrollPane;
	PluginLoader pluginLoader;
	private String ftpServerIP;
	private String ftpServerUsername;
	private String ftpServerPassword;
	private boolean ftpOn;
	BigInteger modulus, publicExponent;
	PublicKey publicKey;
	public IndividualClient individualClient = null;
	public Time timeRunnable;
	private Thread time;
	JPanel panelMain;
	private boolean scheduledForShutdown = false;

	private Color DARK_GREEN = new Color(34, 139, 34);
	private Color DARK_ORANGE = new Color(210, 105, 30);
	private String osInfo;
	private String javaInfo;
	private String javaPath;
	private String homeDir;
	private String extIp;
	private String[] info;
	private InetAddress inetAddress;

	/**
	 * The constructor of the Client class
	 * 
	 * @param x
	 *            the x coordinate of the client panel
	 * @param y
	 *            the y coordinate of the client panel
	 * @param width
	 *            the width of the client panel
	 * @param height
	 *            the height of the client panel
	 * @param panelMain
	 *            the main program panel
	 * @param clientName
	 *            the client name
	 * @param pluginLoader
	 *            the plugin loader object
	 * @param ftpServerIP
	 *            the image ftp server ip
	 * @param ftpServerUsername
	 *            the image ftp server username
	 * @param ftpServerPassword
	 *            the image ftp server password
	 * @param ftpOn
	 *            the state of image transfer (if it's on true, if it's off
	 *            false)
	 * @param modulus
	 *            the modulus of the public key
	 * @param exponent
	 *            the exponent of the public key
	 * @param info
	 *            the client info
	 * @param inetAddress
	 */
	public Client(int x, int y, int width, int height, JPanel panelMain,
			final String clientName, PluginLoader pluginLoader,
			String ftpServerIP, String ftpServerUsername,
			String ftpServerPassword, boolean ftpOn, BigInteger modulus,
			BigInteger exponent, String[] info, InetAddress inetAddress) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.clientName = clientName;
		this.pluginLoader = pluginLoader;
		this.ftpServerIP = ftpServerIP;
		this.ftpServerUsername = ftpServerUsername;
		this.ftpServerPassword = ftpServerPassword;
		this.ftpOn = ftpOn;
		this.modulus = modulus;
		this.publicExponent = exponent;
		this.panelMain = panelMain;
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
		{
			initPopupsAndLabels();
			initProcessList();
			initButtons();
			initProcPanel();
		}
		timeRunnable = new Time(getClient());
		time = new Thread(timeRunnable);
		time.start();
		panelMain.add(clientPanel);
		panelMain.revalidate();
	}

	/**
	 * Returns the modulus of the public key!
	 * 
	 * @return the modulus of the public key
	 */
	public BigInteger getModulus() {
		return modulus;
	}

	/**
	 * Returns the modulus of the public key!
	 * 
	 * @return the exponent of the public key
	 */
	public BigInteger getExponent() {
		return publicExponent;
	}

	/**
	 * Initializes right click popups and labels!
	 */
	private void initPopupsAndLabels() {
		popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("Ugasi proces");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addToBuffer("killproc", processListJList.getSelectedValue()
						.toString());
			}
		});
		popup.add(menuItem);
		name = new JLabel(clientName, JLabel.LEFT);
		timeLabel = new JLabel("0.0", JLabel.RIGHT);
		timeLabel.setForeground(DARK_GREEN);
		namePanel = new JPanel();
		namePanel.setLayout(new BorderLayout());
		namePanel.add(name);
		timePanel = new JPanel();
		timePanel.setLayout(new BorderLayout());
		timePanel.add(timeLabel);
		topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		topPanel.add(namePanel);
		topPanel.add(timePanel);
	}

	/**
	 * Initializes the process list!
	 */
	private void initProcessList() {
		processListJList = new JList<String>(processArray);
		processListJList.setListData(processArray);
		processListJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		processListJList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (processListJList.getSelectedIndex() == -1) {

				} else {
					if (currentSelectedIndex != processListJList
							.getSelectedIndex()) {
						currentSelectedIndex = processListJList
								.getSelectedIndex();
						currentSelectedProcess = processListJList
								.getSelectedValue().toString();
					}
				}
			}
		});
		processListJList.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {

			}

			@Override
			public void keyReleased(KeyEvent arg0) {

			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getExtendedKeyCode() == 127)
					addToBuffer("killproc", processListJList.getSelectedValue()
							.toString());
			}
		});
		processListJList.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {

			}

			@Override
			public void mousePressed(MouseEvent arg0) {

			}

			@Override
			public void mouseExited(MouseEvent arg0) {

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {

			}

			@Override
			public void mouseClicked(MouseEvent arg0) {

			}
		});
		processListJList.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					processListJList.setSelectedIndex(getRow(e.getPoint()));
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	@SuppressWarnings("unused")
	/**
	 * Initializes the buttons! 
	 */
	private void initButtons() {

		mainButtonPanel = new JPanel();
		mainButtonPanel.setLayout(new BoxLayout(mainButtonPanel,
				BoxLayout.Y_AXIS));
		defaultButtonPanel1 = new JPanel();
		defaultButtonPanel1.setLayout(new BoxLayout(defaultButtonPanel1,
				BoxLayout.X_AXIS));
		defaultButtonPanel2 = new JPanel();
		defaultButtonPanel2.setLayout(new BoxLayout(defaultButtonPanel2,
				BoxLayout.X_AXIS));

		TextFieldPopupButton commandButton = new TextFieldPopupButton(
				"Pokreni", "command", clientName, defaultButtonPanel1,
				"Unesi komandu za " + clientName);
		TextFieldPopupButton popupButton = new TextFieldPopupButton("Poruka",
				"popup", clientName, defaultButtonPanel1, "Unesi poruku za "
						+ clientName);
		individual = new JButton("Zasebni");
		individual.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						int i = 0;
						for (String client : SchoolarServer.clients) {
							if (client.equalsIgnoreCase(getName())) {
								Component comp = SchoolarServer.infoScrollPanel
										.getComponent(i);
								SchoolarServer.infoScrollPanel.remove(i);
								SchoolarServer.infoScrollPanel.repaint();
								SchoolarServer.infoScrollPanel.revalidate();
								individualClient = new IndividualClient(
										clientName, getClient(), comp, i,
										pluginLoader, ftpServerIP,
										ftpServerUsername, ftpServerPassword,
										ftpOn, modulus, publicExponent, info,
										inetAddress);
								individualClient.setVisible(true);
							}
							if (!SchoolarServer.removedClients.contains(client))
								i++;
						}
					}
				});
			}
		});
		shutdownClient = new JButton(" Ugasi ");
		shutdownClient.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						scheduleClientForShutdown();
					}
				});
			}
		});
		infoButton = new JButton("Informacije");
		infoButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						String locIp = inetAddress.toString();
						locIp = locIp.replace("/", "");
						String text = "OS: " + osInfo + "\n" + "Java: "
								+ javaInfo + "\n" + "JavaPath: " + javaPath
								+ "\n" + "User folder: " + homeDir + "\n"
								+ "IP: " + extIp + "\n" + "Local Ip: " + locIp;
						JOptionPane pane = new JOptionPane(text,
								JOptionPane.PLAIN_MESSAGE);

						JDialog popupInfo = pane.createDialog("Info");
						popupInfo.setAlwaysOnTop(true);
						popupInfo.setVisible(true);
					}
				});
			}
		});
		disable = new JButton("Zamrzni");
		disable.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						freezeClient();
						freezed = !freezed;
						if (freezed) {
							disable.setText("Odmrzni");
						} else {
							disable.setText("Zamrzni");
						}
					}
				});
			}
		});
		defaultButtonPanel1.add(individual);
		defaultButtonPanel2.add(infoButton);
		defaultButtonPanel2.add(shutdownClient);
		defaultButtonPanel2.add(disable);
		mainButtonPanel.add(defaultButtonPanel1);
		mainButtonPanel.add(defaultButtonPanel2);
		if (ftpOn) {
			Image image = new Image();
			image.addJComponentsToServer(mainButtonPanel, width, clientName,
					ftpServerIP, ftpServerUsername, ftpServerPassword);
		}
		try {
			pluginLoader.loadPlugins(mainButtonPanel, width, clientName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the Client object!
	 * 
	 * @return the Client object
	 */
	private Client getClient() {
		return this;
	}

	/**
	 * Initializes the main client panel!
	 */
	private void initProcPanel() {

		processesScrollPane = new JScrollPane(processListJList);
		processesScrollPane.setPreferredSize(new Dimension(width, height));
		clientPanel = new ClientPanel();
		clientPanel.setLayout(new BoxLayout(clientPanel, BoxLayout.Y_AXIS));
		clientPanel.add(topPanel);
		clientPanel.add(processesScrollPane);
		clientPanel.add(mainButtonPanel);

		clientPanel.setPreferredSize(new Dimension(width, height));
		clientPanel.setMaximumSize(clientPanel.getPreferredSize());
		clientPanel.setMinimumSize(clientPanel.getPreferredSize());
		clientPanel.repaint();
		clientPanel.revalidate();
	}

	/**
	 * Sets the process list data!
	 * 
	 * @param processArray
	 *            the array of processes from the client PC
	 */
	public void setData(String[] processArray) {
		if (!scheduledForShutdown) {
			processListJList.setForeground(Color.BLACK);
			Arrays.sort(processArray);
			this.processArray = processArray;
			processListJList.setListData(processArray);
			processListJList.setSelectedIndex(currentSelectedIndex);
		}
	}

	/**
	 * Returns the process list data!
	 * 
	 * @return the process list data
	 */
	public String[] getData() {
		return processArray;
	}

	/**
	 * Sets the position of the client panel!
	 * 
	 * @param x
	 *            the x coordinate of the client panel
	 * @param y
	 *            the y coordinate of the client panel
	 */
	public void setLocation(int x, int y) {
		clientPanel.setBounds(x + 2 + SchoolarServer.clients.size() * 150,
				y + 5, width, height);
		clientPanel.setPreferredSize(new Dimension(width, height));
		clientPanel.setMaximumSize(clientPanel.getPreferredSize());
		clientPanel.setMinimumSize(clientPanel.getPreferredSize());
	}

	/**
	 * Returns the index on which the point points!
	 * 
	 * @param point
	 *            the point on the JList
	 * @return the index on which the point points
	 */
	private int getRow(Point point) {
		return processListJList.locationToIndex(point);
	}

	/**
	 * Adds the command to be sent to the client to the command buffer!
	 * 
	 * @param arg0
	 *            the command
	 * @param arg1
	 *            the command arguments
	 */
	public void addToBuffer(String arg0, String arg1) {
		SchoolarServer.buffer.addToBuffer(arg0, arg1, this.clientName);
	}

	/**
	 * Returns the client name!
	 * 
	 * @return the client name
	 */
	public String getName() {
		return clientName;
	}

	/**
	 * Removes a button from the defaultButtonPanel1!
	 * 
	 * @param i
	 *            index of the button to be removed
	 */
	public void removeButtonP1(int i) {
		defaultButtonPanel1.remove(i);
		defaultButtonPanel1.revalidate();
		mainButtonPanel.revalidate();
	}

	/**
	 * Removes a component from the main client panel!
	 * 
	 * @param i
	 *            index of the component to be removed
	 */
	public void removeComponent(int i) {
		clientPanel.remove(i);
		clientPanel.revalidate();
	}

	/**
	 * Schedules the client to be shut down!
	 */
	public void scheduleClientForShutdown() {
		addToBuffer("ShutdownClient", "");
		processListJList.setForeground(Color.RED);
		processListJList.setListData(new String[] { "SCHEDULED", "FOR",
				"SHUTDOWN" });
		scheduledForShutdown = true;
	}

	/**
	 * Sets the location of the client panel!
	 * 
	 * @param x
	 *            the x coordinate of the client panel
	 * @param y
	 *            the y coordinate of the client panel
	 * @param width
	 *            the width of the client panel
	 * @param height
	 *            the height of the client panel
	 */
	public void setLocation(int x, int y, int width, int height) {
		clientPanel.setBounds(x, y, width, height);
	}

	/**
	 * Sets the size of the client panel!
	 * 
	 * @param d
	 *            the dimensions of the client panel
	 */
	public void setSize(Dimension d) {
		clientPanel.setSize(d);
		clientPanel.setMaximumSize(d);
		clientPanel.setMinimumSize(d);
		clientPanel.setPreferredSize(d);
		clientPanel.repaint();
		clientPanel.revalidate();
	}

	/**
	 * Removes the individual client window!
	 */
	public void removeIndividualClient() {
		individualClient.die();
		individualClient = null;
	}

	/**
	 * Updates the last connection time!
	 * 
	 * @param time
	 *            the last connection time
	 */
	public void updateLastConnectionTime(long time) {
		float percent = ((float) time / (float) 12000) * (float) 100;
		if (percent < 50f)
			timeLabel.setForeground(DARK_GREEN);
		else if (percent >= 50f && percent < 80f)
			timeLabel.setForeground(DARK_ORANGE);
		else if (percent >= 80f && percent < 100f)
			timeLabel.setForeground(Color.RED);
		else if (percent >= 100f)
			forceRemoveClient();
		timeLabel.setText(Float.toString(((float) time) / (float) 1000));
		if (individualClient != null)
			individualClient.updateLastConnectionTime(time);
	}

	/**
	 * Forcefully removes the client
	 */
	public void forceRemoveClient() {
		SchoolarServer.infoScrollPanel.remove(getPanel());
		timeRunnable.die();
		SchoolarServer.clientList.remove(getClient());
		SchoolarServer.infoScrollPanel.repaint();
		SchoolarServer.infoScrollPanel.revalidate();
		SchoolarServer.buffer.removeAllClientCommands(getName());
	}

	public void freezeClient() {
		addToBuffer("freezeClient", "");
	}

	/**
	 * Resets the last connection time!
	 */
	public void resetLastConnectionTime() {
		timeRunnable.resetLastConnectionTime();
		if (individualClient != null)
			individualClient.resetLastConnectionTime();
	}

	public void removeClientNameLabel() {
		topPanel.remove(0);
		topPanel.revalidate();
		topPanel.repaint();
	}

	/**
	 * Returns the client panel!
	 * 
	 * @return the client panel
	 */
	public ClientPanel getPanel() {
		return clientPanel;
	}
}