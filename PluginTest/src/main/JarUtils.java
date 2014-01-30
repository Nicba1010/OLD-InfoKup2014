package main;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarUtils {

	public JarUtils() {
		// TODO Auto-generated constructor stub
	}

	private String process(Object obj) {
		JarEntry entry = (JarEntry) obj;
		String name = entry.getName();
		if (name.contains("Plugin.class"))
			return name;
		return null;
	}

	public String getPluginClass(String jarLocation) throws IOException {

		JarFile jarFile = new JarFile(jarLocation);
		Enumeration<JarEntry> enumeration = jarFile.entries();
		while (enumeration.hasMoreElements()) {
			String pluginClass = process(enumeration.nextElement());
			if (pluginClass != null) {
				if (pluginClass.contains("Plugin.class")) {
					jarFile.close();
					return pluginClass.replaceAll("/", ".").replaceAll(
							".class", "");
				}
			}
		}
		jarFile.close();
		return null;
	}
}
