package base.plugins;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;
import java.util.ArrayList;

import javax.swing.JPanel;

import base.util.JarUtils;

@SuppressWarnings("resource")
public class PluginLoader {
	JarUtils jarUtils = new JarUtils();
	ArrayList<BasePlugin> plugins = new ArrayList<BasePlugin>();
	boolean server;

	public PluginLoader(boolean server) throws Exception {
		this.server = server;
		String dataFolder = System.getenv("APPDATA") + "\\.Schoolar";
		Policy.setPolicy(new PluginPolicy());
		System.setSecurityManager(new SecurityManager());
		String file;
		String jarLocation;
		String pluginClass;
		File folder = new File(dataFolder);
		if (!folder.exists()) {
			System.out.println("creating directory: " + ".Schoolar");
			boolean result = folder.mkdir();
			if (result) {
				System.out.println("DIR created");
			}
		}
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles.length == 0) {
			System.out.println("No plugins!");
		} else {
			for (int i = 0; i < listOfFiles.length; i++) {
				file = listOfFiles[i].getName();
				if (listOfFiles[i].isFile() && file.endsWith(".JAR")
						|| file.endsWith(".jar")) {
					jarLocation = dataFolder + "\\" + listOfFiles[i].getName();
					pluginClass = jarUtils.getPluginClass(jarLocation);
					if (pluginClass != null) {
						File jarFile = new File(jarLocation);
						URL url = jarFile.toURI().toURL();
						URL[] urls = { url };
						ClassLoader loader = new URLClassLoader(urls);
						System.out.println(listOfFiles[i].getName() + ""
								+ pluginClass);
						plugins.add((BasePlugin) loader.loadClass(pluginClass)
								.newInstance());
					}
				}
			}
		}

	}

	public void loadPlugins(JPanel panel, int width, String clientName) {
		for (BasePlugin plugin : plugins) {
			if (server) {
				plugin.runServer();
				plugin.addJComponentsToServer(panel, width, clientName);
			}
		}
	}

	public void checkInput(String input) {
		for (BasePlugin plugin : plugins) {
			plugin.checkInputFromClient(input);
		}
	}

	public void runClient(String input, String[] args) {
		for (BasePlugin plugin : plugins) {
			plugin.runClient(args);
		}
	}

}
