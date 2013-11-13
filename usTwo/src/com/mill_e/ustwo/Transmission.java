package com.mill_e.ustwo;

import java.io.Serializable;

/**
 * Generic transmission between two users, carrying a payload.
 */
public final class Transmission implements Serializable{
	private static final long serialVersionUID = 993796426109822183L;
    /**
     * Indicates the type of transmission, using the TransmissionType enum.
     */
	public final TransmissionType type;
	private final TransmissionPayload payload;

    /**
     * Creates a Transmission object of the specified type.
     * @param p_type Type of transmission
     */
	public Transmission(TransmissionType p_type, TransmissionPayload p_payload){
		type = p_type;
        payload = p_payload;
	}

    /**
     * Retrieves the transmission's payload.
     * @return The payload
     */
	public TransmissionPayload GetPayload(){
		return this.payload;
	}
}
