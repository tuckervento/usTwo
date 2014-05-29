package com.mill_e.ustwo.DataModel;

import java.util.Map;
/**
 * Created by Owner on 12/11/13.
 */
public class EditPayload extends TransmissionPayload {

    public static String JSON_TYPE = "EDIT";
    public static String LIST_ITEM = "ListItem";
    public static String LIST_CHECKED = "ListItemChecked";
    public static String CALENDAR = "CalendarEvent";
    private final String _type;
    private final String _jsonObj;

    public EditPayload(String p_type, String p_jsonObj) {
        _type = p_type;
        _jsonObj = p_jsonObj;
    }

    @Override
    public Map<String, String> getMap() {
        Map<String, String> map =  super.getMap();
        map.put("Type", JSON_TYPE);
        map.put("Target", _type);
        map.put("Object", _jsonObj);

        return map;
    }
}
