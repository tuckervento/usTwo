package com.mill_e.ustwo;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Custom ArrayAdapter for the MessagingFragment ListView.
 */
public class MessageArrayAdapter extends ArrayAdapter<Message>
{
	private final Context _context;
	private List<Message> _messages;

	public MessageArrayAdapter(Context context, int resource, List<Message> objects) {
		super(context, resource, objects);
		this._context = context;
		this._messages = objects;
	}
	
	private static class ViewHolder{
		public TextView textItem;
		public ImageView imageItem;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder;
		int layoutId;
		
		final Message msg = _messages.get(position);
		
		if (msg.getSender().contentEquals(_context.getString(R.string.user_name)))
			layoutId = R.layout.message_layout_sent;
		else
			layoutId = R.layout.message_layout_received;
			
		convertView = inflater.inflate(layoutId, null);
		holder = new ViewHolder();
		holder.textItem = (TextView) convertView.findViewById(R.id.message_contents);
		holder.imageItem = (ImageView) convertView.findViewById(R.id.list_image);
		holder.textItem.setMinimumHeight(convertView.findViewById(R.id.thumbnail).getHeight());
		convertView.setTag(holder);
		
		if (holder.textItem.getText().equals("") && msg != null)
			holder.textItem.setText(msg.getMessageContent());
	
		return convertView;
	}
}
