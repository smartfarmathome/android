package com.smf.smf.data.model;

import com.google.gson.JsonParser;

public class ChatMessage {
	private String uid, fromName, message;

	public static final String CHAT_DATABASE_PATH = "chat_database";

	public static String CHAT_SENDER_UID = "senderUid";
	public static String CHAT_SENDER_NAME = "senderName";
	public static String CHAT_MESSAGE = "message";
	private boolean myself = false;

	public ChatMessage() {
	}

	public String getUid() {
		return uid;
	}

	public ChatMessage(String uid, String fromName, String message) {
		this.uid = uid;
		this.fromName = fromName;
		this.message = JsonParser.parseString(message).getAsJsonObject().get("message").toString();
	}

	public void setMyself(boolean myself) {
		this.myself = myself;
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
		return myself;
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
