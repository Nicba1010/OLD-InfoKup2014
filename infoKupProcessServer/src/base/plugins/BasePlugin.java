package base.plugins;

import javax.swing.JPanel;

public interface BasePlugin {

	public void run();
	public void addJComponents(JPanel panel, int width);

}
