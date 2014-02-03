package base.util;

import java.util.ArrayList;

public class Queue {
	ArrayList<String> queue = new ArrayList<String>();

	public Queue() {
	}

	public void addToQueue(String message) {
		queue.add(message);
	}

	public String get(int index) {
		return queue.get(index) != null ? queue.get(index)
				: "Invalid" ;
	}

	public int len() {
		return queue.size();
	};
	public void remove(int index) {
		queue.remove(index);
	};
}
