package com.mill_e.ustwo.DataModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mill_e.ustwo.DataModel.Helpers.MessagingDBOpenHelper;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Data model for Message objects.
 */
public class Messages extends UsTwoDataModel {

    private final LinkedList<Message> _messages = new LinkedList<Message>();
    private final List<Message> _safeMessages = Collections.unmodifiableList(_messages);
    private MessagingDBOpenHelper _dbOpener;

    private static boolean FINISHED_LOADING = false;

    private DataModelChangeListener _messagesChangeListener;

    //region UsTwoDataModel
    @Override
    public void setUpDatabase(Context p_context){
        _dbOpener = new MessagingDBOpenHelper(p_context);
        SQLiteDatabase db = _dbOpener.getWritableDatabase();
        loadDatabase(db);
        _dbOpener.close();
    }

    /**
     * Returns a boolean to indicate whether the data model has finished loading from the SQLite database.
     * @return Boolean indicating whether the data model has finished loading from the SQLite database
     */
    public boolean isFinishedLoading(){ return FINISHED_LOADING; }

    @Override
    public void setDataModelChangeListener(DataModelChangeListener l){ _messagesChangeListener = l; }

    @Override
    public boolean isEmpty(){ return _messages.size() == 0; }

    @Override
    public void clearModel(){
        _messages.clear();
        _dbOpener.getWritableDatabase().delete(MessagingDBOpenHelper.MESSAGE_DATABASE_TABLE, null, null);
        _dbOpener.close();
        notifyListener();
    }

    @Override
    public void closeDatabase(){ _dbOpener.close(); }

    private void loadDatabase(SQLiteDatabase p_db){
        String[] result_columns = new String[] { MessagingDBOpenHelper.KEY_MESSAGE_SYSTEM, MessagingDBOpenHelper.KEY_MESSAGE_TIMESTAMP, MessagingDBOpenHelper.KEY_MESSAGE_SENDER, MessagingDBOpenHelper.KEY_MESSAGE_CONTENTS };

        Cursor cursor = p_db.query(MessagingDBOpenHelper.MESSAGE_DATABASE_TABLE, result_columns, null, null, null, null, MessagingDBOpenHelper.KEY_MESSAGE_TIMESTAMP + " ASC");

        int systemIndex = cursor.getColumnIndexOrThrow(MessagingDBOpenHelper.KEY_MESSAGE_SYSTEM);
        int timestampIndex = cursor.getColumnIndexOrThrow(MessagingDBOpenHelper.KEY_MESSAGE_TIMESTAMP);
        int senderIndex = cursor.getColumnIndexOrThrow(MessagingDBOpenHelper.KEY_MESSAGE_SENDER);
        int contentsIndex = cursor.getColumnIndexOrThrow(MessagingDBOpenHelper.KEY_MESSAGE_CONTENTS);

        while (cursor.moveToNext())
            _messages.add((Message) new Message(cursor.getString(contentsIndex), cursor.getInt(systemIndex)).setPayloadInfo(cursor.getLong(timestampIndex), cursor.getString(senderIndex)));
        cursor.close();
        notifyListener();
        FINISHED_LOADING = true;
    }

    private void notifyListener(){
        if (_messagesChangeListener != null)
            _messagesChangeListener.onDataModelChange();
    }
    //endregion

    /**
     * Return the last message.
     * @return The most recent message added to the model
     */
    public Message getLastMessage(){ return (_messages.size() <= 0) ? null : _messages.getFirst(); }

    /**
     * Return a list of all messages.
     * @return List of all messages in the model
     */
    public List<Message> getMessages(){ return _safeMessages; }

    /**
     * Indicates whether the message already exists within the model.
     * @param p_message The message to check for
     * @return Boolean indicating existence of message
     */
    public boolean containsMessage(Message p_message){
        Message check = _messages.get(findIndex(p_message.getTimeStamp()));
        return (check.getMessageContent().contentEquals(p_message.getMessageContent()) && check.getTimeStamp() == p_message.getTimeStamp());
    }

    private void internalAddMessage(String p_text, String p_sender, long p_timestamp, int p_system){
        _messages.add(findIndex(p_timestamp), (Message) new Message(p_text, p_system).setPayloadInfo(p_timestamp, p_sender));

        ContentValues newVals = new ContentValues();
        newVals.put(MessagingDBOpenHelper.KEY_MESSAGE_CONTENTS, p_text);
        newVals.put(MessagingDBOpenHelper.KEY_MESSAGE_SENDER, p_sender);
        newVals.put(MessagingDBOpenHelper.KEY_MESSAGE_TIMESTAMP, p_timestamp);
        newVals.put(MessagingDBOpenHelper.KEY_MESSAGE_SYSTEM, p_system);

        if (_dbOpener != null) {
            _dbOpener.getWritableDatabase().insert(MessagingDBOpenHelper.MESSAGE_DATABASE_TABLE, null, newVals);
            _dbOpener.close();
        }
        notifyListener();
    }

    /**
     * Adds an existing Message object to the model.
     * @param p_message The Message to add
     */
    public void addMessage(Message p_message){
        _messages.add(findIndex(p_message.getTimeStamp()), p_message);

        ContentValues newVals = new ContentValues();
        newVals.put(MessagingDBOpenHelper.KEY_MESSAGE_SYSTEM, p_message.isSystem());
        newVals.put(MessagingDBOpenHelper.KEY_MESSAGE_TIMESTAMP, p_message.getTimeStamp());
        newVals.put(MessagingDBOpenHelper.KEY_MESSAGE_SENDER, p_message.getSender());
        newVals.put(MessagingDBOpenHelper.KEY_MESSAGE_CONTENTS, p_message.getMessageContent());

        if (_dbOpener != null) {
            _dbOpener.getWritableDatabase().insert(MessagingDBOpenHelper.MESSAGE_DATABASE_TABLE, null, newVals);
            _dbOpener.close();
        }
        notifyListener();
    }

    private int findIndex(long p_timestamp) {
        if (_messages.isEmpty())
            return 0;

        int i = _messages.size() - 1;

        while (i > 0){
            long stamp = _messages.get(i).getTimeStamp();
            if (p_timestamp < stamp){
                i--;
            }else if (p_timestamp > stamp){
                i++;
                break;
            }else if (p_timestamp == stamp){
                break;
            }
        }

        return i;
    }
}
