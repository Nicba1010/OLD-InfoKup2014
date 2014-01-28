package base.UIComponents;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import base.SchoolarServer;

public class TextFieldPopupButton {
	JButton button;
	String clientName;
	JPanel buttonPanel;

	public TextFieldPopupButton(String buttonName, final String type,
			String clientName, JPanel mainButtonPanel, final String popupMessage) {
		this.clientName = clientName;
		button = new JButton(buttonName);
		button.setHorizontalTextPosition(SwingConstants.CENTER);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String arg2 = JOptionPane.showInputDialog(popupMessage);
				if (arg2 != null)
					addToBuffer(type, arg2);
			}
		});

		buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(button, BorderLayout.NORTH);
		mainButtonPanel.add(buttonPanel);
	}

	public void addToBuffer(String arg0, String arg1) {
		SchoolarServer.buffer.addToBuffer(arg0, arg1, this.clientName);
	}
}
