package ru.lanolin;

import ru.lanolin.messages.UserMessages;
import ru.lanolin.server.Server;
import ru.lanolin.util.ConfigApplication;
import ru.lanolin.util.XML;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Main {

	public static boolean isDebug;
	public static XML xml;

	static{
		ConfigApplication.getInstance().load();
		isDebug = ConfigApplication.getInstance().getBooleanProperty("debug_mode");
	}

	public static void main(String[] args){
		Runtime.getRuntime().addShutdownHook(new Thread(Main::saveAndClose));
		xml = new XML(ConfigApplication.getInstance().getStringProperty("date_xml_file"));
		try {
			List<UserMessages> saves = xml.readXMLFile();
			if(Objects.nonNull(saves))
				UserMessages.STORAGE.addAll(saves);
		}catch (IOException | XMLStreamException ex){
			ex.printStackTrace();
		}

		Server.getInstance().start();

	}

	private static void saveAndClose(){
		System.out.println("Завершение работы сервера");
		Server server = Server.getInstance();
		if (server != null && server.isConnect()) server.stop();
		try {
			xml.writeDocument(UserMessages.STORAGE);
		} catch (FileNotFoundException e) { e.printStackTrace(); }
	}

}
