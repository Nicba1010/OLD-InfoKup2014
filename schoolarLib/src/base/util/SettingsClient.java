package base.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsClient {
	Properties loadSettings, saveProps;
	String path;
	File settingsFile = new File(path);
	boolean existed;

	public SettingsClient(String clientName) {
		File dataFolder = new File(System.getenv("APPDATA") + "\\.Schoolar");
		path = System.getenv("APPDATA") + "\\.Schoolar\\settingsClient"
				+ clientName + ".xml";
		if (!dataFolder.exists()) {
			System.out.println("creating directory: " + ".Schoolar");
			boolean result = dataFolder.mkdir();
			if (result) {
				System.out.println("DIR created");
			}
		}
		try {
			System.out.println(settingsFile.exists());
			if (!settingsFile.exists()) {
				existed=false;
				System.out.println(settingsFile.createNewFile());
				saveProps = new Properties();
				saveProps.setProperty("ip", "127.0.0.1");
				saveProps.setProperty("port", "25565");
				saveProps.storeToXML(new FileOutputStream(settingsFile), "");
			}else{
				existed=true;
			}
			loadSettings = new Properties();
			loadSettings.loadFromXML(new FileInputStream(settingsFile));
			getSettings();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Object[] getSettings() {
		Object ip = loadSettings.getProperty("ip");
		Object port = Integer.parseInt(loadSettings.getProperty("port"));
		Object[] obj = new Object[] { ip, port };
		return obj;
	}

	public void setSettings(Object[] obj) {
		for (Object object : obj) {
			System.out.println("Settings:" + object);
		}
		saveProps = new Properties();
		saveProps.setProperty("ip", obj[0].toString());
		saveProps.setProperty("port", obj[1].toString());
		try {
			saveProps.storeToXML(new FileOutputStream(settingsFile), "");
		} catch (IOException e) {
			System.out.println("IOEXCEPTION");
			e.printStackTrace();
		}
	}

	public boolean settingsExist() {
		return existed;
	}
}
