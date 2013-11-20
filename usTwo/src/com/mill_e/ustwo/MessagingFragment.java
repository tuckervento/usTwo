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
import java.util.Date;

/**
 * Fragment to display messaging between two users.
 */
public class MessagingFragment extends ListFragment{

    private EditText _messageText;
    private String _userPartner;
    private UsTwoService _serviceRef;
    private MessageArrayAdapter _arrayAdapter;
    private Context _context;
    private boolean _isViewable = false;

    private ServiceConnection _serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            _serviceRef = ((UsTwoService.UsTwoBinder) iBinder).getService();
            updateAdapter(_context);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) { _serviceRef = null; }
    };

	//TODO: Add "extras" to messaging, open a popupwindow
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UsTwoHome.ACTIVE_FRAGMENT = 0;
        View v = inflater.inflate(R.layout.fragment_messaging_view, container, false);
        _context = container.getContext();
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

        Intent intent = new Intent(_context, UsTwoService.class);
        _context.bindService(intent, _serviceConnection, Context.BIND_WAIVE_PRIORITY);

        _isViewable = true;

        return v;
    }

    private void updateAdapter(Context p_context){
        _arrayAdapter = new MessageArrayAdapter(p_context, R.layout.message_layout_sent, _serviceRef.getMessagesModel());
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
        try {
            _serviceRef.addMessage(_messageText.getText().toString(), System.currentTimeMillis());
        } catch (IOException e) {
            e.printStackTrace();
        }
        _messageText.setText(R.string.empty);
    }


    //defunct for now, service can't send message not from current user
    private void simulateReceipt(View view){
        try {
            _serviceRef.addMessage(_messageText.getText().toString(), System.currentTimeMillis());
        } catch (IOException e) {
            e.printStackTrace();
        }
        _messageText.setText(R.string.empty);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        _context = view.getContext();
        Messages messages;
        if (_serviceRef != null)
            messages = _serviceRef.getMessagesModel();
        else
            messages = new Messages();

        _arrayAdapter = new MessageArrayAdapter(_context, R.layout.message_layout_sent, messages);

        super.setListAdapter(_arrayAdapter);
        refreshMessages();
        _messageText = (EditText) view.findViewById(R.id.edittext_message);
    }

    //region Unbinding
    @Override
    public void onPause() {
        try{ _context.unbindService(_serviceConnection); }catch(IllegalArgumentException e){ e.printStackTrace(); }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        try{ _context.unbindService(_serviceConnection); }catch(IllegalArgumentException e){ e.printStackTrace(); }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        try{ _context.unbindService(_serviceConnection); }catch(IllegalArgumentException e){ e.printStackTrace(); }
        super.onDestroy();
    }
    //endregion
}
