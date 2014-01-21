package infoKupProcess;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

class TCPServer extends JFrame {
	private int screenWidth = 800, screenHeight = 600;
	JButton quitButton;
	JScrollPane procScroll;
	static ServerSocket inSocket;
	static int selectedIndex;
	private static boolean running = false;
	static ProcessesList processList1;
	public JFrame mainFrame = this;
	public static Socket connectionSocket;

	public TCPServer() {
		initUI();
	}

	public void initUI() {
		setTitle("TCPServer");

		JPanel panel = new JPanel();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(panel);
		// panel.setLayout(new BorderLayout());
		panel.setLayout(null);
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
					try {
						inSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.exit(0);
				}
			});
			panel.add(quitButton);
		}

		this.addWindowStateListener(new WindowStateListener() {

			@Override
			public void windowStateChanged(WindowEvent e) {
				System.out.println(e.getNewState());
				quitButton.setBounds(mainFrame.getWidth() - 80 - 16,
						mainFrame.getHeight() - 30 * 2 - 8, 80, 30);
			}
		});
		{
			processList1 = new ProcessesList(0, 0, 150, 400, panel);
		}
		System.out.println(this.getWidth());
		{
			// procList = new JList<String>(processesList);
			// procList.setListData(processesList);
			// procList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			// procList.addListSelectionListener(new ListSelectionListener() {
			//
			// @Override
			// public void valueChanged(ListSelectionEvent e) {
			// if (procList.getSelectedIndex() == -1) {
			//
			// } else {
			// if (selectedIndex != procList.getSelectedIndex()) {
			// System.out.println(procList.getSelectedIndex());
			// selectedIndex = procList.getSelectedIndex();
			// }
			// }
			// }
			// });
			//
			// procScroll = new JScrollPane(procList);
			// procScroll.setPreferredSize(new Dimension(150, 400));
			//
			// JPanelProcList procPanel = new JPanelProcList();
			// procPanel.add(procScroll);
			// // procPanel.setBorder(new EmptyBorder(0,-223,150,400));
			// procPanel.setBounds(0, 0, 150, 400);
			// panel.add(procPanel);
		}
	}

	public static void main(String argv[]) throws Exception {
		running = true;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				TCPServer ex = new TCPServer();
				ex.setVisible(true);
			}
		});
		processProcesses();
	}

	public static void processProcesses() {
		String clientSentence;
		try {
			inSocket = new ServerSocket(25565);
			while (running) {
				connectionSocket = inSocket.accept();
				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(connectionSocket.getInputStream()));

				clientSentence = inFromClient.readLine();
				// processes = clientSentence;
				if (clientSentence != null)
					processList1.setData(clientSentence.split(":"));
				// processesList = processes.split(":");
				// procList.setListData(processesList);
				System.out.println(clientSentence);
				// procList.setSelectedIndex(selectedIndex);
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				outToClient.writeBytes("killpr");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}