package infoKupProcess;

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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
class TCPServer extends JFrame {
	private int screenWidth = 800, screenHeight = 600;
	JButton quitButton;
	JScrollPane procScroll;
	public static ServerSocket inSocket;
	static int selectedIndex;
	private static boolean running = false;
	static ProcessesList processList1;
	public JFrame mainFrame = this;
	public static int height;
	public static int width;
	public static Socket connectionSocket;
	private static String[] nameAndProcesses = new String[2];
	public static ArrayList<String> clients = new ArrayList<String>();
	public static ArrayList<String> killBuffer = new ArrayList<String>();
	public static ArrayList<String> clientKillBuffer = new ArrayList<String>();
	static JPanel panel;
	public static int sock;

	public TCPServer() {
		initUI();
	}

	public void initUI() {
		setTitle("TCPServer");

		panel = new JPanel();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(panel);
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
					System.exit(0);
				}
			});
			panel.add(quitButton);
		}
		height = mainFrame.getHeight();
		width = mainFrame.getWidth();
		this.addWindowStateListener(new WindowStateListener() {

			@Override
			public void windowStateChanged(WindowEvent e) {
				System.out.println(e.getNewState());
				height = mainFrame.getHeight();
				width = mainFrame.getWidth();
				quitButton.setBounds(mainFrame.getWidth() - 80 - 16,
						mainFrame.getHeight() - 30 * 2 - 8, 80, 30);
			}
		});

	}

	public static void main(String argv[]) throws Exception {
		sock = Integer.parseInt(argv[0]);
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
			inSocket = new ServerSocket(sock);
			while (running) {
				connectionSocket = inSocket.accept();
				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(connectionSocket.getInputStream()));

				clientSentence = inFromClient.readLine();
				if (clientSentence != null) {
					nameAndProcesses = clientSentence.split(";");

					if (clients.contains(nameAndProcesses[0])) {
					} else {
						processList1 = new ProcessesList(0, 0, 150, 400, panel,
								nameAndProcesses[0]);
						clients.add(nameAndProcesses[0]);
					}

					processList1.setData(nameAndProcesses[1].split(":"));
				}
				if (killBuffer.size() > 0) {

					for (int i = 0; i < killBuffer.size(); i++) {
						if (clientKillBuffer.get(i).equalsIgnoreCase(
								nameAndProcesses[0])) {
							System.out.println("OK");
							DataOutputStream outToClient = new DataOutputStream(
									connectionSocket.getOutputStream());
							outToClient.writeBytes(clientKillBuffer.get(i)
									+ " killproc " + killBuffer.get(i) + "\n");
							clientKillBuffer.remove(i);
							killBuffer.remove(i);
							break;
						} else {
						}
					}

					// DataOutputStream outToClient = new DataOutputStream(
					// connectionSocket.getOutputStream());
					// outToClient.writeBytes("killproc " + killprocess + "\n");
				} else {
					DataOutputStream outToClient = new DataOutputStream(
							connectionSocket.getOutputStream());
					outToClient.writeBytes("OK" + "\n");
				}
				connectionSocket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}