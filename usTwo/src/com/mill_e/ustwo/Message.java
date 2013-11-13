package com.mill_e.ustwo;

/**
 * This object contains data pertaining to a single transmitted message between users.
 */
public final class Message extends TransmissionPayload{

	private static final long serialVersionUID = -6045256050703755731L;
	private final String _text;
    private final String _sender;
    private final long _timeStamp;

    /**
     * Creates a new Message object.
     * @param text The text of the message
     * @param sender Whether this message was received or not
     */
	public Message(String text, String sender, long timeStamp){
		_text = text;
		_sender = sender;
        _timeStamp = timeStamp;
	}

    /**
     * Gets the name of the sender of this message.
     * @return The name of the sender
     */
    public String getSender() { return _sender; }

    /**
     * Retrieves the contents of the message.
     * @return The message contents
     */
	public String getMessageContent(){
		return _text;
	}

    /**
     * Gets the time stamp for the message.
     * @return The message time stamp
     */
    public long getTimeStamp(){ return _timeStamp; }
}
