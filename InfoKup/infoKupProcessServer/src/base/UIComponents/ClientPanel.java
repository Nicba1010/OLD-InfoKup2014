package base.UIComponents;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ClientPanel extends JPanel {
        @SuppressWarnings("deprecation")
		@Override
        public void setBounds(int x, int y, int width, int height) {
                reshape(x, y-5, width, height+4);
        }

}