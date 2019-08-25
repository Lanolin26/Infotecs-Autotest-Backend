package ru.lanolin.client.threads;


import ru.lanolin.client.Client;
import ru.lanolin.util.Message;
import ru.lanolin.util.UserMessages;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class ThreadReader extends Thread {

	private Client client;

	public ThreadReader(Client client) {
		super("[Reader]");
		this.client = client;
	}

	@Override
	public void run() {
		super.run();
		InputStream is = null;
		while (client.isConnected() && !isInterrupted()) {
			try {
				InputStream ne = client.getInputStream();
				if (ne != is) is = ne;
			} catch (IOException e) {
				continue;
			}
			if (Objects.nonNull(is)) try {
				int ava = is.available();
				if (ava > 0) {
					byte[] inputData = new byte[ava];
					is.read(inputData);
					ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(inputData);
					ObjectInputStream objectInputStream = new ObjectInputStream(arrayInputStream);
					Message msg = (Message) objectInputStream.readObject();
					if (Objects.nonNull(msg)) msgParser(msg);
				}
			} catch (EOFException eoef) {
				System.err.println("Отвал сервера");
			} catch (IOException | ClassNotFoundException sexep) {
				System.err.println("Ошибка при чтении. (" + sexep.getLocalizedMessage() + ")");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void msgParser(Message msg) {
		System.out.println(msg);

		switch (msg.getType()){
			case ANSWER:
				System.out.println(msg.getMessage());
				break;
			case ERROR:
				System.err.println(msg.getMessage());
				break;
			case ARRAY:
				List<UserMessages> message = (ArrayList<UserMessages>) msg.getMessage();
				ListIterator it = message.listIterator();
				while (it.hasNext()){
					System.out.println(it.nextIndex() + ": " + it.next());
				}
				break;
		}

	}

}
