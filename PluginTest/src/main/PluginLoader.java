package main;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;

import javax.swing.JPanel;

@SuppressWarnings("resource")
public class PluginLoader {
	JarUtils jarUtils = new JarUtils();
	public PluginLoader(JPanel panel) throws Exception {
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
						PluginBase plugin = null;
						File jarFile = new File(jarLocation);
						URL url = jarFile.toURI().toURL();
						URL[] urls = { url };
						ClassLoader loader = new URLClassLoader(urls);
						plugin = (PluginBase) loader.loadClass(pluginClass).newInstance();
						plugin.run();
						plugin.addJButtons(panel);
					}
				}
			}
		}

	}

}