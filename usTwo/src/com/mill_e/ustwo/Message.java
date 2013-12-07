package com.mill_e.ustwo;

/**
 * This object contains data pertaining to a single transmitted message between users.
 */
public final class Message extends TransmissionPayload{

	private static final long serialVersionUID = -6045256050703755731L;
	private final String _text;
    private final int _system;

    /**
     * Creates a new Message object.
     * @param p_text The text of the message
     * @param p_system 0 = non-system, 1 = system
     */
	public Message(String p_text, int p_system){
		_text = p_text;
        _system = p_system;
    }

    /**
     * Creates a new Message object with a specified sender.
     * @param p_text The text of the message
     * @param p_sender The sender of the message
     * @param p_system 0 = non-system, 1 = system
     */
    public Message(String p_text, String p_sender, int p_system){
        _text = p_text;
        _system = p_system;
    }

    /**
     * Retrieves the contents of the message.
     * @return The message contents
     */
	public String getMessageContent(){
		return _text;
	}

    /**
     * Returns int indicating whether the message is of the system type.
     * @return 0 = non-system, 1 = system
     */
    public int isSystem() { return _system; }
}
