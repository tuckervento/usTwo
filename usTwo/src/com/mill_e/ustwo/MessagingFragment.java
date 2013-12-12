package com.mill_e.ustwo;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Fragment to display messaging between two users.
 */
public class MessagingFragment extends ListFragment{

    private String _userPartner;
    private MessageArrayAdapter _arrayAdapter;
    private boolean _isViewable = false;
    private final LinkedList<String> _backLog = new LinkedList<String>();

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        Messages messages;
        UsTwoService serviceRef = ((UsTwo)getActivity()).getService();
        if (serviceRef != null){
            messages = serviceRef.getMessagesModel();
            messages.setDataModelChangeListener(new UsTwoDataModel.DataModelChangeListener() {
                @Override
                public void onDataModelChange(UsTwoDataModel dataModel) {
                    refreshMessages();
                }
            });
        }else
            messages = new Messages();

        _arrayAdapter = new MessageArrayAdapter(getActivity(), R.layout.message_layout_sent, messages);

        super.setListAdapter(_arrayAdapter);
        refreshMessages();
    }

    private void updateAdapter(Context p_context){
        _arrayAdapter = new MessageArrayAdapter(p_context, R.layout.message_layout_sent, ((UsTwo)getActivity()).getService().getMessagesModel());
        super.setListAdapter(_arrayAdapter);
    }

	private void refreshMessages(){ ((MessageArrayAdapter)super.getListView().getAdapter()).notifyDataSetChanged(); }

	private void debuggingMessages(View view){
        EditText box = ((EditText)getView().findViewById(R.id.edittext_message));
        box.setText("Message1");
		sendMessage(view);
        box.setText("Message2");
		simulateReceipt(view);
        box.setText("Message1");
		sendMessage(view);
        box.setText("Message2");
		simulateReceipt(view);
        box.setText("Message1");
        sendMessage(view);
        box.setText("Message2");
        simulateReceipt(view);
        box.setText("Message1");
        sendMessage(view);
        box.setText("Message2");
        simulateReceipt(view);
        box.setText("Message1");
        sendMessage(view);
        box.setText("Message2");
        simulateReceipt(view);
        box.setText("Message1");
        sendMessage(view);
        box.setText("Message2");
        simulateReceipt(view);
	}

    /**
     * Sends the current contents of the message EditText as a Message object to the other user.
     * @param view Context view
     */
    public void sendMessage(View view){
        EditText box = ((EditText)getView().findViewById(R.id.edittext_message));
        String text = box.getText().toString();
        if (text.contentEquals(""))
            return;
        try {
            UsTwoService service = ((UsTwo)getActivity()).getService();
            if (_backLog.size() > 0){
                for (int i = 0; i < _backLog.size(); i++)
                    service.addMessage(_backLog.get(i));
                _backLog.clear();
            }
            service.addMessage(text);
        } catch (NullPointerException e2){
            _backLog.add(text);
        }
        box.setText(R.string.empty);
    }

    //defunct for now, service can't send message not from current user
    private void simulateReceipt(View view){
        EditText box = ((EditText)getView().findViewById(R.id.edittext_message));
        ((UsTwo)getActivity()).getService().addMessage(box.getText().toString());
        box.setText(R.string.empty);
    }
}
