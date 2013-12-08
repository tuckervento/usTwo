package com.mill_e;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main implements MqttCallback {

    private MqttAsyncClient _client;
    private BufferedWriter _messageLog;
    private BufferedWriter _calendarLog;
    private BufferedWriter _listLog;

    protected static String TYPE_MESSAGE = "MESSAGE";
    protected static String TYPE_LIST = "LIST";
    protected static String TYPE_LISTITEM = "LISTITEM";
    protected static String TYPE_CALENDAREVENT = "CALENDAREVENT";

    public void main(String[] args) {
        try {
            _client = new MqttAsyncClient("http://10.0.0.11:1883", "LOGGER");
            _client.setCallback(this);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(false);
            _client.connect(connectOptions);
        } catch (MqttException e) {
            handleMqttException(e);
        }

        setUpLogs();
    }

    @Override
    public void connectionLost(Throwable cause) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        InputStream inputStream = new ByteArrayInputStream(message.getPayload());
        ObjectInputStream readingStream = new ObjectInputStream(inputStream);
        JSONObject tx = (JSONObject) readingStream.readObject();
        String type = tx.getString("Type");

        if (type.contentEquals(TYPE_MESSAGE))
            logMessage(tx);
        else if (type.contentEquals(TYPE_CALENDAREVENT))
            logCalendar(tx);
        else if (type.contentEquals(TYPE_LIST))
            logListCreate(tx);
        else if (type.contentEquals(TYPE_LISTITEM))
            logListItem(tx);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void handleMqttException(MqttException e){
        //Handle different exception types
    }

    private void setUpLogs(){
        File messageLogFile = new File("message_log.txt");
        File calendarLogFile = new File("calendar_log.txt");
        File listLogFile = new File("list_log.txt");

        try{
            messageLogFile.createNewFile();
            calendarLogFile.createNewFile();
            listLogFile.createNewFile();
        }catch(IOException e){ e.printStackTrace(); }

        try {
            _messageLog = new BufferedWriter(new FileWriter(messageLogFile));
            _calendarLog = new BufferedWriter(new FileWriter(calendarLogFile));
            _listLog = new BufferedWriter(new FileWriter(listLogFile));
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void logMessage(JSONObject p_message){
        try {
            _messageLog.append(String.format("%s | %s | %s", getDate(Long.parseLong(p_message.getString("Timestamp"))), p_message.getString("Sender"), p_message.getString("Text")));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void logCalendar(JSONObject p_event){
        try {
            _calendarLog.append(String.format("%s | %s | %02d/%02d/%04d | %02d:%02d | %s", getDate(Long.parseLong(p_event.getString("Timestamp"))), p_event.getString("Sender"),
                    p_event.getString("Day"), p_event.getString("Month"), p_event.getString("Year"), p_event.getString("Hour"), p_event.getString("Minute"), p_event.getString("Name")));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void logListItem(JSONObject p_item){
        try {
            _listLog.append(String.format("%s | %s | %s | %s", getDate(Long.parseLong(p_item.getString("Timestamp"))), p_item.getString("Sender"), p_item.getString("ListName"),
                    p_item.getString("Item")));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void logListCreate(JSONObject p_list){
        try {
            _listLog.append(String.format("%s | %s | CREATED LIST %s", getDate(Long.parseLong(p_list.getString("Timestamp"))), p_list.getString("Sender"), p_list.getString("Name")));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getDate(long p_epoch){
        Date date = new Date(p_epoch);
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
    }
}
