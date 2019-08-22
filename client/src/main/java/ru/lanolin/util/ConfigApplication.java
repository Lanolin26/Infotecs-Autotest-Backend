package ru.lanolin.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class ConfigApplication {

	private final Properties properties;

	private static ConfigApplication instance;

	public static ConfigApplication getInstance() {
		return instance == null ? instance = new ConfigApplication() : instance;
	}

	private ConfigApplication(){
		properties = new Properties();
	}

	public void load(){
		InputStream propertyFile = ru.lanolin.Main.class.getClassLoader().getResourceAsStream("ru/lanolin/application.properties");
		try {
			properties.load(propertyFile);
		} catch (IOException e) {
			System.err.println("Ошибка при чтении конфигурационных файлов");
			e.printStackTrace();
			System.exit(1);
		}
	}

	public int getIntegerProperty(String key){
		return getIntegerProperty(key, 0);
	}

	public int getIntegerProperty(String key, int defaultValue){
		String value = properties.getProperty(key);
		return Objects.isNull(value) ? defaultValue : Integer.parseInt(value);
	}

	public String getStringProperty(String key){
		return getStringProperty(key, "");
	}

	public String getStringProperty(String key, String defaultValue){
		String value = properties.getProperty(key);
		return Objects.isNull(value) ? defaultValue : value;
	}

	public boolean getBooleanProperty(String key){
		return getBooleanProperty(key, false);
	}

	public boolean getBooleanProperty(String key, boolean defaultValue){
		String value = properties.getProperty(key);
		return Objects.isNull(value) ? defaultValue : Boolean.getBoolean(value);
	}

}
