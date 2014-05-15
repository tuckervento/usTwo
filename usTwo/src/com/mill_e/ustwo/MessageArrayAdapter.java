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
        public TextView timestamp;
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
        holder.timestamp = (TextView) convertView.findViewById(R.id.time_stamp);
		holder.imageItem = (ImageView) convertView.findViewById(R.id.list_image);
		holder.message.setMinimumHeight(convertView.findViewById(R.id.thumbnail).getHeight());

        if (holder.timestamp.getText().equals(getContext().getString(R.string.empty_time_stamp))){
            String time = getTime(msg.getTimeStamp());
            if (time.charAt(0) == '0')
                holder.timestamp.setText(time.substring(1));
            else
                holder.timestamp.setText(getTime(msg.getTimeStamp()));
        }

		if (holder.message.getText().equals("") && msg != null)
			holder.message.setText(msg.getMessageContent());

        if (msg.isSystem() == 1)
            holder.message.setTypeface(holder.message.getTypeface(), Typeface.ITALIC);

        convertView.setTag(holder);
	
		return convertView;
	}

    private String getTime(long p_timestamp){
        return new SimpleDateFormat((System.currentTimeMillis() - p_timestamp > 86400000) ? "dd/MM/yyyy" : "hh:mma").format(new Date(p_timestamp));
    }
}
