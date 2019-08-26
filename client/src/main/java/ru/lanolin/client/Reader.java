package ru.lanolin.client;

import ru.lanolin.messages.Message;
import ru.lanolin.messages.UserMessages;
import ru.lanolin.util.Utils;

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
		switch (msg.getType()){
			case ANSWER:
				Utils.printlnf(">>Server: " + msg.getMessage());
				break;
			case ERROR:
				System.err.println(">>Server Error" + msg.getMessage());
				break;
			case ARRAY:
				List<UserMessages> message = (ArrayList<UserMessages>) msg.getMessage();
				ListIterator<UserMessages> it = message.listIterator();
				Utils.printlnf("  ID  | Login |          Date           | Message");
				while (it.hasNext()){
					int ind = it.nextIndex();
					UserMessages um = it.next();
					Utils.printlnf(String.format(" %4d | %5s | %19s | %s", ind, um.getLogin(), um.getDate(), um.getMessage()));
				}
				break;
		}
		Utils.printlnf("");
	}

}
