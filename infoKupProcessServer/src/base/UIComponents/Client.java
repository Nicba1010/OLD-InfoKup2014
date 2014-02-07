package base.UIComponents;

import java.awt.BorderLayout;
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
import base.plugins.PluginLoader;

public class Client {
	int x, y, width, height, currentSelectedIndex = -1;
	String currentSelectedProcess, clientName;
	String[] processArray = new String[] { "not connected" };

	JButton sendCommandButton, popupButton, individual;
	JLabel name;
	JList<String> processesListJList;
	JPanel panel, cmdButtonPanel, popupButtonPanel, mainButtonPanel,
			defaultButtonPanel, namePanel;
	public ClientPanel procPanel;
	JPopupMenu popup;
	JScrollPane processesScrollPane;
	PluginLoader pluginLoader;
	private String ftpServerIP;
	private String ftpServerUsername;
	private String ftpServerPassword;
	private boolean ftpOn;

	public Client(int x, int y, int width, int height, JPanel panelMain,
			final String clientName, PluginLoader pluginLoader,
			String ftpServerIP, String ftpServerUsername,
			String ftpServerPassword, boolean ftpOn) {
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
		this.ftpOn=ftpOn;
		System.out.println(ftpServerIP + ":" + ftpServerUsername + ":"
				+ ftpServerPassword);
		{
			initPopups();
			initProcessList();
			initButtons();
			initImage();
			initProcPanel();
		}

		panelMain.add(procPanel);
		panelMain.revalidate();
	}

	private void initPopups() {
		popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("Ugasi proces");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addToBuffer("killproc", processesListJList.getSelectedValue()
						.toString());
			}
		});
		popup.add(menuItem);
		name = new JLabel(clientName, JLabel.CENTER);
		namePanel = new JPanel();
		namePanel.setLayout(new BorderLayout());
		namePanel.add(name);
	}

	private void initProcessList() {
		processesListJList = new JList<String>(processArray);
		processesListJList.setListData(processArray);
		processesListJList
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		processesListJList
				.addListSelectionListener(new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (processesListJList.getSelectedIndex() == -1) {

						} else {
							if (currentSelectedIndex != processesListJList
									.getSelectedIndex()) {
								System.out.println(processesListJList
										.getSelectedIndex());
								System.out.println(processesListJList
										.getSelectedValue());
								currentSelectedIndex = processesListJList
										.getSelectedIndex();
								currentSelectedProcess = processesListJList
										.getSelectedValue().toString();
							}
						}
					}
				});
		processesListJList.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent arg0) {

			}

			@Override
			public void keyReleased(KeyEvent arg0) {

			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getExtendedKeyCode() == 127)
					addToBuffer("killproc", processesListJList
							.getSelectedValue().toString());
			}
		});
		processesListJList.addMouseListener(new MouseListener() {

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
		processesListJList.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					System.out.println("Row: " + getRow(e.getPoint()));
					processesListJList.setSelectedIndex(getRow(e.getPoint()));
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
		defaultButtonPanel = new JPanel();
		defaultButtonPanel.setLayout(new BoxLayout(defaultButtonPanel,
				BoxLayout.X_AXIS));

		TextFieldPopupButton commandButton = new TextFieldPopupButton("Run",
				"command", clientName, defaultButtonPanel, "Unesi komandu!");
		TextFieldPopupButton popupButton = new TextFieldPopupButton("Popup",
				"popup", clientName, defaultButtonPanel,
				"Unesi tekst za popup!");
		individual = new JButton("Individual");
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
								// BUG
								System.out.println(i);
								SchoolarServer.infoScrollPanel.remove(i);
								SchoolarServer.infoScrollPanel.repaint();
								SchoolarServer.infoScrollPanel.revalidate();
								IndividualClient individualClient = new IndividualClient(
										clientName, getClient(), comp, i,
										pluginLoader, ftpServerIP,
										ftpServerUsername, ftpServerPassword,ftpOn);
								individualClient.setVisible(true);
							}
							if (!SchoolarServer.removedClients.contains(client))
								i++;
						}
					}
				});
			}
		});
		defaultButtonPanel.add(individual);
		mainButtonPanel.add(defaultButtonPanel);
		if(ftpOn){
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
		processesScrollPane = new JScrollPane(processesListJList);
		processesScrollPane.setPreferredSize(new Dimension(width, height));

		procPanel = new ClientPanel();
		procPanel.setLayout(new BoxLayout(procPanel, BoxLayout.Y_AXIS));
		procPanel.add(namePanel);
		procPanel.add(processesScrollPane);
		procPanel.add(mainButtonPanel);

		procPanel.setPreferredSize(new Dimension(width, height));
		procPanel.setMaximumSize(procPanel.getPreferredSize());
		procPanel.setMinimumSize(procPanel.getPreferredSize());
	}

	public void setData(String[] processArray) {
		Arrays.sort(processArray);
		this.processArray = processArray;
		processesListJList.setListData(processArray);
		processesListJList.setSelectedIndex(currentSelectedIndex);
	}

	public String[] getData() {
		return processArray;
	}

	public void setLocation(int x, int y) {
		procPanel.setBounds(x + 2 + SchoolarServer.clients.size() * 150, y + 5,
				width, height);
		procPanel.setPreferredSize(new Dimension(width, height));
		procPanel.setMaximumSize(procPanel.getPreferredSize());
		procPanel.setMinimumSize(procPanel.getPreferredSize());
	}

	private int getRow(Point point) {
		return processesListJList.locationToIndex(point);
	}

	public void addToBuffer(String arg0, String arg1) {
		SchoolarServer.buffer.addToBuffer(arg0, arg1, this.clientName);
	}

	public String getName() {
		return clientName;
	}

	public void removeButton(int i) {
		defaultButtonPanel.remove(i);
		defaultButtonPanel.revalidate();
		mainButtonPanel.revalidate();
	}

	public void removeComponent(int i) {
		procPanel.remove(i);
		procPanel.revalidate();
	}

	public void setLocation(int x, int y, int width, int height) {
		procPanel.setBounds(x, y, width, height);
	}
}
