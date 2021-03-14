package com.smf.smf.data.model;

public class ChatMessage {
	private String uid, fromName, message;

	public static final String CHAT_DATABASE_PATH = "chat_database";

	public static String CHAT_SENDER_UID = "senderUid";
	public static String CHAT_SENDER_NAME = "senderName";
	public static String CHAT_MESSAGE = "message";

	public ChatMessage() {
	}

	public ChatMessage(String uid, String fromName, String message) {
		this.uid = uid;
		this.fromName = fromName;
		this.message = message;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSelf() {
		// TODO: check it is myself
		return false;
	}

	@Override
	public String toString() {
		return "ChatMessage{" +
				"uid='" + uid + '\'' +
				", fromName='" + fromName + '\'' +
				", message='" + message + '\'' +
				'}';
	}
}
