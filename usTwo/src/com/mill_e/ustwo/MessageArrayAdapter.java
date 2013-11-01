package com.mill_e.ustwo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageArrayAdapter extends ArrayAdapter<Message>
{
	Context context;
	private ArrayList<Message> messages;
	
	public MessageArrayAdapter(Context context, int resource, ArrayList<Message> objects) {
		super(context, resource, objects);
		this.context = context;
		this.messages = objects;
	}
	
	public static class ViewHolder{
		public TextView textItem;
		public ImageView imageItem;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder;
		int layoutId;
		
		final Message msg = messages.get(position);
		
		if (msg.Received)
			layoutId = R.layout.message_layout_received;
		else
			layoutId = R.layout.message_layout_sent;
			
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
