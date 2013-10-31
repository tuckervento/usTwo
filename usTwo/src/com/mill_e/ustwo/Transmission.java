package com.mill_e.ustwo;

import java.io.Serializable;

public class Transmission implements Serializable{
	private static final long serialVersionUID = 993796426109822183L;
	public TransmissionType type;
	private TransmissionPayload payload;
	
	public Transmission(TransmissionType p_type){
		this.type = p_type;
	}
	
	public void SetPayload(TransmissionPayload p_payload){
		this.payload = p_payload;
	}
	
	public TransmissionPayload GetPayload(){
		return this.payload;
	}
}
