package ru.lanolin.client;

import ru.lanolin.Main;
import ru.lanolin.client.threads.HeartBeatThread;
import ru.lanolin.util.ConfigApplication;
import ru.lanolin.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ru.lanolin.Main.isDebug;

public class Client {

	private String host;
	private final int port;
	private Socket socket;

	private final int maxTryingToReconnect = 5;
	private int nowTryingToReconnect = 0;

	private final Lock pause = new ReentrantLock();

	private HeartBeatThread heartbeatThread;
//	private final ThreadReader threadReader;

	private Writer writer;
	private Reader reader;

	public Client(){
		ConfigApplication config = ConfigApplication.getInstance();
		host = config.getStringProperty("hostname");
		port = config.getIntegerProperty("port_main");
		createVar();
	}

	public Client(String host){
		ConfigApplication config = ConfigApplication.getInstance();
		port = config.getIntegerProperty("port_main");
		this.host = host;
		createVar();
	}

	private void createVar(){
		this.heartbeatThread = new HeartBeatThread(this);
		this.reader = new Reader(this);
		this.writer = new Writer(this);
	}

	public void connect() {
		try {
			socket = new Socket(host, port);
			heartbeatThread.start();
			Utils.printlnf("Успешно подключено");
			Main.menu.start();
		} catch (IOException e) {
			if(isDebug) e.printStackTrace();
			System.err.println("Внимание!! Невозможно подключиться к серверу. " + e.getLocalizedMessage());
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

	public Writer getWriter() {
		return writer;
	}

	public Reader getReader() {
		return reader;
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
		try {
			socket.close();
		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
		}
		System.exit(0);
	}
}
