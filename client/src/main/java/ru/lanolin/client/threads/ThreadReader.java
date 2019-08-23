package ru.lanolin.client.threads;


import ru.lanolin.client.Client;
import ru.lanolin.util.Message;

import java.io.*;
import java.util.Objects;

public class ThreadReader extends Thread {

	private Client client;

	public ThreadReader(Client client) {
		super("[Reader]");
		this.client = client;
	}

	@Override
	public void run() {
		super.run();
		InputStream is = null;
		while (client.isConnected() && !isInterrupted()) {
			try {
				InputStream ne = client.getInputStream();
				if (ne != is) is = ne;
			} catch (IOException e) {
				continue;
			}
			if (Objects.nonNull(is)) try {
				int ava = is.available();
				if (ava > 0) {
					byte[] inputData = new byte[ava];
					is.read(inputData);
					ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(inputData);
					ObjectInputStream objectInputStream = new ObjectInputStream(arrayInputStream);
					Message msg = (Message) objectInputStream.readObject();
					if (Objects.nonNull(msg)) msgParser(msg);
				}
			} catch (EOFException eoef) {
				System.err.println("Отвал сервера");
			} catch (IOException | ClassNotFoundException sexep) {
				System.err.println("Ошибка при чтении. (" + sexep.getLocalizedMessage() + ")");
			}
		}
	}

	private void msgParser(Message msg) {
		System.out.println(msg);

//		if (client.getHexPassword() == null || client.getCreator() == -1 || "".equals(client.getHexPassword())) {
//			switch (msg.getCommand()) {
//				case signin:
//					if (msg.getType() == Message.TypeMessage.StringOut) {
//						client.getFx().getRegResultController().setMessageTitle(I18N.createStringBinding("regRes.label.successReg"), (String) msg.getMessage());
//						Platform.runLater(() -> client.getFx().getRegAuthController().changeSceneRegistration());
//					} else if (msg.getType() == Message.TypeMessage.StringError) {
//						client.getFx().getRegResultController().setMessageTitle(I18N.createStringBinding("regRes.label.failReg"), (String) msg.getMessage());
//						Platform.runLater(() -> client.getFx().getRegAuthController().changeSceneRegistration());
//					}
//					break;
//				case login:
//					if (msg.getType() == Message.TypeMessage.StringOut) {
//						if (msg.getHash() != null && !msg.getHash().isEmpty() && msg.getCreator() != null && msg.getCreator() != -1) {
//							client.setCreator(msg.getCreator());
//							client.setHexPassword(msg.getHash());
//							Platform.runLater(() -> client.getFx().getRegAuthController().changeSceneSuccessAuth());
//							client.getWriter().sendMessage(Message.Command.start, "");
//						}
//					} else if (msg.getType() == Message.TypeMessage.StringError) {
//						client.getFx().getRegResultController().setMessageTitle(I18N.createStringBinding("regRes.label.failAuth"), (String) msg.getMessage());
//						Platform.runLater(() -> client.getFx().getRegAuthController().changeSceneRegistration());
//					}
//					break;
//				case logout:
//
//					break;
//			}
//		} else switch (msg.getType()) {
//			case StringOut:
//				if (msg.getCommand() == Message.Command.info) {
//					Platform.runLater(() -> Dialog.displayInf(I18N.get("reader.out.info.title"), (String) msg.getMessage(), "Ok"));
//				} else {
//					Platform.runLater(() -> Dialog.displayInf(I18N.get("reader.out.other.title"), (String) msg.getMessage(), "Ok"));
//				}
//				break;
//			case StringError:
//				Platform.runLater(() -> Dialog.displayError(I18N.get("reader.error.title"), (String) msg.getMessage(), false));
//				//System.err.println((String) msg.getMessage());
//				break;
//			case Arrays:
//				List<Human> guest = (ArrayList<Human>) msg.getMessage();
//				Main.celebrate.getGuests().clear();
//				Main.celebrate.addParticipants(guest);
//				Platform.runLater(() -> {
//					client.getFx().getWorkSceneController().updateHumansList(Main.celebrate.getGuests());
//					client.getFx().getWorkSceneController().updateHumansList(Main.celebrate.getGuests());
//				});
//				break;
//		}
	}

}
