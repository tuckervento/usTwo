package com.mill_e.ustwo;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Fragment to display messaging between two users.
 */
public class MessagingFragment extends ListFragment implements OnClickListener
{
    ArrayList<Message> messageList;
    EditText messageText;
    private String _userName;
    private String _userPartner;

	public MessagingFragment() {
        messageList = new ArrayList<Message>();
	}

	//TODO: Add "extras" to messaging, open a popupwindow
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_messaging_view, container, false);
        Context context = container.getContext();
        _userName = context.getString(R.string.user_name);
        _userPartner = context.getString(R.string.user_partner);
        Button b = (Button) v.findViewById(R.id.send_button);
        b.setOnClickListener(this);
        b = (Button) v.findViewById(R.id.button_messaging_extras);
        b.setOnClickListener(this);
        return v;
    }

    /**
     * Refreshes the message ListView.
     */
	private void refreshMessages(){
		ListView listView = super.getListView();
		MessageArrayAdapter adapter = (MessageArrayAdapter)listView.getAdapter(); 
		adapter.notifyDataSetChanged();
	}

    /**
     * Simulates bulk messaging.
     * @param view Context view
     */
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

    /**
     * Sends the current contents of the message EditText as a Message object to the other user.
     * @param view Context view
     */
    public void sendMessage(View view){
    	messageList.add(new Message(messageText.getText().toString(), _userName));
		refreshMessages();
    	messageText.setText(R.string.empty);
    }

    /**
     * Simulates receipt of the contents of the message EditText as a Message object to this user.
     * @param view Context view
     */
    public void simulateReceipt(View view){
    	messageList.add(new Message(messageText.getText().toString(), _userPartner));
		refreshMessages();
    	messageText.setText(R.string.empty);
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.setListAdapter(new MessageArrayAdapter(view.getContext(), R.layout.message_layout_sent, messageList));
        if (!messageList.isEmpty())
        	refreshMessages();
        messageText = (EditText) view.findViewById(R.id.edittext_message);
        ListView listView = super.getListView();
    }

	public void onClick(View v) {
		if (v.getId() == R.id.send_button){
            sendMessage(v);
			debuggingMessages(v);
		}
	}
}
