package base;

public class Image {
	public Image() {

	}

	public void runClient(String ftpServerIP, String ftpServerUsername,
			String ftpServerPassword) {
		new Thread(new Client(ftpServerIP, ftpServerUsername, ftpServerPassword)).start();
	}

	public void checkInputFromClient(String input) {
	}
}
