package com.mill_e.ustwo;

import java.io.Serializable;

/**
 * Abstract class for the various payloads that can be attached to transmissions.
 */
public abstract class TransmissionPayload implements Serializable {

	private static long serialVersionUID = -5816309385357121912L;
	private String _sender = UsTwo.USER_ID;
    private long _timeStamp = System.currentTimeMillis();

    /**
     * Get the ID of the payload-sender.
     * @return Android ID of who sent the payload
     */
    public String getSender(){ return this._sender; }

    /**
     * Get the epoch time of the payload.
     * @return Epoch time
     */
    public long getTimeStamp(){ return this._timeStamp; }

    /**
     * Sets the epoch time stamp on the payload.
     * @param p_timeStamp Epoch time
     * @param p_sender Sender of the payload
     * @return The updated payload
     */
    public TransmissionPayload setPayloadInfo(long p_timeStamp, String p_sender){
        this._timeStamp = p_timeStamp;
        this._sender = p_sender;
        return this;
    }
}
