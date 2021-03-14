package com.smf.smf.ui.chat;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smf.smf.R;
import com.smf.smf.data.model.ChatMessage;

public class MessagesListAdapter extends BaseAdapter {

	private Context context;
	private List<ChatMessage> messagesItems;
	private String TAG = MessagesListAdapter.class.getSimpleName();

	public MessagesListAdapter(Context context, List<ChatMessage> navDrawerItems) {
		this.context = context;
		this.messagesItems = navDrawerItems;
	}

	@Override
	public int getCount() {
		return messagesItems.size();
	}

	@Override
	public Object getItem(int position) {
		return messagesItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		/**
		 * The following list not implemented reusable list items as list items
		 * are showing incorrect data Add the solution if you have one
		 * */

		ChatMessage m = messagesItems.get(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		ChatMessage chatMessage = messagesItems.get(position);
		if( chatMessage == null ) {
			Log.e(TAG, "getView: chatMessage is null. position => " + position +
					" messagesItems.size() => " + messagesItems.size() + " messagesItems => " + messagesItems);
			convertView = mInflater.inflate(R.layout.list_item_message_right,
					null);
			return convertView;
		}

		// Identifying the message owner
		if (messagesItems.get(position).isSelf()) {
			// message belongs to you, so load the right aligned layout
			convertView = mInflater.inflate(R.layout.list_item_message_right,
					null);
		} else {
			// message belongs to other person, load the left aligned layout
			convertView = mInflater.inflate(R.layout.list_item_message_left,
					null);
		}

		TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
		TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);

		txtMsg.setText(m.getMessage());
		lblFrom.setText(m.getFromName());

		return convertView;
	}
}
