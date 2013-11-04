package com.mill_e.ustwo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    private MessagingDBOpenHelper _dbOpener;

    private MessagesChangeListener _messagesChangeListener;

    public void setUpDatabase(Context p_context){
        _dbOpener = new MessagingDBOpenHelper(p_context, MessagingDBOpenHelper.DATABASE_NAME, null, MessagingDBOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = _dbOpener.getWritableDatabase();
        loadDatabase(db);
    }

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
    public void addMessage(String p_text, String p_sender, long p_timeStamp){
        _messages.add(new Message(p_text, p_sender, p_timeStamp));

        ContentValues newVals = new ContentValues();
        newVals.put(MessagingDBOpenHelper.KEY_MESSAGE_CONTENTS, p_text);
        newVals.put(MessagingDBOpenHelper.KEY_MESSAGE_SENDER, p_sender);
        newVals.put(MessagingDBOpenHelper.KEY_MESSAGE_TIMESTAMP, p_timeStamp);

        _dbOpener.getWritableDatabase().insert(MessagingDBOpenHelper.MESSAGE_DATABASE_TABLE, null, newVals);
        notifyListener();
    }

    public boolean isEmpty(){ return _messages.size() == 0; }

    private void loadDatabase(SQLiteDatabase p_db){
        String[] result_columns = new String[] { MessagingDBOpenHelper.KEY_MESSAGE_SENDER, MessagingDBOpenHelper.KEY_MESSAGE_CONTENTS, MessagingDBOpenHelper.KEY_MESSAGE_TIMESTAMP };

        Cursor cursor = p_db.query(MessagingDBOpenHelper.MESSAGE_DATABASE_TABLE, result_columns, null, null, null, null, MessagingDBOpenHelper.KEY_MESSAGE_TIMESTAMP + " ASC");

        int contentsIndex = cursor.getColumnIndexOrThrow(MessagingDBOpenHelper.KEY_MESSAGE_CONTENTS);
        int senderIndex = cursor.getColumnIndexOrThrow(MessagingDBOpenHelper.KEY_MESSAGE_SENDER);
        int timeStampIndex = cursor.getColumnIndexOrThrow(MessagingDBOpenHelper.KEY_MESSAGE_TIMESTAMP);

        while (cursor.moveToNext())
            _messages.add(new Message(cursor.getString(contentsIndex), cursor.getString(senderIndex), cursor.getLong(timeStampIndex)));

        notifyListener();
    }

    /**
     * Clear all messages from the model.
     */
    public void clearMessages(){
        _messages.clear();
        _dbOpener.getWritableDatabase().delete(MessagingDBOpenHelper.MESSAGE_DATABASE_TABLE, null, null);
        notifyListener();
    }

    private void notifyListener(){
        if (_messagesChangeListener != null)
            _messagesChangeListener.onMessagesChange(this);
    }
}
