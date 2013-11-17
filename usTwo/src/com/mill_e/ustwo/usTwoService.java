package com.mill_e.ustwo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttToken;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import static com.mill_e.ustwo.TransmissionType.*;

/**
 * This is the background service providing the architecture for the core features in UsTwo.
 */
public class UsTwoService extends Service implements MqttCallback {

    //MQTT values
    private final int _keepAlive = 900;
    private final int _connectionTimeout = 30;
    private final long _alarmTimer = 900000;
    private final String _mqttServer = "tcp://173.75.0.159:1883";
    private final String _topic = "us";
    private final String _clientId = "ustwo_" + Settings.Secure.ANDROID_ID;

    private final Messages _messages = new Messages();
    private final CalendarEvents _events = new CalendarEvents();
    private final Lists _lists = new Lists();
    private final LinkedList<MqttMessage> _backlog = new LinkedList<MqttMessage>();
    private final MqttConnectOptions _mqttOptions = new MqttConnectOptions();
    private boolean _finishedLoading = false;

    private MqttAsyncClient _mqttClient;
    /**
     * Boolean indicating whether the service has been started.
     */
    public static boolean STARTED_STATE = false;

    /**
     * Custom binder for the UsTwoService.
     */
    public class UsTwoBinder extends Binder {

        /**
         * Get the binder's parent UsTwoService.
         * @return the UsTwoService instance
         */
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

    public void setUpService(final Context p_context){
        setUpDatabases(p_context);
        try {
            _mqttClient = new MqttAsyncClient(_mqttServer, _clientId, new MqttDefaultFilePersistence(p_context.getFilesDir().getAbsolutePath()));
            _mqttClient.setCallback(this);
            _mqttOptions.setCleanSession(false);
            _mqttOptions.setKeepAliveInterval(_keepAlive);
            _mqttOptions.setConnectionTimeout(_connectionTimeout);
            IMqttToken token = _mqttClient.connect(_mqttOptions);
            token.waitForCompletion();
        } catch (MqttException e) {
            handleMqttException(e);
        }
    }

    //region Data Model interactions
    private void setUpDatabases(final Context p_context){
        if (_messages.isEmpty()){
            Thread _messagesThread = new Thread(null, new Runnable() {
                @Override
                public void run() {
                    _messages.setUpDatabase(p_context);
                }
            });
            _messagesThread.start();
        }
        if (_events.isEmpty()){
            Thread _calendarThread = new Thread(null, new Runnable() {
                @Override
                public void run() {
                    _events.setUpDatabase(p_context);
                }
            });
            _calendarThread.start();
        }
        if (_lists.isEmpty()){
            Thread _listsThread = new Thread(null, new Runnable() {
                @Override
                public void run() {
                    _lists.setUpDatabase(p_context);
                }
            });
            _listsThread.start();
        }
    }

    /**
     * Creates and sends a new message.
     * @param p_contents The text contents of the message
     * @param p_sender The username of the sender
     * @param p_timeStamp Timestamp for the message
     */
    public void addMessage(String p_contents, String p_sender, long p_timeStamp) throws IOException {
        Message message = new Message(p_contents, p_sender, p_timeStamp, 0);
        _messages.addMessage(message);
        publishMessage(MESSAGE, message);
    }

    private void addSystemMessage(String p_contents, String p_sender, long p_timeStamp) throws IOException {
        Message message = new Message(p_contents, p_sender, p_timeStamp, 1);
        _messages.addMessage(message);
        publishMessage(MESSAGE, message);
    }

    /**
     * Creates and adds a new calendar event.
     * @param p_year The year of the event
     * @param p_day The day of the event
     * @param p_month The month of the event
     * @param p_hour The hour of the event
     * @param p_minute The minute of the event
     * @param p_name The name of the event
     * @param p_location The location of the event
     * @param p_reminder The reminder selection for the event
     */
    public void addEvent(int p_year, int p_day, int p_month, int p_hour, int p_minute, String p_name, String p_location, int p_reminder) throws IOException {
        CalendarEvent event = new CalendarEvent(p_year, p_day, p_month, p_hour, p_minute, p_name, p_location, p_reminder);
        _events.addEvent(event);
        addSystemMessage(String.format("Created event \"%s\"", p_name), event.sender, new Date().getTime());
        publishMessage(CALENDAR_ITEM, event);
    }

