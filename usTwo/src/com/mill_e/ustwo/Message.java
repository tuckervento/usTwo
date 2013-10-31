package com.mill_e.ustwo;

public class Message extends TransmissionPayload{
	private static final long serialVersionUID = -6045256050703755731L;
	private String _text;
	public boolean Received;
	
	public Message(String text, boolean rec){
		this._text = text;
		this.Received = rec;
	}
	
	public String getMessageContent(){
		return this._text;
	}
}
