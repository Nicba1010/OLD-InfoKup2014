package infoKupProcess;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Client {
	int x, y, width, height, currentSelectedIndex = -1;
	String currentSelectedProcess, clientName;
	String[] processArray = new String[] { "not connected" };

	JButton sendCommand;
	JLabel name;
	JList<String> processesListJList;
	JPanel panel, cmdPanel;
	JPanelProcList procPanel;
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
				killProc(processesListJList.getSelectedValue(), clientName);
			}
		});
		popup.add(menuItem);
		name = new JLabel(clientName, JLabel.LEFT);
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
										.getSelectedValue();
							}
						}
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

	private void initButtons() {
		sendCommand = new JButton("CMD");
		sendCommand.setHorizontalTextPosition(SwingConstants.CENTER);

		sendCommand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String command = JOptionPane.showInputDialog("Unesi komandu!");
				command(command, clientName);
			}
		});

		cmdPanel = new JPanel(new BorderLayout());
		cmdPanel.add(sendCommand);
	}

	private void initProcPanel() {
		processesScrollPane = new JScrollPane(processesListJList);
		processesScrollPane.setPreferredSize(new Dimension(width, height));

		procPanel = new JPanelProcList();
		procPanel.setLayout(new BoxLayout(procPanel, BoxLayout.PAGE_AXIS));
		procPanel.add(name);
		procPanel.add(processesScrollPane);

		procPanel.add(cmdPanel);

		procPanel.setPreferredSize(new Dimension(width, height));
		procPanel.setBounds(x + 2 + TCPServer.clients.size() * 150, y + 5,
				width, height);
	}

	public void setData(String[] processArray) {
		Arrays.sort(processArray);
		processesListJList.setListData(processArray);
		processesListJList.setSelectedIndex(currentSelectedIndex);
	}

	private int getRow(Point point) {
		return processesListJList.locationToIndex(point);
	}

	public void killProc(String proc, String clientName) {
		TCPServer.killBuffer.add(proc);
		TCPServer.clientKillBuffer.add(clientName);
	}

	public void command(String command, String clientName) {
		TCPServer.commandBuffer.add(command);
		TCPServer.clientCommandBuffer.add(clientName);
	}
}