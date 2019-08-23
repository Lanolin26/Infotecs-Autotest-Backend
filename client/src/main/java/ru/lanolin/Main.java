package ru.lanolin;

import ru.lanolin.client.Client;
import ru.lanolin.client.menu.Menu;
import ru.lanolin.util.ConfigApplication;

import java.io.IOException;

public class Main {

	public static boolean isDebug;

	static{
		ConfigApplication.getInstance().load();
		isDebug = ConfigApplication.getInstance().getBooleanProperty("debug_mode");
	}

	public static void main(String[] args) throws IOException {
		Thread.currentThread().setName("[Main]");

		Client.getInstance().connect();

		new Menu();
	}

}
