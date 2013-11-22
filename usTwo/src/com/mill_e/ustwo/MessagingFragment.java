package com.mill_e.ustwo;

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Fragment to display messaging between two users.
 */
public class MessagingFragment extends ListFragment{

    private EditText _messageText;
    private String _userPartner;
    private MessageArrayAdapter _arrayAdapter;
    private boolean _isViewable = false;
    private final HashMap<String, Long> _backLog = new HashMap<String, Long>();

	//TODO: Add "extras" to messaging, open a popupwindow
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UsTwo.ACTIVE_FRAGMENT = 0;
        View v = inflater.inflate(R.layout.fragment_messaging_view, container, false);
        _userPartner = "Kelsey";

        v.findViewById(R.id.send_button).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(view);
            }
        });
        /*v.findViewById(R.id.button_messaging_extras).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) { simulateReceipt(view); }
        });*/

        _isViewable = true;

        return v;
    }

    private void updateAdapter(Context p_context){
        _arrayAdapter = new MessageArrayAdapter(p_context, R.layout.message_layout_sent, ((UsTwo)getActivity()).getService().getMessagesModel());
        super.setListAdapter(_arrayAdapter);
    }

	private void refreshMessages(){ ((MessageArrayAdapter)super.getListView().getAdapter()).notifyDataSetChanged(); }

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
        String text = _messageText.getText().toString();
        long time = System.currentTimeMillis();
        try {
            UsTwoService service = ((UsTwo)getActivity()).getService();
            if (_backLog.size() > 0){
                Iterator it = _backLog.entrySet().iterator();
                while (it.hasNext()){
                    Map.Entry entry = (Map.Entry) it.next();
                    service.addMessage(((String)entry.getKey()), ((Long)entry.getValue()).longValue());
                }
            }
            service.addMessage(text, time);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e2){
            _backLog.put(text, time);
        }
        _messageText.setText(R.string.empty);
    }


    //defunct for now, service can't send message not from current user
    private void simulateReceipt(View view){
        try {
            ((UsTwo)getActivity()).getService().addMessage(_messageText.getText().toString(), System.currentTimeMillis());
        } catch (IOException e) {
            e.printStackTrace();
        }
        _messageText.setText(R.string.empty);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        Messages messages;
        UsTwoService serviceRef = ((UsTwo)getActivity()).getService();
        if (serviceRef != null)
            messages = serviceRef.getMessagesModel();
        else
            messages = new Messages();

        _arrayAdapter = new MessageArrayAdapter(getActivity(), R.layout.message_layout_sent, messages);

        super.setListAdapter(_arrayAdapter);
        refreshMessages();
        _messageText = (EditText) view.findViewById(R.id.edittext_message);
    }

    //region Unbinding
    @Override
    public void onPause() {
        _messageText = null;
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        _messageText = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        _messageText = null;
        super.onDestroy();
    }
    //endregion
}
