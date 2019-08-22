package ru.lanolin.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
			System.err.println("Внимание!!! Введен неверный формат JSON строки. Исправьте или не продолжайте работать.");
		}
		return result;
	}

	/**
	 * Конвертирует сериализуемый объект в {@link ByteBuffer} для последующей отправки через {@link java.net.Socket}
	 *
	 * @param obj {@link Object} {@code implements {@link java.io.Serializable}}
	 * @return {@code ByteBuffer} запоненный преобразованным касов дя отправки
	 * @throws IOException Возникает, если невозможно преобразовать класс в {@link ByteBuffer}
	 */
	public static ByteBuffer convertObject2Buffer(Object obj) throws IOException {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream outputStreamWriter = new ObjectOutputStream(arrayOutputStream);

		outputStreamWriter.writeObject(obj);
		outputStreamWriter.flush();
		ByteBuffer b = ByteBuffer.wrap(arrayOutputStream.toByteArray());
		outputStreamWriter.close();
		arrayOutputStream.close();

		return b;
	}

}
