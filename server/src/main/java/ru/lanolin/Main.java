package ru.lanolin;

import ru.lanolin.server.Server;
import ru.lanolin.util.ConfigApplication;

public class Main {

	public static boolean isDebug;

	static{
		ConfigApplication.getInstance().load();
		isDebug = ConfigApplication.getInstance().getBooleanProperty("debug_mode");
	}

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(Main::saveAndClose));
		Server.getInstance().start();
	}

	/**
	 * Метод, срабатывающий при закрытии приложения. Последний рывок.
	 */
	private static void saveAndClose(){
		System.out.println("Завершение работы сервера");
		//TODO: Save DB file
		Server server = Server.getInstance();
		if (server.isAlive() && !server.isInterrupted())
			server.interrupt();
	}

}
