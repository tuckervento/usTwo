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

    private final Messages _messages = new Messages();
    private final CalendarEvents _events = new CalendarEvents();
    private final Lists _lists = new Lists();
    private Thread _messagesThread;
    private Thread _calendarThread;
    private Thread _listsThread;
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
    }

    public void setUpDatabases(final Context p_context){
        if (_messages.isEmpty()){
            _messagesThread = new Thread(null, new Runnable() {
                @Override
                public void run() {
                    _messages.setUpDatabase(p_context);
                }
            });
            _messagesThread.start();
        }
        if (_events.isEmpty()){
            _calendarThread = new Thread(null, new Runnable() {
                @Override
                public void run() {
                    _events.setUpDatabase(p_context);
                }
            });
            _calendarThread.start();
        }
        if (_lists.isEmpty()){
            _listsThread = new Thread(null, new Runnable() {
                @Override
                public void run() {
                    _lists.setUpDatabase(p_context);
                }
            });
            _listsThread.start();
        }
    }

    public void addMessage(String p_contents, String p_sender, long p_timeStamp){
        _messages.addMessage(p_contents, p_sender, p_timeStamp);
    }

    public void addEvent(int p_year, int p_day, int p_month, int p_hour, int p_minute, String p_name, String p_location, int p_reminder){
        _events.addEvent(p_year, p_day, p_month, p_hour, p_minute, p_name, p_location, p_reminder);
    }

    public void addListItem(String p_listName, String p_listItem, int p_checked){
        _lists.addItem(p_listName, p_listItem, p_checked);
    }

    public void createList(String p_listName){
        _lists.createList(p_listName);
    }

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

    public Lists getListsModel() { return _lists; }
}
