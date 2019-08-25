package ru.lanolin.util;

import ru.lanolin.messages.UserMessages;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class XML {

	private static final String StartDocument = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

	private final File xmlFile;

	public XML(File xmlFile) {
		this.xmlFile = xmlFile;
		if(!xmlFile.exists()) {
			try {
				xmlFile.createNewFile();
				writeDocument(new ArrayList<>());
			} catch (IOException e) { e.printStackTrace(); }
		}
	}

	public XML(String xmlFile) {
		this(new File(xmlFile));
	}

	@SuppressWarnings("ConstantConditions")
	public List<UserMessages> readXMLFile() throws IOException, XMLStreamException {
		List<UserMessages> userMessagesList = null;
		UserMessages userMessages = null;
		String text = null;

		FileReader br = new FileReader(xmlFile);
		XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(br);

		while (reader.hasNext()) {
			int event = reader.next();

			switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					if ("storage".equals(reader.getLocalName()))
						userMessagesList = new ArrayList<>();
					if ("message".equals(reader.getLocalName()))
						userMessages = new UserMessages();
					break;

				case XMLStreamConstants.CHARACTERS:
					text = reader.getText().trim();
					break;

				case XMLStreamConstants.END_ELEMENT:
					switch (reader.getLocalName()) {
						case "message":
							userMessagesList.add(userMessages);
							userMessages = null;
							break;
						case "login":
							userMessages.setLogin(text);
							break;
						case "time":
							userMessages.setDate(LocalDateTime.parse(text));
							break;
						case "text":
							userMessages.setMessage(text);
							break;
					}
					break;
			}
		}

		reader.close();
		br.close();
		return userMessagesList;
	}

	public void writeDocument(List<UserMessages> list) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(xmlFile);
		pw.write(StartDocument);
		pw.append("<storage>\n");
		list.forEach(h -> pw.append("\t").append(h.toString()).append("\n"));
		pw.append("</storage>");
		pw.close();
	}
}
