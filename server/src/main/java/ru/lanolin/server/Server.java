package ru.lanolin.server;

import ru.lanolin.util.ConfigApplication;
import ru.lanolin.messages.Message;
import ru.lanolin.util.Utils;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

import static ru.lanolin.Main.isDebug;

public class Server {

	private static Server server;

	public static Server getInstance() {
		return server == null ? server = new Server() : server;
	}

	private final InetSocketAddress socketAddress;
//	private final InetSocketAddress socketFile;

	private Selector selector;
	private ServerSocketChannel socketChannel;

	private Server() {
		socketAddress = new InetSocketAddress(
				ConfigApplication.getInstance().getIntegerProperty("port_main", 45777));
		try {
			selector = Selector.open();
			socketChannel = ServerSocketChannel.open();
		} catch (IOException e) {
			if (isDebug) e.printStackTrace();
			throw new RuntimeException("Ошибка при открытии сервера");
		}

		try {
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


	public void start() {
		try {
			System.out.println("Сервер запущен");
			while (selector.isOpen() && socketChannel.isOpen()) {
				selector.select();
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();

				while (selectionKeyIterator.hasNext() && selector.isOpen() && socketChannel.isOpen()) {
					SelectionKey myKey = selectionKeyIterator.next();

					if (!myKey.isValid()) { continue; }

					if (myKey.isAcceptable()) {
						SocketChannel socketClient = socketChannel.accept();
						socketClient.configureBlocking(false);
						socketClient.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
						socketClient.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
						System.out.println("Connection Accepted: " + socketClient.getRemoteAddress() + "\n");
//					} else if(myKey.isConnectable()){
//						System.out.println("Connect");
					} else if (myKey.isReadable()) {
						SocketChannel socketChannel = (SocketChannel) myKey.channel();
//						System.out.println("Readebl" + socketChannel/);
//						new AnswerThread(selector, socketChannel).start();
						readInSocketClient(socketChannel);
					}
					selectionKeyIterator.remove();
				}
			}
		} catch (Exception e) {
			if (isDebug) e.printStackTrace();
		}
	}

	private void readInSocketClient(SocketChannel socketClient) throws ClosedChannelException {
		try {
			ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
			socketClient.read(byteBuffer);

			Message mResult = Utils.convertBuffer2Message(byteBuffer);

			if (mResult == null) {
				socketClient.register(selector, SelectionKey.OP_CONNECT);
				return;
			} else if (mResult.getType() == Message.Type.PING) {
				return;
			}

			System.out.println("Message received: " + mResult);

			if(mResult.getType() != null) {
				AnswerThread answerThread = new AnswerThread(socketClient, mResult.clone());
				answerThread.start();
			}
		} catch (StreamCorruptedException ex){

			socketClient.register(selector, SelectionKey.OP_CONNECT);
		} catch (IOException | CloneNotSupportedException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		try {
			if (socketChannel.isOpen()) socketChannel.close();
			if (selector.isOpen()) selector.close();
		} catch (IOException e) {
			if (isDebug) e.printStackTrace();
		}
	}
}
