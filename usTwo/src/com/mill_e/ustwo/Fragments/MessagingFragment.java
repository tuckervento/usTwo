package com.mill_e.ustwo.Fragments;

import android.app.ListFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mill_e.ustwo.DataModel.Message;
import com.mill_e.ustwo.DataModel.Messages;
import com.mill_e.ustwo.DataModel.UsTwoDataModel;
import com.mill_e.ustwo.UIParts.MessageArrayAdapter;
import com.mill_e.ustwo.R;
import com.mill_e.ustwo.UsTwo;
import com.mill_e.ustwo.UsTwoService;

import java.util.LinkedList;

/**
 * Fragment to display messaging between two users.
 */
public class MessagingFragment extends ListFragment{

    private MessageArrayAdapter _arrayAdapter;
    public static boolean IS_VIEWABLE = false;
    private final LinkedList<String> _backLog = new LinkedList<String>();

	//TODO: Add "extras" to messaging, open a popupwindow
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UsTwo.ACTIVE_FRAGMENT = 0;
        View v = inflater.inflate(R.layout.fragment_messaging_view, container, false);

        v.findViewById(R.id.send_button).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        v.findViewById(R.id.button_messaging_extras).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) { ((UsTwo)getActivity()).getService().pingServer(); }
        });

        IS_VIEWABLE = true;

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        ((NotificationManager)view.getContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
        Messages messages;
        UsTwoService serviceRef = ((UsTwo)getActivity()).getService();
        if (serviceRef != null){
            messages = serviceRef.getMessagesModel();
            messages.setDataModelChangeListener(new UsTwoDataModel.DataModelChangeListener() {
                @Override
                public void onDataModelChange() {
                    refreshMessages();
                }
            });
        }else
            messages = new Messages();

        _arrayAdapter = new MessageArrayAdapter(getActivity(), messages);

        super.setListAdapter(_arrayAdapter);
        refreshMessages();
    }

    @Override
    public void onStart() {
        super.onStart();
        IS_VIEWABLE = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        IS_VIEWABLE = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        IS_VIEWABLE = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        IS_VIEWABLE = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IS_VIEWABLE = false;
    }

    private void updateAdapter(Context p_context){
        _arrayAdapter = new MessageArrayAdapter(p_context, ((UsTwo)getActivity()).getService().getMessagesModel());
        super.setListAdapter(_arrayAdapter);
    }

	private void refreshMessages(){ ((MessageArrayAdapter)super.getListView().getAdapter()).notifyDataSetChanged(); }

    /**
     * Sends the current contents of the message EditText as a Message object to the other user.
     */
    void sendMessage(){
        EditText box = ((EditText)getView().findViewById(R.id.edittext_message));
        String text = box.getText().toString();
        if (text.contentEquals(""))
            return;
        try {
            UsTwoService service = ((UsTwo)getActivity()).getService();
            if (_backLog.size() > 0){
                for (String message : _backLog)
                    service.addMessage(message);
                _backLog.clear();
            }
            service.addMessage(text);
        } catch (NullPointerException e2){
            _backLog.add(text);
        }
        box.setText(R.string.empty);
    }

    /*defunct for now, service can't send message not from current user
    private void simulateReceipt(){
        EditText box = ((EditText)getView().findViewById(R.id.edittext_message));
        ((UsTwo)getActivity()).getService().addMessage(box.getText().toString());
        box.setText(R.string.empty);
    }*/
}
