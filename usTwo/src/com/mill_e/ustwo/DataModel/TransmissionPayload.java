package com.mill_e.ustwo.DataModel;

import com.mill_e.ustwo.UsTwo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for the various payloads that can be attached to transmissions.
 */
public abstract class TransmissionPayload implements Serializable {

	private static long serialVersionUID = -5816309385357121912L;
	private String _sender = UsTwo.USER_ID;
    private long _timestamp = System.currentTimeMillis();

    /**
     * Get the ID of the payload-sender.
     * @return Android ID of who sent the payload
     */
    public String getSender(){ return this._sender; }

    /**
     * Get the epoch time of the payload.
     * @return Epoch time
     */
    public long getTimeStamp(){ return this._timestamp; }

    /**
     * Sets the epoch time stamp on the payload.
     * @param p_timestamp Epoch time
     * @param p_sender Sender of the payload
     * @return The updated payload
     */
    public TransmissionPayload setPayloadInfo(long p_timestamp, String p_sender){
        this._timestamp = p_timestamp;
        this._sender = p_sender;
        return this;
    }

    /**
     * Get a map representing all of the values of the TransmissionPayload object.
     * @return A map containing all values by name of property
     */
    public Map<String, String> getMap(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Sender", this._sender);
        map.put("Timestamp", String.valueOf(this._timestamp));
        return map;
    }
}
