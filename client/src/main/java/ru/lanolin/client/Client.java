package ru.lanolin.client;

import ru.lanolin.util.ConfigApplication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;

import static ru.lanolin.Main.isDebug;

public class Client {

	private static Client instance;
	public static Client getInstance() {
		return Objects.isNull(instance) ? instance = new Client() : instance;
	}

	private InetSocketAddress hostAddress;
	private SocketChannel client;

	private Client(){
		hostAddress = new InetSocketAddress(
				ConfigApplication.getInstance().getStringProperty("hostname"),
				ConfigApplication.getInstance().getIntegerProperty("port_main"));
	}

	public void connect(){
		try {
			client = SocketChannel.open(hostAddress);
			client.configureBlocking(false);

		} catch (IOException e) {
			if(isDebug) e.printStackTrace();
		}
	}

	public void recieve(String message){
		ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
		try {
			client.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
