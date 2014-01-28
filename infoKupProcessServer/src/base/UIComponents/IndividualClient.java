package base.UIComponents;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class IndividualClient extends JFrame{
	JPanel panel;
	int screenWidth=250,screenHeight=500;
	String clientName;
	Client client;
	Client indiClient;
	
	public IndividualClient(String clientName, final Client client) throws HeadlessException {
		super(clientName);
		this.clientName = clientName;
		this.client = client;
		initGUI();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				indiClient.setData(client.getData());
			}
		}, 0, 2000);
	}

	
	private void initGUI(){
		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		setSize(new Dimension(screenWidth,screenHeight));
		getContentPane().add(panel);
		indiClient = new Client(0, 0, screenWidth-15, screenHeight-44, panel, clientName);
		indiClient.removeButton(2);
		indiClient.removeComponent(0);
		indiClient.setData(client.getData());
		setVisible(true);
	}
}
