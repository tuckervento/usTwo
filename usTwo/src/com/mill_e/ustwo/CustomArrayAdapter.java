package com.mill_e.ustwo;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomArrayAdapter extends ArrayAdapter<String>
{
	Context context;
	
	public CustomArrayAdapter(Context context, int resource, List<String> objects) {
		super(context, resource, objects);
		this.context= context;
		// TODO Auto-generated constructor stub
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView messageText;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		
		if (convertView == null){ convertView = inflater.inflate(R.layout.message_layout, null); }
		
		messageText = (TextView)convertView.findViewById(R.id.edit_message);
	
		return convertView;
	}

}
