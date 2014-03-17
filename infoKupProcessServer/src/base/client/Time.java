package base.client;

public class Time implements Runnable {
	private long time = 0;
	private long lastConnectionTime = 0;
	private Client client;
	private boolean dead = false;

	public Time(Client c) {
		this.client = c;
	}

	@Override
	public void run() {
		while (!dead) {
			time += 100;
			lastConnectionTime += 100;
			client.updateLastConnectionTime(lastConnectionTime);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public long getTime() {
		return time;
	}

	public long getLastConnectionTime() {
		return lastConnectionTime;
	}

	public void resetLastConnectionTime() {
		lastConnectionTime = 0;
	}

	public void die() {
		dead = true;
	}
}