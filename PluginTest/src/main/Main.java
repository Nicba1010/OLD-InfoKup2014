package main;

import javax.swing.JPanel;

public class Main {
	static JPanel panel = new JPanel();

	public static void main(String[] arguments) throws Throwable {
		new PluginLoader(panel);
	}

}
