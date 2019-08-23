package ru.lanolin.client.threads;

import ru.lanolin.client.Client;
import ru.lanolin.util.Message;
import ru.lanolin.util.Utils;

import java.io.IOException;

public class HeartBeatThread extends Thread {

	private boolean tryToReconnect = true;
	private Message ping = new Message();
	private long heartbeatDelayMillis = 2000;
	private long deltaSleep = 50;

	private Client client;

	public HeartBeatThread(Client client) {
		super("[HeartBeat]");
		this.client = client;
	}

	@Override
	public void run() {
		super.run();
		while (tryToReconnect) {
			try {
				ping = new Message("", Message.Type.PING, null);
				client.getLock().lock();
				client.getOutputStream().write(Utils.convertMessage2ByteArray(ping));
				client.getLock().unlock();

				long currentTime = 0L;
				while (currentTime < heartbeatDelayMillis && !isInterrupted()) {
					currentTime += deltaSleep;
					Thread.sleep(deltaSleep);
				}

			} catch (InterruptedException e) {
				tryToReconnect = false;
			} catch (IOException e) {
				client.connect(client.getHostname(), client.getPort());
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
				}
			}
		}
	}

	@Override
	public void interrupt() {
		this.tryToReconnect = false;
		super.interrupt();
	}
}
