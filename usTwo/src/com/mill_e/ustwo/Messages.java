package com.mill_e.ustwo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Data model for Message objects.
 */
public class Messages extends UsTwoDataModel{

    private final LinkedList<Message> _messages = new LinkedList<Message>();
    private final List<Message> _safeMessages = Collections.unmodifiableList(_messages);
    private MessagingDBOpenHelper _dbOpener;

    private DataModelChangeListener _messagesChangeListener;

    //region UsTwoDataModel
    @Override
    public void setUpDatabase(Context p_context){
        _dbOpener = new MessagingDBOpenHelper(p_context, MessagingDBOpenHelper.DATABASE_NAME, null, MessagingDBOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = _dbOpener.getWritableDatabase();
        loadDatabase(db);
    }

    @Override
    public void setDataModelChangeListener(DataModelChangeListener l){ _messagesChangeListener = l; }

    @Override
    public boolean isEmpty(){ return _messages.size() == 0; }

    @Override
    public void clearModel(){
        _messages.clear();
        _dbOpener.getWritableDatabase().delete(MessagingDBOpenHelper.MESSAGE_DATABASE_TABLE, null, null);
        notifyListener();
    }

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

    private void notifyListener(){
        if (_messagesChangeListener != null)
            _messagesChangeListener.onDataModelChange(this);
    }
    //endregion

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
     * Create and add a new message to the model.
     * @param p_text Text of the message
     * @param p_sender Sender of the message
     */
    public void addMessage(String p_text, String p_sender, long p_timeStamp){
        _messages.add(findIndex(p_timeStamp), new Message(p_text, p_sender, p_timeStamp));

        ContentValues newVals = new ContentValues();
        newVals.put(MessagingDBOpenHelper.KEY_MESSAGE_CONTENTS, p_text);
        newVals.put(MessagingDBOpenHelper.KEY_MESSAGE_SENDER, p_sender);
        newVals.put(MessagingDBOpenHelper.KEY_MESSAGE_TIMESTAMP, p_timeStamp);

        _dbOpener.getWritableDatabase().insert(MessagingDBOpenHelper.MESSAGE_DATABASE_TABLE, null, newVals);
        notifyListener();
    }

    /**
     * Adds an existing Message object to the model.
     * @param p_message The Message to add
     */
    public void addMessage(Message p_message){
        _messages.add(findIndex(p_message.getTimeStamp()), p_message);

        ContentValues newVals = new ContentValues();
        newVals.put(MessagingDBOpenHelper.KEY_MESSAGE_CONTENTS, p_message.getMessageContent());
        newVals.put(MessagingDBOpenHelper.KEY_MESSAGE_SENDER, p_message.getSender());
        newVals.put(MessagingDBOpenHelper.KEY_MESSAGE_TIMESTAMP, p_message.getTimeStamp());

        _dbOpener.getWritableDatabase().insert(MessagingDBOpenHelper.MESSAGE_DATABASE_TABLE, null, newVals);
        notifyListener();
    }

    private int findIndex(long p_timeStamp) {
        int i = _messages.size() - 1;

        while (i >= 0){
            if (p_timeStamp < _messages.get(i).getTimeStamp()){
                i--;
                continue;
            }else{
                i++;
                break;
            }
        }

        return i;
    }
}
