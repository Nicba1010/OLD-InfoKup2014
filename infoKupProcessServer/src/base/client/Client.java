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
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
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
	int x, y, width, height, currentSelectedIndex = -1;
	String currentSelectedProcess, clientName;
	String[] processArray = new String[] { "not connected" };
	JButton sendCommandButton, popupButton, individual, shutdownClient;
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
	IndividualClient individualClient;
	public Time timeRunnable;
	private Thread time;
	JPanel panelMain;
	private boolean scheduledForShutdown = false;

	private Color DARK_GREEN = new Color(34, 139, 34);
	private Color DARK_ORANGE = new Color(210, 105, 30);

	public Client(int x, int y, int width, int height, JPanel panelMain,
			final String clientName, PluginLoader pluginLoader,
			String ftpServerIP, String ftpServerUsername,
			String ftpServerPassword, boolean ftpOn, BigInteger modulus,
			BigInteger exponent) {
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
		{
			initPopups();
			initProcessList();
			initButtons();
			initImage();
			initProcPanel();
		}
		timeRunnable = new Time(getClient());
		time = new Thread(timeRunnable);
		time.start();
		panelMain.add(clientPanel);
		panelMain.revalidate();
	}

	public BigInteger getModulus() {
		return modulus;
	}

	public BigInteger getExponent() {
		return publicExponent;
	}

	@SuppressWarnings("unused")
	private void assemblePublicKey() throws InvalidKeySpecException,
			NoSuchAlgorithmException {
		RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus,
				publicExponent);
		KeyFactory fact = KeyFactory.getInstance("RSA");
		publicKey = fact.generatePublic(rsaPublicKeySpec);
	}

	private void initPopups() {
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
										ftpOn, modulus, publicExponent);
								individualClient.setVisible(true);
							}
							if (!SchoolarServer.removedClients.contains(client))
								i++;
						}
					}
				});
			}
		});
		shutdownClient = new JButton("Ugasi");
		shutdownClient.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						removeClient();
					}
				});
			}
		});
		defaultButtonPanel1.add(individual);
		defaultButtonPanel2.add(shutdownClient);
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

	private Client getClient() {
		return this;
	}

	private void initImage() {

	}

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

	public void setData(String[] processArray) {
		if (!scheduledForShutdown) {
			processListJList.setForeground(Color.BLACK);
			Arrays.sort(processArray);
			this.processArray = processArray;
			processListJList.setListData(processArray);
			processListJList.setSelectedIndex(currentSelectedIndex);
		}
	}

	public String[] getData() {
		return processArray;
	}

	public void setLocation(int x, int y) {
		clientPanel.setBounds(x + 2 + SchoolarServer.clients.size() * 150,
				y + 5, width, height);
		clientPanel.setPreferredSize(new Dimension(width, height));
		clientPanel.setMaximumSize(clientPanel.getPreferredSize());
		clientPanel.setMinimumSize(clientPanel.getPreferredSize());
	}

	private int getRow(Point point) {
		return processListJList.locationToIndex(point);
	}

	public void addToBuffer(String arg0, String arg1) {
		SchoolarServer.buffer.addToBuffer(arg0, arg1, this.clientName);
	}

	public String getName() {
		return clientName;
	}

	public void removeButton(int i) {
		defaultButtonPanel1.remove(i);
		defaultButtonPanel1.revalidate();
		mainButtonPanel.revalidate();
	}

	public void removeComponent(int i) {
		clientPanel.remove(i);
		clientPanel.revalidate();
	}

	public void removeClient() {
		addToBuffer("ShutdownClient", "");
		processListJList.setForeground(Color.RED);
		processListJList.setListData(new String[]{"SCHEDULED","FOR","SHUTDOWN"});
		scheduledForShutdown = true;
	}

	public void setLocation(int x, int y, int width, int height) {
		clientPanel.setBounds(x, y, width, height);
	}

	public void setSize(Dimension d) {
		clientPanel.setSize(d);
		clientPanel.setMaximumSize(d);
		clientPanel.setMinimumSize(d);
		clientPanel.setPreferredSize(d);
		clientPanel.repaint();
		clientPanel.revalidate();
	}

	public void removeIndividualClient() {
		individualClient.die();
	}

	public void updateLastConnectionTime(long time) {
		float percent = ((float) time / (float) 12000) * (float) 100;
		if (percent < 50f)
			timeLabel.setForeground(DARK_GREEN);
		else if (percent >= 50f && percent < 80f)
			timeLabel.setForeground(DARK_ORANGE);
		else if (percent >= 80f && percent < 100f)
			timeLabel.setForeground(Color.RED);
		else if (percent >= 100f)
			removeClient();
		timeLabel.setText(Float.toString(((float) time) / (float) 1000));
	}

	public void resetLastConnectionTime() {
		timeRunnable.resetLastConnectionTime();
	}

	public ClientPanel getPanel() {
		return clientPanel;
	}
}