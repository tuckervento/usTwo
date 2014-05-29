package com.mill_e.ustwo.DataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class contains all of the user-specified settings for UsTwo.
 */
public class UserSettings {

    private String _userName;
    private final List<String> _defaultNameList = new ArrayList<String>();

    /**
     * Constructor for the user-specified settings object.
     */
    public UserSettings(){
        generateDefaultName();
    }

    private void generateDefaultName() {
        _defaultNameList.add("Boo-Bear");
        _defaultNameList.add("Boo");
        _defaultNameList.add("BB");
        _defaultNameList.add("Babe");
        _defaultNameList.add("Baby");

        _userName = _defaultNameList.get(new Random().nextInt(_defaultNameList.size()));
    }

    /**
     * Set the username.
     * @param p_name The username
     */
    public void setUserName(String p_name){ _userName = p_name; }

    /**
     * Gets the username.
     * @return The username
     */
    public String getUserName(){ return _userName; }
}
