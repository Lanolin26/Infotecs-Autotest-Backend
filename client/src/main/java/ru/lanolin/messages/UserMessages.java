package ru.lanolin.messages;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UserMessages implements Serializable {

	private static final long serialVersionUID = 1685316L;

//	public static transient List<UserMessages> STORAGE =
//			Collections.synchronizedList(new ArrayList<>());

	private String login;
	private LocalDateTime date;
	private String message;

	public UserMessages() { }

	public UserMessages(String login, LocalDateTime date, String message) {
		this.login = login;
		this.date = date;
		this.message = message;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return String.format("<message><login>%s</login><time>%s</time><text>%s</text></message>", login, date, message);
	}
}
