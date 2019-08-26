package ru.lanolin.messages;

import java.io.Serializable;

public class Message implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	public enum Type implements Serializable{
		ARRAY,
		PING,
		ANSWER,
		ERROR,
		NEW_MESSAGE,
		SHOW_MY_MESSAGE,
		SHOW_ALL_MESSAGE,
		DELETE
	}

	private String login;
	private Type type;
	private Object message;

	public Message(String login, Type type, Object message) {
		this.login = login;
		this.type = type;
		this.message = message;
	}

	public Message() {  }

	public void setCommandObject(Type command, Object text) {
		this.setType(command);
		this.setMessage(text);
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Message{" + "login='" + login + '\'' + ", type=" + type + ", message=" + message + '}';
	}

	@Override
	public Message clone() throws CloneNotSupportedException {
		return (Message) super.clone();
	}
}