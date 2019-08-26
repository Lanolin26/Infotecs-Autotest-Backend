package ru.lanolin;

import ru.lanolin.client.Client;
import ru.lanolin.menu.Menu;
import ru.lanolin.util.ConfigApplication;
import ru.lanolin.util.Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

	public static boolean isDebug;
	public static BufferedReader console;

	public static Menu menu;
	public static Client client;

	static{
		ConfigApplication.getInstance().load();
		isDebug = ConfigApplication.getInstance().getBooleanProperty("debug_mode");
	}

	public static void main(String[] args) {
		Thread.currentThread().setName("[Main]");

		if(args.length == 0){
			Utils.printlnf("Внимание. Попытка подключения к localhost серверу. " +
					"Чтобы подлючиться к другому введите после команды запуска адресс подключения");
			client = new Client();
		}else{
			client = new Client(args[0]);
		}

		console = new BufferedReader(new InputStreamReader(System.in));
		menu = new Menu();

		client.connect();
	}

}
