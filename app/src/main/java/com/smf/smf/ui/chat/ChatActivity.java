package com.smf.smf.ui.chat;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smf.smf.R;
import com.smf.smf.data.model.ChatMessage;
import com.smf.smf.data.model.LoggedInUser;
import com.smf.smf.ui.login.LoginViewModel;
import com.smf.smf.ui.login.LoginViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import static com.smf.smf.data.model.ChatMessage.CHAT_DATABASE_PATH;
import static com.smf.smf.data.model.ChatMessage.CHAT_SENDER_NAME;

public class ChatActivity extends AppCompatActivity {

	// LogCat tag
	private static final String TAG = ChatActivity.class.getSimpleName();

	private Button btnSend;
	private EditText inputMsg;

	// Chat messages list adapter
	private MessagesListAdapter adapter;
	private List<ChatMessage> listMessages;
	private ListView listViewMessages;

	private Utils utils;

	// Client name
	private String name = null;

	// JSON flags to identify the kind of JSON response
	private static final String TAG_SELF = "self", TAG_NEW = "new",
			TAG_MESSAGE = "message", TAG_EXIT = "exit";
	private LoginViewModel loginViewModel;
	@Nullable LoggedInUser loggedInUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_main);

		btnSend = (Button) findViewById(R.id.btnSend);
		inputMsg = (EditText) findViewById(R.id.inputMsg);
		listViewMessages = (ListView) findViewById(R.id.list_view_messages);

		loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
				.get(LoginViewModel.class);

		FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference myRef = database.getReference(CHAT_DATABASE_PATH);
		// Read from the database
		myRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				// This method is called once with the initial value and again
				// whenever data at this location is updated.
				ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);

				Log.d(TAG, String.format("Got string message! %s", chatMessage));

				parseMessage(chatMessage);
			}

			@Override
			public void onCancelled(DatabaseError error) {
				// Failed to read value
				Log.w(TAG, "Failed to read value.", error.toException());
			}
		});

		utils = new Utils(getApplicationContext());

		// Getting the person name from previous screen
		Intent i = getIntent();
		name = i.getStringExtra(CHAT_SENDER_NAME);

		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Sending message to web socket server
				sendMessageToServer(utils.getSendMessageJSON(inputMsg.getText()
						.toString()));

				// Clearing the input filed once message was sent
				inputMsg.setText("");
			}
		});

		listMessages = new ArrayList<>();

		adapter = new MessagesListAdapter(this, listMessages);
		listViewMessages.setAdapter(adapter);
	}

	/**
	 * Method to send message to web socket server
	 * */
	private void sendMessageToServer(String message) {
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference myRef = database.getReference(CHAT_DATABASE_PATH);

		String uid = loggedInUser != null ? loggedInUser.getUserId() : "-1";
		String userName = loggedInUser != null ? loggedInUser.getDisplayName() : "error user";
		ChatMessage chatMessage = new ChatMessage(uid, userName, message);

		myRef.setValue(chatMessage);
	}

	/**
	 * Parsing the JSON message received from server The intent of message will
	 * be identified by JSON node 'flag'. flag = self, message belongs to the
	 * person. flag = new, a new person joined the conversation. flag = message,
	 * a new message received from server. flag = exit, somebody left the
	 * conversation.
	 * */
	private void parseMessage(final ChatMessage chatMessage) {
		appendMessage(chatMessage);
	}

	/**
	 * Appending message to list view
	 * */
	private void appendMessage(final ChatMessage m) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				listMessages.add(m);

				adapter.notifyDataSetChanged();

				// Playing device's notification
				playBeep();
			}
		});
	}

	private void showToast(final String message) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), message,
						Toast.LENGTH_LONG).show();
			}
		});

	}

	/**
	 * Plays device's default notification sound
	 * */
	public void playBeep() {

		try {
			Uri notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
					notification);
			r.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

}
