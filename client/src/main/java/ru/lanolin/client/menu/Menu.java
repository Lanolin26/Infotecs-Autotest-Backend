package ru.lanolin.client.menu;

import ru.lanolin.messages.Message;
import ru.lanolin.util.Utils;

import java.io.IOException;
import java.util.Objects;

import static ru.lanolin.Main.*;

public class Menu {

	private MenuItems activeMenu;

	private boolean interactive = true;

	private MenuItems mainMenuItems, sortType;

	private String login;

	public Menu() {
		sortType = new MenuItems("Сортировка");
		sortType.putAction("Сортировка оп логину", () -> this.showAllMessage(true));
		sortType.putAction("Сортировка оп дате", () -> this.showAllMessage(false));

		mainMenuItems = new MenuItems("Меню");
		mainMenuItems.putAction("Ввести новое сообщение", this::enterNewMessage);
		mainMenuItems.putAction("Показать список своих сообщений", this::showMyMessage);
		mainMenuItems.putAction("Показать список всех сообщений", () -> this.activateMenu(sortType));
		mainMenuItems.putAction("Удалить свое сообщение", this::deleteMyMessage);
//		mainMenuItems.putAction("Upload file", () -> {});
//		mainMenuItems.putAction("Download file", () -> {});
		mainMenuItems.putAction("Выход из сессии", this::exitSession);
	}

	public void start(){
		activateMenu(mainMenuItems);
	}

	private void activateMenu(MenuItems newMenuItems) {
		if(activeMenu != newMenuItems)
			activeMenu = newMenuItems;

		while (interactive) {
			if(Objects.isNull(login)){
				Utils.printf("Введите логин: ");
				String input = readText();
				if(input == null) continue;

				if(input.length() > 4 || input.length() == 0 ||
					!input.matches("^[a-z]{1,4}$")){
					System.err.println("Введен недопустимый логин");
				}else{
					login = input;
				}
				Utils.printlnf("");
			}else{
				Utils.printlnf(newMenuItems.generateText());
				Utils.printlnf("");
				Utils.printf("Введите действие: ");
				try {
					String input = readText();
					if(input == null) continue;
					int actionNumber = Integer.parseInt(input);
					activeMenu.executeAction(actionNumber);
				}catch (NumberFormatException ime){
					System.err.println("Введен недопустимый символ");
				}
			}
		}
	}

	private String readText(){
		try {
			String raw_input = console.readLine();
			if (Objects.isNull(raw_input)) return null;
			return raw_input;
		} catch (IOException e) {
			if (isDebug) e.printStackTrace();
			System.err.println("IO ошибка при чтении введенной строки");
			return null;
		}
	}


	private void enterNewMessage(){
		String TERMINATOR_STRING = "EOF";
		Utils.printlnf("Введите новое сообщение в формате JSON. В конце напишите EOF: ");
		StringBuilder b = new StringBuilder();
		String strLine;
		while (true) {
			strLine = readText();
			if(strLine == null || TERMINATOR_STRING.equals(strLine)) break;
			b.append(strLine.trim());
		}

		if(Utils.parseJSON(b.toString()) != null) {
			Message m = new Message(login, Message.Type.NEW_MESSAGE, b.toString());
			sendAndReceiveMessage(m);
		}
	}

	private void showMyMessage(){
		Message m = new Message(login, Message.Type.SHOW_MY_MESSAGE, null);
		sendAndReceiveMessage(m);
	}

	private void showAllMessage(boolean isLoginSort) {
		Message m = new Message(login, Message.Type.SHOW_ALL_MESSAGE, isLoginSort);
		sendAndReceiveMessage(m);
		activateMenu(mainMenuItems);
	}

	private void deleteMyMessage(){
		Utils.printf("Введите идентификатор своего сообщения для удаления: ");
		String ids = readText();
		int id = -1;
		try{
			id = Integer.parseInt(ids);
			if(id == -1) return;
		}catch (NumberFormatException nfe){
			Utils.printlnf("Введены не числовые симолы");
			return;
		}catch (NullPointerException npe){
			Utils.printlnf("Ошибка вводы");
			return;
		}

		Message m = new Message(login, Message.Type.DELETE, id);
		sendAndReceiveMessage(m);
	}

	private void exitSession(){
		login = null;
		Utils.printlnf("Выход из сессии");
	}

	private void sendAndReceiveMessage(Message m){
		client.getWriter().sendMessage(m);
		Utils.printlnf("");
		client.getReader().readMessage();
	}
}
