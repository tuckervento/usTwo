package com.mill_e.ustwo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ListFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Fragment to display messaging between two users.
 */
public class MessagingFragment extends ListFragment
{
    private EditText _messageText;
    private String _userName;
    private String _userPartner;
    private Messages _messages;
    private UsTwoService _serviceRef;

	//TODO: Add "extras" to messaging, open a popupwindow
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_messaging_view, container, false);
        final Context context = container.getContext();
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

        Intent intent = new Intent(context, UsTwoService.class);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                _serviceRef = ((UsTwoService.UsTwoBinder) iBinder).getService();
                _messages = _serviceRef.getMessagesModel();
                updateAdapter(context);

                _serviceRef.setMessagesModelUpdateListener(new UsTwoService.MessagesModelUpdateListener() {
                    @Override
                    public void onMessagesUpdate(Messages messages) {
                        refreshMessages();
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                _serviceRef = null;
            }
        }, Context.BIND_WAIVE_PRIORITY);

        return v;
    }

    private void updateAdapter(Context p_context){
        super.setListAdapter(new MessageArrayAdapter(p_context, R.layout.message_layout_sent, _messages.getMessages()));
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
    	_serviceRef.addMessage(_messageText.getText().toString(), _userName, new Date().getTime());
    	_messageText.setText(R.string.empty);
    }

    /**
     * Simulates receipt of the contents of the message EditText as a Message object to this user.
     * @param view Context view
     */
    public void simulateReceipt(View view){
        _serviceRef.addMessage(_messageText.getText().toString(), _userPartner, new Date().getTime());
    	_messageText.setText(R.string.empty);
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        Context context = view.getContext();
        List<Message> messages;
        if (_messages != null)
            messages = _messages.getMessages();
        else
            messages = new ArrayList<Message>();

        super.setListAdapter(new MessageArrayAdapter(context, R.layout.message_layout_sent, messages));
        refreshMessages();
        _messageText = (EditText) view.findViewById(R.id.edittext_message);
    }
}
