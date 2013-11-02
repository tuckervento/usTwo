package com.mill_e.ustwo;

/**
 * This object contains data pertaining to a single transmitted message between users.
 */
public class Message extends TransmissionPayload
{
	private static final long serialVersionUID = -6045256050703755731L;
	private String _text;
    private String _sender;
	//TODO: Add timestamp

    /**
     * Creates a new Message object.
     * @param text The text of the message.
     * @param sender Whether this message was received or not (
     */
	public Message(String text, String sender){
		this._text = text;
		this._sender = sender;
	}

    /**
     * Gets the name of the sender of this message.
     * @return The name of the sender.
     */
    public String getSender() { return _sender; }

    /**
     * Retrieves the contents of the message.
     * @return The message contents.
     */
	public String getMessageContent(){
		return this._text;
	}
}
