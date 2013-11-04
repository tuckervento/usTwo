package com.mill_e.ustwo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
/**
 * Created by Owner on 11/3/13.
 */
public class UsTwoService extends Service {

    public interface MessagesModelUpdateListener{
        void onMessagesUpdate(Messages messages);
    }

    public interface CalendarModelUpdateListener{
        void onCalendarUpdate(CalendarEvents events);
    }

    private final Messages _messages = new Messages();
    private final CalendarEvents _events = new CalendarEvents();
    private MessagesModelUpdateListener _messagesModelUpdateListener;
    private CalendarModelUpdateListener _calendarModelUpdateListener;
    public static boolean STARTED_STATE = false;

    public class UsTwoBinder extends Binder {
        UsTwoService getService(){
            return UsTwoService.this;
        }
    }

    private final IBinder _binder = new UsTwoBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        STARTED_STATE = true;
        _messages.setMessagesChangeListener(new Messages.MessagesChangeListener() {
            @Override
            public void onMessagesChange(Messages messages) {
                refreshMessages();
            }
        });
        _events.setEventsChangeListener(new CalendarEvents.EventsChangeListener() {
            @Override
            public void onEventsChange(CalendarEvents events) {
                refreshEvents();
            }
        });
    }

    public void setUpDatabases(final Context p_context){
        Thread messagesThread = new Thread(null, new Runnable() {
            @Override
            public void run() {
                _messages.setUpDatabase(p_context);
            }
        });
        messagesThread.start();
        Thread calendarThread = new Thread(null, new Runnable() {
            @Override
            public void run() {
                _events.setUpDatabase(p_context);
            }
        });
        calendarThread.start();
    }

    private void refreshMessages(){
        if (_messagesModelUpdateListener != null)
            _messagesModelUpdateListener.onMessagesUpdate(_messages);
    }

    private void refreshEvents(){
        if (_calendarModelUpdateListener != null)
            _calendarModelUpdateListener.onCalendarUpdate(_events);
    }

    public void addMessage(String p_contents, String p_sender, long p_timeStamp){
        _messages.addMessage(p_contents, p_sender, p_timeStamp);
    }

    public void setMessagesModelUpdateListener(MessagesModelUpdateListener l){ _messagesModelUpdateListener = l; }

    public void setCalendarModelUpdateListener(CalendarModelUpdateListener l){ _calendarModelUpdateListener = l; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        STARTED_STATE = false;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return _binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    public Messages getMessagesModel(){ return _messages; }

    public CalendarEvents getEventsModel(){ return _events; }
}
