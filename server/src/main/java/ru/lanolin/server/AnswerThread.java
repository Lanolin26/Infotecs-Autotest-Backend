package ru.lanolin.server;

import ru.lanolin.Main;
import ru.lanolin.messages.Message;
import ru.lanolin.messages.UserMessages;
import ru.lanolin.util.Utils;

import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class AnswerThread extends Thread {

	private SocketChannel client;
	private Message message;

	public AnswerThread(SocketChannel client, final Message message) {
		super("[ServerWriterAnswer]");
		this.client = client;
		this.message = message;
	}

	@Override
	public void run() {
		super.run();
		try{
			Message answer = new Message("server", null, null);
			switch (message.getType()){
				case NEW_MESSAGE:
					UserMessages newMessages = new UserMessages(
							message.getLogin(),
							LocalDateTime.now(),
							(String)message.getMessage()
					);
					UserMessages.STORAGE.add(newMessages);
					answer.setCommandObject(Message.Type.ANSWER, "Ok");
					break;
				case SHOW_MY_MESSAGE:
					List<UserMessages> filtered = UserMessages.STORAGE.stream()
							.filter(userMessages -> userMessages.getLogin().equals(message.getLogin()))
							.sorted(Comparator.comparing(UserMessages::getDate))
							.collect(Collectors.toList());
					answer.setCommandObject(Message.Type.ARRAY, filtered);
					break;
				case SHOW_ALL_MESSAGE:
					boolean sortType = Boolean.getBoolean(message.getMessage().toString());
					List<UserMessages> sorted = UserMessages.STORAGE.stream()
							.sorted((o1, o2) ->
									sortType ?	o1.getLogin().compareTo(o2.getLogin()) :
											o1.getDate().compareTo(o2.getDate())
							).collect(Collectors.toList());
					answer.setCommandObject(Message.Type.ARRAY, sorted);
					break;
				case DELETE:
					ListIterator it = UserMessages.STORAGE.listIterator();
					int id = Integer.parseInt(message.getMessage().toString());
					boolean success = false;
					while (it.hasNext()){
						if(it.nextIndex() == id){
							it.remove();
							success = true;
							break;
						}
						it.next();
					}
					if(success)
						answer.setCommandObject(Message.Type.ANSWER, "Успешно удалено");
					else
						answer.setCommandObject(Message.Type.ERROR, "Не удалено");
					break;
				default:
					answer.setCommandObject(Message.Type.ERROR, "Неизвестная комманда");
					break;
			}

			System.out.println("Message send: " + answer);
			client.write(Utils.convertObject2Buffer(answer));
		}catch (Exception e){
			if (Main.isDebug) e.printStackTrace();
		}

	}

}
