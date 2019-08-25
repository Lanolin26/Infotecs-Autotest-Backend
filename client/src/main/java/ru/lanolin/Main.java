package ru.lanolin;

import ru.lanolin.client.Client;
import ru.lanolin.client.menu.Menu;
import ru.lanolin.util.ConfigApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	public static boolean isDebug;
	public static BufferedReader console;

	public static Menu menu;

	static{
		ConfigApplication.getInstance().load();
		isDebug = ConfigApplication.getInstance().getBooleanProperty("debug_mode");
	}

	public static void main(String[] args) throws IOException {
		Thread.currentThread().setName("[Main]");
		console = new BufferedReader(new InputStreamReader(System.in));
		menu = new Menu();

		Client.getInstance().connect();
	}

}
