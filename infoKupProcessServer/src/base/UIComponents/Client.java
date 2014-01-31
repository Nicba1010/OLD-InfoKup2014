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

public class Client {
	int x, y, width, height, currentSelectedIndex = -1;
	String currentSelectedProcess, clientName;
	String[] processArray = new String[] { "not connected" };

	JButton sendCommandButton, popupButton, individual;
	JLabel name;
	JList<String> processesListJList;
	JPanel panel, cmdButtonPanel, popupButtonPanel, mainButtonPanel, namePanel;
	ClientPanel procPanel;
	JPopupMenu popup;
	JScrollPane processesScrollPane;

	public Client(int x, int y, int width, int height, JPanel panelMain,
			final String clientName) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.clientName = clientName;

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
				BoxLayout.X_AXIS));

		TextFieldPopupButton commandButton = new TextFieldPopupButton("Run",
				"command", clientName, mainButtonPanel, "Unesi komandu!");
		TextFieldPopupButton popupButton = new TextFieldPopupButton("Popup",
				"popup", clientName, mainButtonPanel, "Unesi tekst za popup!");
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
								Component comp = SchoolarServer.infoScrollPanel.getComponent(i);
								SchoolarServer.infoScrollPanel.remove(i);
								SchoolarServer.infoScrollPanel.repaint();
								SchoolarServer.infoScrollPanel.revalidate();
								IndividualClient individualClient = new IndividualClient(
										clientName, getClient(), comp, i);
								individualClient.setVisible(true);
							}
							i++;
						}
					}
				});
			}
		});
		mainButtonPanel.add(individual);
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
		mainButtonPanel.remove(i);
		mainButtonPanel.revalidate();
	}

	public void removeComponent(int i) {
		procPanel.remove(i);
		procPanel.revalidate();
	}
}
