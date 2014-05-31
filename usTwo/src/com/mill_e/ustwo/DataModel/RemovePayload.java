package com.mill_e.ustwo.DataModel;

import java.util.Map;
/**
 * Generic payload for the removal of objects from the data model.
 */
public class RemovePayload extends TransmissionPayload {

    public static final String JSON_TYPE = "REMOVE";
    public static final String LIST_ITEM = "ListItem";
    public static final String CALENDAR = "CalendarEvent";
    public static final String LIST = "List";
    private final String _type;
    private final String _id;

    /**
     * Creates a new RemovePayload.
     * @param p_type Type of object to be removed.
     * @param p_identifier Identifier for the object (Stringified long or other identifier based on type)
     */
    public RemovePayload(String p_type, String p_identifier) {
        _type = p_type;
        _id = p_identifier;
    }

    @Override
    public Map<String, String> getMap() {
        Map<String, String> map =  super.getMap();
        map.put("Type", JSON_TYPE);
        map.put("Target", _type);
        map.put("Identifier", _id);

        return map;
    }
}
