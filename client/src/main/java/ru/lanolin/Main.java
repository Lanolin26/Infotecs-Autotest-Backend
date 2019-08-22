package ru.lanolin;

import ru.lanolin.client.Client;
import ru.lanolin.util.ConfigApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class Main {

	public static boolean isDebug;

	static{
		ConfigApplication.getInstance().load();
		isDebug = ConfigApplication.getInstance().getBooleanProperty("debug_mode");
	}

	public static void main(String[] args) {
		Thread.currentThread().setName("[Main]");

		Client.getInstance().connect();

		try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
			boolean startInteract = true;

			while (startInteract) {
				String input;
				try {
					String raw_input = console.readLine();
					if (Objects.isNull(raw_input)) continue;
//					input = raw_input.split("\\s+", 2);
					input = raw_input;
				} catch (IOException e) {
					System.err.println("IO ошибка при чтении введенной строки");
					continue;
				}

				Client.getInstance().recieve(input);

			}
		} catch (IOException e) {
			System.err.println("IO ошибка");
		}
	}

}
