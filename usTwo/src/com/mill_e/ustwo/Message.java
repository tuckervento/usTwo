package com.mill_e.ustwo;

/**
 * This object contains data pertaining to a single transmitted message between users.
 */
public final class Message extends TransmissionPayload{

	private static final long serialVersionUID = -6045256050703755731L;
	private final String _text;
    private final String _sender;
    private final long _timeStamp;
    private final int _system;

    /**
     * Creates a new Message object.
     * @param text The text of the message
     * @param sender Whether this message was received or not
     * @param p_system 0 = non-system, 1 = system
     */
	public Message(String text, String sender, long timeStamp, int p_system){
		_text = text;
		_sender = sender;
        _timeStamp = timeStamp;
        _system = p_system;
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

    /**
     * Returns int indicating whether the message is of the system type.
     * @return 0 = non-system, 1 = system
     */
    public int isSystem() { return _system; }
}
