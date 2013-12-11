package com.mill_e.ustwo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Custom ArrayAdapter for the MessagingFragment ListView.
 */
public class MessageArrayAdapter extends ArrayAdapter<Message>
{
	private List<Message> _messagesList;
    private final Messages _messages;

	public MessageArrayAdapter(Context context, int resource, Messages objects) {
		super(context, resource, objects.getMessages());
		this._messages = objects;
        _messagesList = _messages.getMessages();
        _messages.setDataModelChangeListener(new UsTwoDataModel.DataModelChangeListener() {
            @Override
            public void onDataModelChange(UsTwoDataModel dataModel) {
                notifyDataSetChanged();
            }
        });
	}

    @Override
    public void notifyDataSetChanged() {
        _messagesList = _messages.getMessages();
        super.notifyDataSetChanged();
    }

    private static class ViewHolder{
		public TextView message;
        public TextView timeStamp;
		public ImageView imageItem;
	}

    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		ViewHolder holder;
		int layoutId;
		
		final Message msg = _messagesList.get(position);
		
		if (msg.getSender().contentEquals(UsTwo.USER_ID))
			layoutId = R.layout.message_layout_sent;
		else
			layoutId = R.layout.message_layout_received;
			
		convertView = inflater.inflate(layoutId, null);
		holder = new ViewHolder();
		holder.message = (TextView) convertView.findViewById(R.id.message_contents);
        holder.timeStamp = (TextView) convertView.findViewById(R.id.time_stamp);
		holder.imageItem = (ImageView) convertView.findViewById(R.id.list_image);
		holder.message.setMinimumHeight(convertView.findViewById(R.id.thumbnail).getHeight());

        String stamp = getTime(msg.getTimeStamp());
        if (stamp != null && holder.timeStamp.getText().equals(getContext().getString(R.string.empty_time_stamp)))
            holder.timeStamp.setText(stamp);
        else if (stamp == null)
            holder.timeStamp.setText("");

		if (holder.message.getText().equals("") && msg != null)
			holder.message.setText(msg.getMessageContent());

        if (msg.isSystem() == 1)
            holder.message.setTypeface(holder.message.getTypeface(), Typeface.ITALIC);

        convertView.setTag(holder);
	
		return convertView;
	}

    private String getTime(long p_timeStamp){
        if (System.currentTimeMillis() - p_timeStamp > 86400000)
            return null;

        return new SimpleDateFormat("hh:mma").format(new Date(p_timeStamp));
    }
}
