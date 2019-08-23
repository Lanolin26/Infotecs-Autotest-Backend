package ru.lanolin.client.menu;

import lombok.Getter;
import ru.lanolin.client.Client;
import ru.lanolin.util.Message;
import ru.lanolin.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Scanner;

import static ru.lanolin.Main.isDebug;

public class Menu {
	private MenuItems activeMenu;
	private BufferedReader console;

	private boolean interactive = true;

	@Getter private String login;

	public Menu() {
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

		MenuItems mainMenuItems = new MenuItems("Main");
		mainMenuItems.putAction("Ввести новое сообщение", this::enterNewMessage);
		mainMenuItems.putAction("Показать список своих сообщений", this::showMyMessage);
		mainMenuItems.putAction("Показать список всех сообщений", this::showAllMessage);
		mainMenuItems.putAction("Удалить свое сообщение", this::deleteMyMessage);
//		mainMenuItems.putAction("Upload file", () -> {});
//		mainMenuItems.putAction("Download file", () -> {});
		mainMenuItems.putAction("Выход из сессии", this::exitSession);

		activateMenu(mainMenuItems);
	}

	private void activateMenu(MenuItems newMenuItems) {
		if(activeMenu != newMenuItems)
			activeMenu = newMenuItems;

		while (interactive) {
			if(Objects.isNull(login)){
				System.out.print("Введите логин: ");
				String input = readText();
				if(input == null) continue;

				if(input.length() > 4 || input.length() == 0 ||
					!input.matches("^[a-z]{1,4}$")){
					System.err.println("Введен недопустимый логин");
				}else{
					login = input;
				}
			}else{
				System.out.println(newMenuItems.generateText());
				System.out.print("Введите действие: ");
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
		System.out.println("Введите новое сообщение в формате JSON. В конце напишите EOF: ");
		StringBuilder b = new StringBuilder();
		String strLine = null;
		while (!TERMINATOR_STRING.equals(strLine)) {
			strLine = readText();
			if(strLine == null) break;
			b.append(strLine.trim());
		}

		if(Utils.parseJSON(b.toString()) != null) {
			Message m = new Message(
					login,
					Message.Type.NEW_MESSAGE,
					b.toString()
			);
			Client.getInstance().getWriter().sendMessage(m);
			System.out.println("Отправлено");
		}
	}

	private void showMyMessage(){
		//Get Into Server Messages
	}

	private void showAllMessage() {
	}

	private void deleteMyMessage(){
		//Deletes Messagess
	}

	private void exitSession(){
		login = null;
		System.out.println("Выход из сессии");
	}
}
