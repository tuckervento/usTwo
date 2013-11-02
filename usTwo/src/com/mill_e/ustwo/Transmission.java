package com.mill_e.ustwo;

import java.io.Serializable;

/**
 * Generic transmission between two users, carrying a payload.
 */
public class Transmission implements Serializable{
	private static final long serialVersionUID = 993796426109822183L;
	public TransmissionType type;
	private TransmissionPayload payload;

    /**
     * Creates a Transmission object of the specified type.
     * @param p_type Type of transmission
     */
	public Transmission(TransmissionType p_type){
		this.type = p_type;
	}

    /**
     * Sets the payload of the transmission.
     * @param p_payload The payload object
     */
	public void SetPayload(TransmissionPayload p_payload){
		this.payload = p_payload;
	}

    /**
     * Retrieves the transmission's payload.
     * @return The payload
     */
	public TransmissionPayload GetPayload(){
		return this.payload;
	}
}
