package infoKupProcess;

import java.util.ArrayList;

public class Buffer {
	ArrayList<String[]> buffer = new ArrayList<String[]>();

	public Buffer() {
	}

	public void addToBuffer(String arg0, String arg1, String clientName) {
		buffer.add(new String[] { arg0, arg1, clientName });
	}

	public String[] get(int index) {
		return buffer.get(index) != null ? buffer.get(index)
				: new String[] { "Invalid" };
	}

	public int len() {
		return buffer.size();
	};
	public void remove(int index) {
		buffer.remove(index);
	};
}
