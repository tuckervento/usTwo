package com.mill_e.ustwo;

import java.util.ArrayList;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MessagingView extends ListFragment implements OnClickListener
{
    EditText messageText;
    ArrayList<Message> messageList;

	public MessagingView() {
        messageList = new ArrayList<Message>();
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_messaging_view, container, false);
        
        Button b = (Button) v.findViewById(R.id.send_button);
        b.setOnClickListener(this);
        return v;
    }
	
	private void refreshMessages(){
		ListView listView = super.getListView();
		MessageArrayAdapter adapter = (MessageArrayAdapter)listView.getAdapter(); 
		adapter.notifyDataSetChanged();
	}
	
	private void debuggingMessages(View view){
		messageText.setText("Message1");
		sendMessage(view);
		messageText.setText("Message2");
		simulateReceipt(view);
		messageText.setText("Message1");
		sendMessage(view);
		messageText.setText("Message2");
		simulateReceipt(view);
		messageText.setText("Message1");
		sendMessage(view);
		messageText.setText("Message2");
		simulateReceipt(view);
		messageText.setText("Message1");
		sendMessage(view);
		messageText.setText("Message2");
		simulateReceipt(view);
		messageText.setText("Message1");
		sendMessage(view);
		messageText.setText("Message2");
		simulateReceipt(view);
		messageText.setText("Message1");
		sendMessage(view);
		messageText.setText("Message2");
		simulateReceipt(view);
	}
    
    public void sendMessage(View view){
    	messageList.add(new Message(messageText.getText().toString(), false));
		refreshMessages();
    	messageText.setText(R.string.empty);
    }
    
    public void simulateReceipt(View v){
    	messageList.add(new Message(messageText.getText().toString(), true));
		refreshMessages();
    	messageText.setText(R.string.empty);
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.setListAdapter(new MessageArrayAdapter(view.getContext(), R.layout.message_layout_sent, messageList));
        if (!messageList.isEmpty())
        	refreshMessages();
        ListView listView = super.getListView();
        listView.setPadding(0, listView.getPaddingTop(), 0, 0);
        messageText = (EditText)view.findViewById(R.id.edit_message);
    }

	public void onClick(View v) {
		if (v.getId() == R.id.send_button){
			debuggingMessages(v);
			sendMessage(v);
		}
	}
}
