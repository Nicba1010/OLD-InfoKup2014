package infoKupProcess;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.BoxLayout;
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

public class ProcessesList {
	int x, y, width, height;
	JPanel panel;
	JList<String> processesListJList;
	JScrollPane processesScrollPane;
	int currentSelectedIndex = -1;
	String currentSelectedProcess;
	String[] processArray = new String[] { "not connected" };

	public ProcessesList(int x, int y, int width, int height, JPanel panelMain, String clientName) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		final JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("Ugasi proces");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				killProc(processesListJList.getSelectedValue());
			}
		});
		popup.add(menuItem);
		JLabel name = new JLabel(clientName, JLabel.LEFT);
		
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
		
		processesScrollPane = new JScrollPane(processesListJList);
		processesScrollPane.setPreferredSize(new Dimension(width, height));

		JPanelProcList procPanel = new JPanelProcList();
		procPanel.setLayout(new BoxLayout(procPanel, BoxLayout.PAGE_AXIS));
		procPanel.add(name);
		procPanel.add(processesScrollPane);
		procPanel.setPreferredSize(new Dimension(width, height));
		procPanel.setBounds(x+2+TCPServer.clients.size()*150, y+5, width, height);
		panelMain.add(procPanel);
		panelMain.revalidate();
	}

	public void setData(String[] processArray) {
		Arrays.sort(processArray);
		processesListJList.setListData(processArray);
		processesListJList.setSelectedIndex(currentSelectedIndex);
	}

	private int getRow(Point point) {
		return processesListJList.locationToIndex(point);
	}

	public void killProc(String proc) {
		TCPServer.kill = true;
		TCPServer.killprocess = proc;
	}
}
