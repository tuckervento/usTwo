package com.mill_e.ustwo;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Fragment to display messaging between two users.
 */
public class MessagingFragment extends ListFragment
{
    private EditText _messageText;
    private String _userName;
    private String _userPartner;
    private final Messages _messages;

	public MessagingFragment(Messages p_messages) {
        _messages = p_messages;
        _messages.setMessagesChangeListener(new Messages.MessagesChangeListener() { @Override public void onMessagesChange(Messages messages) { refreshMessages(); } });
	}

	//TODO: Add "extras" to messaging, open a popupwindow
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messaging_view, container, false);
        Context context = container.getContext();
        _userName = context.getString(R.string.user_name);
        _userPartner = context.getString(R.string.user_partner);

        v.findViewById(R.id.send_button).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(view);
            }
        });
        v.findViewById(R.id.button_messaging_extras).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) { simulateReceipt(view); }
        });

        return v;
    }

    /**
     * Refreshes the message ListView.
     */
	private void refreshMessages(){ ((MessageArrayAdapter)super.getListView().getAdapter()).notifyDataSetChanged(); }

    /**
     * Simulates bulk messaging.
     * @param view Context view
     */
	private void debuggingMessages(View view){
		_messageText.setText("Message1");
		sendMessage(view);
		_messageText.setText("Message2");
		simulateReceipt(view);
		_messageText.setText("Message1");
		sendMessage(view);
		_messageText.setText("Message2");
		simulateReceipt(view);
		_messageText.setText("Message1");
		sendMessage(view);
		_messageText.setText("Message2");
		simulateReceipt(view);
		_messageText.setText("Message1");
		sendMessage(view);
		_messageText.setText("Message2");
		simulateReceipt(view);
		_messageText.setText("Message1");
		sendMessage(view);
		_messageText.setText("Message2");
		simulateReceipt(view);
		_messageText.setText("Message1");
		sendMessage(view);
		_messageText.setText("Message2");
		simulateReceipt(view);
	}

    /**
     * Sends the current contents of the message EditText as a Message object to the other user.
     * @param view Context view
     */
    public void sendMessage(View view){
    	_messages.addMessage(_messageText.getText().toString(), _userName);
    	_messageText.setText(R.string.empty);
    }

    /**
     * Simulates receipt of the contents of the message EditText as a Message object to this user.
     * @param view Context view
     */
    public void simulateReceipt(View view){
        _messages.addMessage(_messageText.getText().toString(), _userPartner);
    	_messageText.setText(R.string.empty);
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.setListAdapter(new MessageArrayAdapter(view.getContext(), R.layout.message_layout_sent, _messages.getMessages()));
        refreshMessages();
        _messageText = (EditText) view.findViewById(R.id.edittext_message);
    }
}
