package com.mill_e.ustwo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Data model for Message objects.
 */
public class Messages {

    /** Interface to define Listener for the Messages model. */
    public interface MessagesChangeListener{
        /** @param messages The Messages model */
        void onMessagesChange(Messages messages);
    }

    private final LinkedList<Message> _messages = new LinkedList<Message>();
    private final List<Message> _safeMessages = Collections.unmodifiableList(_messages);

    private MessagesChangeListener _messagesChangeListener;

    /**
     * Set the listener for any changes to Messages.
     * @param l The MessagesChangeListener
     */
    public void setMessagesChangeListener(MessagesChangeListener l){ _messagesChangeListener = l; }

    /**
     * Return the last message.
     * @return The most recent message added to the model
     */
    public Message getLastMessage(){ return (_messages.size() <= 0) ? null : _messages.getLast(); }

    /**
     * Return a list of all messages.
     * @return List of all messages in the model
     */
    public List<Message> getMessages(){ return _safeMessages; }

    /**
     * Add a new message to the model.
     * @param p_text Text of the message
     * @param p_sender Sender of the message
     */
    public void addMessage(String p_text, String p_sender){
        _messages.add(new Message(p_text, p_sender));
        notifyListener();
    }

    /**
     * Clear all messages from the model.
     */
    public void clearMessages(){
        _messages.clear();
        notifyListener();
    }

    private void notifyListener(){
        if (_messagesChangeListener != null)
            _messagesChangeListener.onMessagesChange(this);
    }
}
