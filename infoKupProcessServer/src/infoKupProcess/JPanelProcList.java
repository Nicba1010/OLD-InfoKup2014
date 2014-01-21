package infoKupProcess;

import javax.swing.JPanel;

public class JPanelProcList extends JPanel {
	@Override
	public void setBounds(int x, int y, int width, int height) {
		reshape(x, y-5, width, height+4);
	}

}
