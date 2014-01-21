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

@SuppressWarnings("serial")
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
		processList1 = new ProcessesList(0, 0, 150, 400, panel);
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
				if (clientSentence != null)
					processList1.setData(clientSentence.split(":"));
				System.out.println(clientSentence);
				//PROBLEM{
				DataOutputStream outToClient = new DataOutputStream(
						connectionSocket.getOutputStream());
				outToClient.writeBytes("killpr");
				//PROBLEM}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}