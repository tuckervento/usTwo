package com.mill_e.ustwo;

import java.io.Serializable;

/**
 * Abstract class for the various payloads that can be attached to transmissions.
 */
public abstract class TransmissionPayload implements Serializable {

	private static long serialVersionUID = -5816309385357121912L;
	protected final String sender = UsTwo.USER_ID;
    private long _timeStamp = System.currentTimeMillis();

    public long getTimeStamp(){
        return this._timeStamp;
    }

    public TransmissionPayload setTimeStamp(long p_timeStamp){
        this._timeStamp = p_timeStamp;
        return this;
    }
}
