package com.mill_e.ustwo;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import com.mill_e.ustwo.DataModel.CalendarEvent;
import com.mill_e.ustwo.DataModel.CalendarEvents;
import com.mill_e.ustwo.DataModel.EditPayload;
import com.mill_e.ustwo.DataModel.ListItem;
import com.mill_e.ustwo.DataModel.ListList;
import com.mill_e.ustwo.DataModel.Lists;
import com.mill_e.ustwo.DataModel.Message;
import com.mill_e.ustwo.DataModel.Messages;
import com.mill_e.ustwo.DataModel.RemovePayload;
import com.mill_e.ustwo.DataModel.TransmissionPayload;
import com.mill_e.ustwo.DataModel.UserSettings;
import com.mill_e.ustwo.Fragments.MessagingFragment;
import com.mill_e.ustwo.DataModel.Transmission;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import static com.mill_e.ustwo.DataModel.TransmissionType.*;

/**
 * This is the background service providing the architecture for the core features in UsTwo.
 */
@SuppressWarnings({"FieldCanBeLocal", "SameReturnValue"})
public class UsTwoService extends Service implements MqttCallback {

    //region MQTT values
    private final int _keepAlive = 36000;
    private final int _connectionTimeout = 30;
    private final long _alarmTimer = 36000000;
    private final String _mqttServer = "tcp://54.209.169.230:1883";
    private final String _mqttUserName = "ustwo";
    private final String _mqttPassword = "millie";
    private final String _topic = "us";
    private final String _pingTopic = "ping";
    private final String _clientId = "ustwo_" + UsTwo.USER_ID;
    private final String _alarmIntentFilter = "UsTwoAlarm";
    private final BroadcastReceiver _mqttBr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) { pingServer(); }
    };
    //endregion

    //region Notification values
    private int _notificationCount = 0;
    private String _notificationContentText = ""; //TODO: Update-by-concatenation for notifications
    private final String _notificationContentTitleSingle = "New Message";
    private final String _notificationContentTitleMultiple = "New Messages";
    private final int _notificationMessageId = 1;
    private final String _notificationIntentFilter = "UsTwoNotification";
    private final BroadcastReceiver _notificationBr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            _notificationCount = 0;
            _notificationContentText = "";
        }
    };
    //endregion

    private final UserSettings _settings = new UserSettings();
    private final Messages _messages = new Messages();
    private final CalendarEvents _events = new CalendarEvents();
    private final Lists _lists = new Lists();
    private final LinkedList<MqttMessage> _backlog = new LinkedList<MqttMessage>();
    private final MqttConnectOptions _mqttOptions = new MqttConnectOptions();
    private boolean _finishedLoading = false;

    private final String _encoding = "utf-8";

    private MqttAsyncClient _mqttClient;

    /**
     * Boolean indicating whether the service has been started.
     */
    public static boolean STARTED_STATE = false;

    /**
     * Boolean indicating whether the service has been set up.
     */
    public static boolean SETUP_STATE = false;

    /**
     * Custom binder for the UsTwoService.
     */
    public class UsTwoBinder extends Binder {

        /**
         * Get the binder's parent UsTwoService.
         * @return the UsTwoService instance
         */
        public UsTwoService getService(){
            return UsTwoService.this;
        }
    }

    private final IBinder _binder = new UsTwoBinder();

    //region Service overrides
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        STARTED_STATE = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        STARTED_STATE = false;
        SETUP_STATE = false;
        _messages.closeDatabase();
        _events.closeDatabase();
        _lists.closeDatabase();
        unregisterReceiver(_mqttBr);
        unregisterReceiver(_notificationBr);
        try {
            _mqttClient.disconnect();
        } catch (MqttException e) {
            handleMqttException(e);
        }

        super.onDestroy();
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
    //endregion

    //region Setup
    public void setUpService(final Context p_context){
        setUpDatabases(p_context);
        try {
            _mqttClient = new MqttAsyncClient(_mqttServer, _clientId, new MqttDefaultFilePersistence(p_context.getFilesDir().getAbsolutePath()));
            _mqttClient.setCallback(this);
            _mqttOptions.setUserName(_mqttUserName);
            _mqttOptions.setPassword(_mqttPassword.toCharArray());
            _mqttOptions.setCleanSession(false);
            _mqttOptions.setKeepAliveInterval(_keepAlive);
            _mqttOptions.setConnectionTimeout(_connectionTimeout);
            IMqttToken token = _mqttClient.connect(_mqttOptions);
            token.waitForCompletion();
            IMqttToken token2 = _mqttClient.subscribe("us", 2);
            token2.waitForCompletion();
        } catch (MqttException e) {
            handleMqttException(e);
        }
        _lists.setListItemCheckedChangedListener(new Lists.ListItemCheckedChangedListener() {
            @Override
            public void onListItemCheckedChanged(long p_timestamp, String p_listname, String p_item, int p_checked) {
                checkListItem(p_timestamp, p_listname, p_item, p_checked);
            }
        });
        
        registerReceiver(_mqttBr, new IntentFilter(_alarmIntentFilter));
        registerReceiver(_notificationBr, new IntentFilter(_notificationIntentFilter));
        ((AlarmManager)getSystemService(ALARM_SERVICE)).setRepeating(AlarmManager.RTC_WAKEUP, _alarmTimer, _alarmTimer, PendingIntent.getBroadcast(this, 0, new Intent(_alarmIntentFilter), 0));
    
        SETUP_STATE = true;
    }

    private boolean finishedLoading() {
        _finishedLoading = _messages.isFinishedLoading() && _events.isFinishedLoading() && _lists.isFinishedLoading();
        return _finishedLoading;
    }

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
    //endregion

    //region Data Model interactions
    /**
     * Creates and sends a new message.
     * @param p_contents The text contents of the message
     */
    public void addMessage(String p_contents){
        Message message = new Message(p_contents, 0);
        _messages.addMessage(message);
        publishMessage(message);
    }

    private void addSystemMessage(String p_contents){
        Message message = new Message(p_contents, 1);
        _messages.addMessage(message);
        publishMessage(message);
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
    public void addEvent(int p_year, int p_day, int p_month, int p_hour, int p_minute, String p_name, String p_location, int p_reminder){
        publishMessage(_events.addEvent(new CalendarEvent(p_year, p_day, p_month, p_hour, p_minute, p_name, p_location, p_reminder)));
        addSystemMessage(String.format("Created event \"%s\"", p_name));
    }

    /**
     * Edits an existing calendar event.
     * @param p_timestamp The timestamp of the existing event
     * @param p_year The year of the event
     * @param p_day The day of the event
     * @param p_month The month of the event
     * @param p_hour The hour of the event
     * @param p_minute The minute of the event
     * @param p_name The name of the event
     * @param p_location The location of the event
     * @param p_reminder The reminder selection for the event
     */
    public void editEvent(long p_timestamp, int p_year, int p_day, int p_month, int p_hour, int p_minute, String p_name, String p_location, int p_reminder){
        CalendarEvent event = _events.editEvent(p_timestamp, p_year, p_day, p_month, p_hour, p_minute, p_name, p_location, p_reminder);
        if (event != null){
            publishMessage(new EditPayload(EditPayload.CALENDAR, new JSONObject(event.getMap()).toString()));
            addSystemMessage(String.format("Edited \"%s\"", event.getEventName()));
        }
    }

    /**
     * Removes an existing calendar event.
     * @param p_timestamp The timestamp of the event
     * @param p_name The name of the event to remove
     */
    public void removeEvent(long p_timestamp, String p_name){
        _events.removeEvent(p_timestamp);
        publishMessage(new RemovePayload(RemovePayload.CALENDAR, String.valueOf(p_timestamp)));
        addSystemMessage(String.format("Removed event \"%s\"", p_name));
    }

    /**
     * Adds an item to the specified list.
     * @param p_listName Name of the list to be added to
     * @param p_listItem The item to add
     * @param p_checked 1 = the item is checked, 0 = item is unchecked
     */
    public void addListItem(String p_listName, String p_listItem, @SuppressWarnings("SameParameterValue") int p_checked){
        publishMessage(_lists.addItem(new ListItem(p_listName, p_listItem, p_checked)));
        addSystemMessage(String.format("Added \"%s\" to the list \"%s\"", p_listItem, p_listName));
    }

    /**
     * Edits an item in the specified list.
     * @param p_timestamp The timestamp of the item
     * @param p_listName The name of the list containing the item
     * @param p_oldItem The old item
     * @param p_listItem The new item
     * @param p_checked 1 = the item is checked, 0 = item is unchecked
     */
    public void editListItem(long p_timestamp, String p_listName, String p_oldItem, String p_listItem, int p_checked){
        ListItem item = _lists.editItem(p_timestamp, p_listName, p_listItem, p_checked);
        if (item != null){
            publishMessage(new EditPayload(EditPayload.LIST_ITEM, new JSONObject(item.getMap()).toString()));
            addSystemMessage(String.format("Changed \"%s\" to \"%s\" in \"%s\"", p_oldItem, p_listItem, p_listName));
        }
    }

    /**
     * Sends a message regarding the checked status of the specified ListItem.
     * @param p_timestamp The timestamp of the item
     * @param p_listName The name of the list containing the item
     * @param p_listItem The item
     * @param p_checked Checked status of the item
     */
    @SuppressWarnings("WeakerAccess")
    public void checkListItem(long p_timestamp, String p_listName, String p_listItem, int p_checked){
        JSONObject obj = new JSONObject();
        try {
            obj.put("ListName", p_listName);
            obj.put("Item", p_listItem);
            obj.put("Checked", String.valueOf(p_checked));
            obj.put("Timestamp", p_timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        publishMessage(new EditPayload(EditPayload.LIST_CHECKED, obj.toString()));
    }

    /**
     * Removes a list item and notifies the other user of this action.
     * @param p_listItem The item to remove
     * @param p_timestamp The timestamp of the item to remove
     * @param p_listName The name of the list containing the item
     */
    public void removeListItem(String p_listItem, long p_timestamp, String p_listName){
        _lists.removeItem(p_timestamp, p_listName);
        JSONObject obj = new JSONObject();
        try {
            obj.put("ListName", p_listName);
            obj.put("Timestamp", p_timestamp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        publishMessage(new RemovePayload(RemovePayload.LIST_ITEM, obj.toString()));
        addSystemMessage(String.format("Removed \"%s\" from \"%s\"", p_listItem, p_listName));
    }

    /**
     * Creates a new list.
     * @param p_listName Name of the list to create
     */
    public void createList(String p_listName){
        ListList list = new ListList(p_listName);
        _lists.addList(list);
        publishMessage(list);
        addSystemMessage(String.format("Created new list \"%s\"", p_listName));
    }

    /**
     * Removes a list from the data model.
     * @param p_listName Name of the list to remove
     */
    public void removeList(String p_listName){
        _lists.removeList(p_listName);
        publishMessage(new RemovePayload(RemovePayload.LIST, p_listName));
        addSystemMessage(String.format("Removed list \"%s\"", p_listName));
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

    /**
     * Gets the UserSettings object.
     * @return The UserSettings
     */
    public UserSettings getSettings() { return _settings; }

    public String getUsername() { return _settings.getUserName(); }
    //endregion

    //region Mqtt
    @Override
    public void connectionLost(Throwable throwable) {
        //Test of doing nothing on this callback
    }

    @Override
    public void messageArrived(String p_topic, MqttMessage p_mqttMessage) {
        boolean check = finishedLoading();
        if (!check){
            _backlog.add(p_mqttMessage);
        }
        else if (!_backlog.isEmpty()){
            for (MqttMessage message : _backlog){ handleMessage(message); }
            _backlog.clear();
        }
        else{ handleMessage(p_mqttMessage); }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) { }

    private void publishMessage(TransmissionPayload p_payload){
        if (_mqttClient == null)
            return;
        if (!_mqttClient.isConnected()){
            try {
                IMqttToken token = _mqttClient.connect(_mqttOptions);
                token.waitForCompletion();
            } catch (MqttException e) { handleMqttException(e); }
        }

        JSONObject tx = new JSONObject(p_payload.getMap());
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancelAll();

        try {
            _mqttClient.publish(_topic, tx.toString().getBytes(_encoding), 2, false);
        } catch (MqttException e) { handleMqttException(e); } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(MqttMessage p_mqttMessage) {
        try {
            String received = new String(p_mqttMessage.getPayload(), _encoding);
            Transmission rx = decodeJson(new JSONObject(received));

            if (rx == null)
                return;

            switch (rx.type){
                case MESSAGE:
                    Message message = (Message) rx.getPayload();
                    if (!_messages.containsMessage(message)){
                        _messages.addMessage(message);
                        throwNotification(message);
                    }
                    break;
                case CALENDAR_ITEM:
                    CalendarEvent event = (CalendarEvent) rx.getPayload();
                    if (!_events.containsEvent(event))
                        _events.addEvent(event);
                    break;
                case LIST_ITEM:
                    ListItem item = (ListItem) rx.getPayload();
                    if (!_lists.containsItem(item))
                        _lists.addItem(item);
                    break;
                case LIST_CREATE:
                    ListList list = (ListList) rx.getPayload();
                    if (_lists.getList(list.getName()) == null)
                        _lists.addList(list);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Transmission decodeJson(JSONObject p_obj){
        try {
            String type = p_obj.getString("Type");
            if (type.contentEquals(Message.JSON_TYPE))
                return new Transmission(MESSAGE, new Message(p_obj.getString("Text"), Integer.parseInt(p_obj.getString("System")))
                        .setPayloadInfo(Long.parseLong(p_obj.getString("Timestamp")), p_obj.getString("Sender")));
            else if (type.contentEquals(CalendarEvent.JSON_TYPE))
                return new Transmission(CALENDAR_ITEM, new CalendarEvent(Integer.parseInt(p_obj.getString("Year")), Integer.parseInt(p_obj.getString("Day")),
                        Integer.parseInt(p_obj.getString("Month")), Integer.parseInt(p_obj.getString("Hour")), Integer.parseInt(p_obj.getString("Minute")),
                        p_obj.getString("Name"), p_obj.getString("Location"), Integer.parseInt(p_obj.getString("Reminder")))
                        .setPayloadInfo(Long.parseLong(p_obj.getString("Timestamp")), p_obj.getString("Sender")));
            else if (type.contentEquals(ListList.JSON_TYPE))
                return new Transmission(LIST_CREATE, new ListList(p_obj.getString("Name"))
                        .setPayloadInfo(Long.parseLong(p_obj.getString("Timestamp")), p_obj.getString("Sender")));
            else if (type.contentEquals(ListItem.JSON_TYPE))
                return new Transmission(LIST_ITEM, new ListItem(p_obj.getString("ListName"), p_obj.getString("Item"), Integer.parseInt(p_obj.getString("Checked")))
                        .setPayloadInfo(Long.parseLong(p_obj.getString("Timestamp")), p_obj.getString("Sender")));
            else if (type.contentEquals(EditPayload.JSON_TYPE))
                return receiveEdit(p_obj.getString("Target"), new JSONObject(p_obj.getString("Object")));
            else if (type.contentEquals(RemovePayload.JSON_TYPE))
                return receiveRemove(p_obj.getString("Target"), p_obj.getString("Identifier"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Transmission receiveEdit(String p_target, JSONObject p_obj){
        try {
            if (p_target.contentEquals(EditPayload.LIST_CHECKED))
                _lists.checkItemWithoutNotifyingListener(p_obj.getLong("Timestamp"), p_obj.getString("ListName"), p_obj.getString("Item"), Integer.parseInt(p_obj.getString("Checked")));
            else if (p_target.contentEquals(EditPayload.CALENDAR))
                _events.editEvent(Long.parseLong(p_obj.getString("Timestamp")), Integer.parseInt(p_obj.getString("Year")), Integer.parseInt(p_obj.getString("Day")),
                        Integer.parseInt(p_obj.getString("Month")), Integer.parseInt(p_obj.getString("Hour")), Integer.parseInt(p_obj.getString("Minute")),
                        p_obj.getString("Name"), p_obj.getString("Location"), Integer.parseInt(p_obj.getString("Reminder")));
            else if (p_target.contentEquals(EditPayload.LIST_ITEM))
                _lists.editItem(Long.parseLong(p_obj.getString("Timestamp")), p_obj.getString("ListName"), p_obj.getString("Item"), Integer.parseInt(p_obj.getString("Checked")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Transmission receiveRemove(String p_target, String p_identifier){
        if (p_target.contentEquals(RemovePayload.LIST))
            _lists.removeList(p_identifier);
        else if (p_target.contentEquals(RemovePayload.CALENDAR))
            _events.removeEvent(Long.parseLong(p_identifier));
        else if (p_target.contentEquals(RemovePayload.LIST_ITEM)) {
            try {
                JSONObject obj = new JSONObject(p_identifier);
                _lists.removeItem(Long.parseLong(obj.getString("Timestamp")), obj.getString("ListName"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void pingServer(){
        try {
            _mqttClient.publish(_pingTopic, ("p").getBytes(), 1, false);
        } catch (MqttException e) {
            handleMqttException(e);
        } catch (NullPointerException e) {
            e.printStackTrace();
            //setUpService(getApplicationContext());
        }
    }

    private void handleMqttException(MqttException e){
        e.printStackTrace();
        if (e.getReasonCode() == 32104){
            try {
                IMqttToken token = _mqttClient.connect(_mqttOptions);
                token.waitForCompletion();
            } catch (MqttException e1) { handleMqttException(e1); } catch (NullPointerException e2) { e2.printStackTrace(); }
        }
        //We need to fill this with reason code-specific exception handling
    }

    private void throwNotification(Message p_message){
        if (!MessagingFragment.IS_VIEWABLE)
            return;
        _notificationCount++;

        String contentTitle = (_notificationCount > 1) ? _notificationContentTitleMultiple : _notificationContentTitleSingle;

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentInfo("("+_notificationCount+")")
                .setContentTitle(contentTitle)
                .setContentText(p_message.getMessageContent())
                .setSmallIcon(R.drawable.ic_launcher)
                .setLights(0xFF00D0, 1000, 2500)
                .setWhen(p_message.getTimeStamp())
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 1000})
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, UsTwo.class), PendingIntent.FLAG_UPDATE_CURRENT))
                .setDeleteIntent(PendingIntent.getBroadcast(this, 0, new Intent(_notificationIntentFilter), 0));

        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(_notificationMessageId, builder.build());
    }
    //endregion
}
