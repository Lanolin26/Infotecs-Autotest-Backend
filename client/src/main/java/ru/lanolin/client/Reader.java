package ru.lanolin.client;

import ru.lanolin.messages.Message;
import ru.lanolin.messages.UserMessages;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class Reader {

	private Client client;

	public Reader(Client client) {
		this.client = client;
	}

	public void readMessage(){
		InputStream is = null;
		try {
			is = client.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
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
			sexep.printStackTrace();
			System.err.println("Ошибка при чтении. (" + sexep.getLocalizedMessage() + ")");
		}
	}

	@SuppressWarnings("unchecked")
	private void msgParser(Message msg) {
		System.out.println(msg);

		switch (msg.getType()){
			case ANSWER:
				System.out.println(">>" + msg.getMessage());
				break;
			case ERROR:
				System.err.println(">>" + msg.getMessage());
				break;
			case ARRAY:
				List<UserMessages> message = (ArrayList<UserMessages>) msg.getMessage();
				ListIterator it = message.listIterator();
				System.out.println("Index | message");
				while (it.hasNext()){
					System.out.println(it.nextIndex() + ": " + it.next());
				}
				break;
		}
		System.out.flush();
	}

}
