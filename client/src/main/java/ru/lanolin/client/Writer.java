package ru.lanolin.client;

import ru.lanolin.util.Message;
import ru.lanolin.util.Utils;

import java.io.IOException;
import java.io.OutputStream;

public class Writer {
	private Client client;

	private Message msg = new Message();

	public Writer(Client client) {
		this.client = client;
	}

	public void sendMessage(Message.Type command, String message) {
		if (client.isConnected()) {
			OutputStream os;
			try {
				os = client.getOutputStream();
				msg.setCommandObject(command, message);
				sendToServer(os, msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendMessage(Message message) {
		if (client.isConnected()) {
			OutputStream os;
			try {
				os = client.getOutputStream();
				sendToServer(os, message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendToServer(OutputStream os, Message msg) throws IOException {
		client.getLock().lock();
		os.write(Utils.convertMessage2ByteArray(msg));
		os.flush();
		client.getLock().unlock();
	}
}
