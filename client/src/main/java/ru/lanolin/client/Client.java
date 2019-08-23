package ru.lanolin.client;

import javafx.application.Platform;
import lombok.Getter;
import ru.lanolin.client.threads.HeartBeatThread;
import ru.lanolin.client.threads.ThreadReader;
import ru.lanolin.util.ConfigApplication;
import ru.lanolin.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ru.lanolin.Main.isDebug;

public class Client {

	private static Client instance;
	public static Client getInstance() {
		return Objects.isNull(instance) ? instance = new Client() : instance;
	}

	private final String host;
	private final int port;
	private Socket socket;

	private final int maxTryingToReconnect = 5;
	private int nowTryingToReconnect = 0;

	private final Lock pause = new ReentrantLock();

	private final HeartBeatThread heartbeatThread;
	private final ThreadReader threadReader;

	@Getter
	private final Writer writer;

	private Client(){
		ConfigApplication config = ConfigApplication.getInstance();
		host = config.getStringProperty("hostname");
		port = config.getIntegerProperty("port_main");

		this.heartbeatThread = new HeartBeatThread(this);
		this.threadReader = new ThreadReader(this);
		this.writer = new Writer(this);
	}

	public void connect() throws IOException {
		try {
			socket = new Socket(host, port);
			heartbeatThread.start();
			threadReader.start();
			System.out.println("Успешно подключено");
		} catch (IOException e) {
			throw e;
		}
	}

	public void connect(String server, int port) {
		getLock().lock();
		try {
			socket = new Socket(server, port);
			nowTryingToReconnect = 0;
			getLock().unlock();
		} catch (IOException e) {
			if (nowTryingToReconnect == 0) System.out.println("\n");
			nowTryingToReconnect++;
			System.err.println((char) 27 + "[2A" + heartbeatThread.getName() + ": Server is offline\n" + "Попытка " + nowTryingToReconnect + ": " + e.getLocalizedMessage());
			getLock().unlock();
			if (nowTryingToReconnect >= maxTryingToReconnect) {
				System.err.println("В соеденении отказано. Попытки кончились");
				shutdown();
			}
		}
	}

	public String getHostname() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}

	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}

	public Lock getLock() {
		return pause;
	}

	public void shutdown() {
		System.out.println("Прекращается работа");

		heartbeatThread.interrupt();
		threadReader.interrupt();
		try {
			socket.close();
		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
		}
	}
}
