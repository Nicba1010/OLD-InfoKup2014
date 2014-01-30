package pljugin;

import javax.swing.JPanel;

import main.PluginBase;

public class Plugin implements PluginBase {

	public Plugin() {
		// TODO Auto-generated constructor stub
	}

	public void run() {
		System.out.println("PERIŠA JE IDIOT");
	}

	public void addJButtons(JPanel panel) {
		System.out.println("TEST");
	}

}
