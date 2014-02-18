package base.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class Settings {
	Properties loadSettings, saveProps;
	String path = System.getenv("APPDATA") + "\\.Schoolar\\settings.xml";
	File settingsFile = new File(path);

	public Settings() {
		File dataFolder = new File(System.getenv("APPDATA") + "\\.Schoolar");
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
				System.out.println(settingsFile.createNewFile());
				saveProps = new Properties();
				saveProps.setProperty("height", "1280");
				saveProps.setProperty("width", "720");
				saveProps.setProperty("port", "25565");
				saveProps.setProperty("ftpState", "false");
				saveProps.setProperty("ftpIp", "127.0.0.1");
				saveProps.setProperty("ftpRemoteIp", "127.0.0.1");
				saveProps.setProperty("ftpUsername", "infokup");
				saveProps.setProperty("ftpPassword", "12346789");
				saveProps.storeToXML(new FileOutputStream(settingsFile), "");
			}
			loadSettings = new Properties();
			loadSettings.loadFromXML(new FileInputStream(settingsFile));
			getSettings();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Object[] getSettings() {
		loadSettings = new Properties();
		try {
			loadSettings.loadFromXML(new FileInputStream(settingsFile));
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Object height = Integer.parseInt(loadSettings.getProperty("height"));
		Object width = Integer.parseInt(loadSettings.getProperty("width"));
		Object port = Integer.parseInt(loadSettings.getProperty("port"));
		Object ftpState = Boolean.parseBoolean(loadSettings
				.getProperty("ftpState"));
		Object ftpIp = loadSettings.getProperty("ftpIp");
		Object ftpRemoteIp = loadSettings.getProperty("ftpRemoteIp");
		Object ftpUsername = loadSettings.getProperty("ftpUsername");
		Object ftpPassword = loadSettings.getProperty("ftpPassword");
		Object[] obj = new Object[] { height, width, port, ftpState, ftpIp,
				ftpRemoteIp, ftpUsername, ftpPassword };
		return obj;
	}

	public void setSettings(Object[] obj) {
		for (Object object : obj) {
			System.out.println("Settings:" + object);
		}
		saveProps = new Properties();
		saveProps.setProperty("height", obj[0].toString());
		saveProps.setProperty("width", obj[1].toString());
		saveProps.setProperty("port", obj[2].toString());
		saveProps.setProperty("ftpState", obj[3].toString());
		saveProps.setProperty("ftpIp", obj[4].toString());
		saveProps.setProperty("ftpRemoteIp", obj[5].toString());
		saveProps.setProperty("ftpUsername", obj[6].toString());
		saveProps.setProperty("ftpPassword", obj[7].toString());
		try {
			saveProps.storeToXML(new FileOutputStream(settingsFile), "");
		} catch (IOException e) {
			System.out.println("IOEXCEPTION");
			e.printStackTrace();
		}
	}
}
