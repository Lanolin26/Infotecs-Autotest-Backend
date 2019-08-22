package ru.lanolin.server;

import ru.lanolin.util.ConfigApplication;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import static ru.lanolin.Main.isDebug;

public class Server extends Thread {

	private static Server server;

	public static Server getInstance() {
		return server == null ? server = new Server() : server;
	}

	private final InetSocketAddress socketAddress;
//	private final InetSocketAddress socketFile;

	private Selector selector;
	private ServerSocketChannel socketChannel;

	private Server() {
		super("[SERVER]");

		socketAddress = new InetSocketAddress(
				ConfigApplication.getInstance().getIntegerProperty("port_main", 45777));
		try {
			selector = Selector.open();

			socketChannel = ServerSocketChannel.open();
			socketChannel.bind(socketAddress);
			socketChannel.configureBlocking(false);
			socketChannel.register(selector, socketChannel.validOps(), null);
		} catch (BindException be) {
			System.err.println("Внимание! " + be.getLocalizedMessage());
			try {
				selector.close();
				socketChannel.close();
				System.exit(1);
			} catch (IOException e) { if (isDebug) e.printStackTrace(); }
		} catch (IOException e) { if (isDebug) e.printStackTrace(); }
	}

	public boolean isConnect() {
		return selector.isOpen() && socketChannel.isOpen();
	}

	@Override
	public void run() {
		super.run();
		try {
			System.out.println("Сервер запущен");
			while (isConnect() && !isInterrupted()) {
				selector.select();
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();

				while (selectionKeyIterator.hasNext() && isConnect()) {
					SelectionKey myKey = selectionKeyIterator.next();

					if (!myKey.isValid()) { continue; }

					if (myKey.isAcceptable()) {
						SocketChannel socketClient = socketChannel.accept();
						socketClient.configureBlocking(false);
						socketClient.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
						socketClient.register(selector, SelectionKey.OP_READ);
						System.out.println("Connection Accepted: " + socketClient.getRemoteAddress());
					} else if(myKey.isReadable()){
						SocketChannel sc = (SocketChannel) myKey.channel();
						ByteBuffer buffer = ByteBuffer.allocate(2048);

						int read = -1;
						try{
							read = sc.read(buffer);
						}catch (IOException ex1){
							myKey.cancel();
							sc.close();
							System.out.println("Forceful shutdown");
							continue;
						}

						if (read == -1) {
							System.out.println("Graceful shutdown");
							myKey.channel().close();
							myKey.cancel();
							continue;
						}

						System.out.println(sc.getRemoteAddress() + ": " + new String(buffer.array()).trim());

					} else if (myKey.isWritable()) {
						SocketChannel sc = (SocketChannel) myKey.channel();
//
					}
					selectionKeyIterator.remove();
				}
			}
		} catch (Exception e) {
			if (isDebug) e.printStackTrace();
		}
	}

	@Override
	public void interrupt() {
		try {
			if (socketChannel.isOpen()) socketChannel.close();
			if (selector.isOpen()) selector.close();
		} catch (IOException e) { if (isDebug) e.printStackTrace(); }
		super.interrupt();
	}

}
