package ru.lanolin.util;

import ru.lanolin.messages.Message;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Map;

public class Utils {

	/**
	 * Скриптовый движок, используется для преобразований {@code JSON} строк в {@link Map}
	 */
	private static final ScriptEngine JAVASCRIPT = new ScriptEngineManager().getEngineByName("javascript");

	/**
	 * Метод, который преобразует {@code JSON} строку в {@link Map}
	 *
	 * @param element {@code JSON} строку
	 * @return {@link Map}, в котором содержатся ключ-значения из JSON
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseJSON(String element) {
		Map<String, Object> result = null;
		try {
			result = (Map<String, Object>) JAVASCRIPT.eval("Java.asJSONCompatible(" + element + ")");
		} catch (Exception e) {
			System.err.println("Внимание!!! Введен неверный формат JSON строки. Повторите ввод");
		}
		return result;
	}

	public static ByteBuffer convertObject2Buffer(Object obj) throws IOException {
		try(ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream outputStreamWriter = new ObjectOutputStream(arrayOutputStream)) {

			outputStreamWriter.writeObject(obj);
			outputStreamWriter.flush();
			return ByteBuffer.wrap(arrayOutputStream.toByteArray());
		}
	}

	public static Message convertBuffer2Message(ByteBuffer byteBuffer) throws IOException, ClassNotFoundException {
		try(ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteBuffer.array());
			ObjectInputStream objectInputStream = new ObjectInputStream(arrayInputStream)){
			return (Message) objectInputStream.readObject();
		}
	}

}