    /**
     * Adds an item to the specified list.
     * @param p_listName Name of the list to be added to
     * @param p_listItem The item to add
     * @param p_checked 1 = the item is checked, 0 = item is unchecked
     */
    public void addListItem(String p_listName, String p_listItem, int p_checked) throws IOException {
        ListItem item = new ListItem(p_listName, p_listItem, p_checked);
        _lists.addItem(item);
        addSystemMessage(String.format("Added \"%s\" to the list \"%s\"", p_listItem, p_listName), item.sender, new Date().getTime());
        publishMessage(LIST_ITEM, item);
    }

    /**
     * Creates a new list.
     * @param p_listName Name of the list to create
     */
    public void createList(String p_listName) throws IOException {
        ListList list = new ListList(p_listName);
        _lists.addList(list);
        addSystemMessage(String.format("Created new list \"%s\"", p_listName), list.sender, new Date().getTime());
        publishMessage(LIST_CREATE, list);
    }

    /**
     * Get the Messages data model.
     * @return The Messages object
     */
    public Messages getMessagesModel(){ return _messages; }

    /**
     * Get the Calendar Events data model.
     * @return The CalendarEvents object
     */
    public CalendarEvents getEventsModel(){ return _events; }

    /**
     * Get the Lists data model.
     * @return The Lists object
     */
    public Lists getListsModel() { return _lists; }
    //endregion

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

    //region Mqtt
    @Override
    public void connectionLost(Throwable throwable) {
        //Test of doing nothing on this callback
    }

    private void publishMessage(TransmissionType p_type, TransmissionPayload p_payload){
        if (!_mqttClient.isConnected()){
            try {
                IMqttToken token = _mqttClient.connect(_mqttOptions);
                token.waitForCompletion();
            } catch (MqttException e) { e.printStackTrace(); }
        }
        Transmission tx = new Transmission(p_type, p_payload);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            ObjectOutputStream writeStream = new ObjectOutputStream(outputStream);
            writeStream.writeObject(tx);
            writeStream.flush();
        }catch(IOException e){ e.printStackTrace(); }

        try {
            _mqttClient.publish(_topic, outputStream.toByteArray(), 2, false);
        } catch (MqttException e) {
            handleMqttException(e);
        }
    }

    @Override
    public void messageArrived(String p_topic, MqttMessage p_mqttMessage) throws Exception {
        if (!_finishedLoading && !finishedLoading()){
            _backlog.add(p_mqttMessage);
        }
        else if (!_backlog.isEmpty()){
            for (int i = 0; i < _backlog.size(); i++){ handleMessage(_backlog.get(i)); }
            _backlog.clear();
        }
        else{ handleMessage(p_mqttMessage); }
    }

    private void handleMessage(MqttMessage p_mqttMessage) throws OptionalDataException, ClassNotFoundException {
        InputStream inputStream = new ByteArrayInputStream(p_mqttMessage.getPayload());
        ObjectInputStream readingStream = null;
        try {
            readingStream = new ObjectInputStream(inputStream);
            Transmission rx = (Transmission)readingStream.readObject();


            switch (rx.type){
                case MESSAGE:
                    Message message = (Message) rx.getPayload();
                    if (!_messages.containsMessage(message))
                        _messages.addMessage(message);
                    break;
                case CALENDAR_ITEM:
                    CalendarEvent event = (CalendarEvent) rx.getPayload();
                    if (!_events.containsEvent(event))
                        _events.addEvent(event);
                    break;
                case LIST_ITEM:
                    ListItem item = (ListItem) rx.getPayload();
                    if (!_lists.containsItem(item.getListName(), item.getItem()))
                        _lists.addItem(item);
                    break;
                case LIST_CREATE:
                    ListList list = (ListList) rx.getPayload();
                    if (_lists.getList(list.getName()) != null)
                        _lists.addList(list);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean finishedLoading() {
        _finishedLoading = _messages.isFinishedLoading() && _events.isFinishedLoading() && _lists.isFinishedLoading();
        return _finishedLoading;
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    private void handleMqttException(MqttException e) {
        e.printStackTrace();
        if (e.getReasonCode() == 32104){
            try {
                _mqttClient.connect(_mqttOptions);
            } catch (MqttException e1) {
                handleMqttException(e1);
            }
        }
        //We need to fill this with reason code-specific exception handling
    }
    //endregion
}
