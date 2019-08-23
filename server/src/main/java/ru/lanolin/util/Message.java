package ru.lanolin.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString(of = {"login", "type", "message"})
@NoArgsConstructor
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
	}

	private String login;
	private Type type;
	private Object message;

	public Message(String login, Type type, Object message) {
		this.login = login;
		this.type = type;
		this.message = message;
	}

	public void setCommandObject(Type command, Object text) {
		this.setType(command);
		this.setMessage(text);
	}

	@Override
	public Message clone() throws CloneNotSupportedException {
		return (Message) super.clone();
	}
}