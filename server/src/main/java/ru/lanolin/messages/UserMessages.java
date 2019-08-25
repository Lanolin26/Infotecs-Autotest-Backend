package ru.lanolin.messages;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMessages implements Serializable {

	private static final long serialVersionUID = 1685316L;

	public static transient List<UserMessages> STORAGE =
			Collections.synchronizedList(new ArrayList<>());

	private String login;
	private LocalDateTime date;
	private String message;

	@Override
	public String toString() {
		return String.format("<message><login>%s</login><time>%s</time><text>%s</text></message>", login, date, message);
	}
}
