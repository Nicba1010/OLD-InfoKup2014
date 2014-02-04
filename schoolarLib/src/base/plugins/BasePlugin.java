package base.plugins;

import javax.swing.JPanel;

public interface BasePlugin {

	public void runServer();
	public void addJComponentsToServer(JPanel panel, int width, String clientName);
	public void runClient(String[] args);
	public void checkInputFromClient(String input);
	
	
}
