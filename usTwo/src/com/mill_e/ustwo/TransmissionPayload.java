package com.mill_e.ustwo;

import java.io.Serializable;

/**
 * Abstract class for the various payloads that can be attached to transmissions.
 */
public abstract class TransmissionPayload implements Serializable {

	private static long serialVersionUID = -5816309385357121912L;
	protected final String sender = UsTwoHome.USER_ID;

}
