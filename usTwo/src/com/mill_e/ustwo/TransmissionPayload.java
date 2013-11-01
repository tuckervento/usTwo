package com.mill_e.ustwo;

import java.io.Serializable;

public abstract class TransmissionPayload implements Serializable {

	private static final long serialVersionUID = -5816309385357121912L;
	protected final String sender = UsTwoHome.userName;

}
