package infoKupProcess;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

	public static ArrayList<Client> processLists = new ArrayList<Client>();

	public JFrame mainFrame = this;
	public static int height, width;
	public static Socket connectionSocket;
	private static String[] nameAndProcesses = new String[2];

	public static ArrayList<String> clients = new ArrayList<String>();
	public static Buffer mainBuffer = new Buffer();

	static JPanel panel, boxPCinfoPanel;
	public static int sock;
	public static boolean defaultSettings = false, nosplash = false;
	JScrollPane scrollablePCinfo;
	static JFrame frame;

	public TCPServer() {
		initUI();
	}

	public void initUI() {
		setTitle("TCPServer");

		panel = new JPanel();
		boxPCinfoPanel = new JPanel();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(panel);
		panel.setLayout(null);
		boxPCinfoPanel
				.setLayout(new BoxLayout(boxPCinfoPanel, BoxLayout.X_AXIS));
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
				scrollablePCinfo.setBounds(0, 0, width - 15, 420);
			}
		});
		scrollablePCinfo = new JScrollPane(boxPCinfoPanel);
		scrollablePCinfo.setBounds(0, 0, width - 15, 420);
		scrollablePCinfo
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		panel.add(scrollablePCinfo);
		setVisible(false);

	}

	private static void createAndShowSplashScreen() throws Exception {
		Image image = ImageIO.read(TCPServer.class
				.getResource("images/splash.png"));
		BufferedImage img = (BufferedImage) image;

		frame = new JFrame("Splash");
		frame.setUndecorated(true);
		frame.add(new JLabel(new ImageIcon(image)) {
			{
				setOpaque(false);
			}
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
		frame.setBounds((int) (java.awt.Toolkit.getDefaultToolkit()
				.getScreenSize().getWidth() / 2 - img.getWidth() / 2),
				(int) (java.awt.Toolkit.getDefaultToolkit().getScreenSize()
						.getHeight() / 2 - img.getHeight() / 2),
				img.getWidth(), img.getHeight());
		RoundRectangle2D r = new RoundRectangle2D.Double(0, 0, img.getWidth(),
				img.getHeight(), 25, 25);
		frame.setShape(r);
		frame.setOpacity(0f);
		frame.setMinimumSize(frame.getPreferredSize());
		frame.setVisible(true);
	}

	public static void main(String args[]) throws Exception {
		if (args.length == 1 && args[0].toString() == "-defaultip") {
			defaultSettings = true;
		} else if (args.length == 2) {
			nosplash = true;
			sock = Integer.parseInt(args[0]);
		} else {
			System.out.println(args.length);
			for (int i = 0; i < args.length; i++) {
				System.out.println(i + ":" + args[i]);
			}
			sock = Integer.parseInt(args[0]);
		}
		running = true;
		if (!nosplash) {
			Runnable splash = new Runnable() {
				public void run() {
					try {
						createAndShowSplashScreen();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			SwingUtilities.invokeAndWait(splash);
			Thread.sleep(500);
			for (float i = 0f; i < 1f; i = i + 0.01f) {
				Thread.sleep(10);
				frame.setOpacity(i);
			}
			Thread.sleep(3000);
			for (float i = 1f; i > 0f; i = i - 0.01f) {
				Thread.sleep(10);
				frame.setOpacity(i);
			}
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				TCPServer server = new TCPServer();
				if (!nosplash) {
					frame.setVisible(false);
					frame.dispose();
				}
				server.setVisible(true);
			}
		});
		Thread.sleep(500);
		start();
	}

	public static void start() {
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
						processLists.add(new Client(0, 0, 250, 400,
								boxPCinfoPanel, nameAndProcesses[0]));
						clients.add(nameAndProcesses[0]);
					}

					for (Client client : processLists) {
						if (client.getName().equalsIgnoreCase(
								nameAndProcesses[0])) {
							client.setData(nameAndProcesses[1].split(":"));
						}
					}
					sendResponse();
					connectionSocket.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendResponse() {
		try {
			if (mainBuffer.len() > 0) {
				for (int i = 0; i < mainBuffer.len(); i++) {
					String arg0 = mainBuffer.get(i)[0];
					String arg1 = mainBuffer.get(i)[1];
					String clientName = mainBuffer.get(i)[2];
					if (clientName.equalsIgnoreCase(nameAndProcesses[0])) {
						System.out
								.println(arg0 + ":" + arg1 + ":" + clientName);
						System.out.println("OK");
						DataOutputStream outToClient = new DataOutputStream(
								connectionSocket.getOutputStream());
						outToClient.writeBytes(clientName + " " + arg0 + " "
								+ arg1 + "\n");
						System.out.println(clientName + " " + arg0 + " " + arg1
								+ "\n");
						mainBuffer.remove(i);
						break;
					}
				}
			} else {
				DataOutputStream outToClient = new DataOutputStream(
						connectionSocket.getOutputStream());
				outToClient.writeBytes("OK" + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}