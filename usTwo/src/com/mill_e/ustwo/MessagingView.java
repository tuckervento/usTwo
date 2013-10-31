package com.mill_e.ustwo;

import java.util.ArrayList;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MessagingView extends ListFragment implements OnClickListener
{
    Button sendButton;
    EditText messageText;
    ArrayAdapter<String> messageAdapter;
    ArrayList<String> messageList;

	public MessagingView() {
		// TODO Auto-generated constructor stub
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_messaging_view, container, false);
        
        Button b = (Button) v.findViewById(R.id.send_button);
        b.setOnClickListener(this);
        return v;
    }
    
    public void sendMessage(View view)
    {
    	messageList.add(messageText.getText().toString());
		ListView listView = super.getListView();
		ArrayAdapter<String> adapter = (ArrayAdapter<String>)listView.getAdapter(); 
		adapter.notifyDataSetChanged();
    	messageText.setText(R.string.empty);
    	//Send a message
    }
    
    public void setEmptyText()
    {
    	messageList.add("No messages...");
    	messageAdapter.notifyDataSetChanged();
    }

    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        messageList = new ArrayList<String>();
        super.setListAdapter(new ArrayAdapter<String>(view.getContext(), R.layout.message_layout, R.id.message_contents, messageList));
        //setEmptyText();
        
        sendButton = (Button)view.findViewById(R.id.send_button);
        messageText = (EditText)view.findViewById(R.id.edit_message);
    }

	public void onClick(View v) {
		if (v.getId() == R.id.send_button){
			sendMessage(v);
		}
	}
}
