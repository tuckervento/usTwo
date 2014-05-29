package com.mill_e.ustwo.DataModel;

import java.util.Map;
/**
 * An implementation of TransmissionPayload for individual list items.
 */
public class ListItem extends TransmissionPayload {

    public static String JSON_TYPE = "LISTITEM";
    private final String _listName;
    private final String _item;
    private int _checked;

    /**
     * Creates a ListItem
     * @param p_listName Name of List
     * @param p_item List Item
     * @param p_checked 0 - not checked, 1 - checked
     */
    public ListItem(String p_listName, String p_item, int p_checked){
        this._listName = p_listName;
        this._checked = p_checked;
        this._item = p_item;
    }

    /**
     * Creates a ListItem, Checked defaults to 0 (false)
     * @param p_listName Name of List
     * @param p_item List Item
     */
    public ListItem(String p_listName, String p_item){
        this._item = p_item;
        this._checked = 0;
        this._listName = p_listName;
    }

    @Override
    public Map<String, String> getMap(){
        Map<String, String> map = super.getMap();
        map.put("Type", JSON_TYPE);
        map.put("ListName", this._listName);
        map.put("Item", this._item);
        map.put("Checked", String.valueOf(this._checked));
        return map;
    }

    /**
     * Get the name of this item's list.
     * @return The list's name
     */
    public String getListName() { return this._listName; }

    /**
     * Get the text of the item.
     * @return The item's text
     */
    public String getItem() { return this._item; }

    /**
     * Get an int indicating whether the item is checked.
     * @return 0 = unchecked, 1 = checked
     */
    public int isChecked() { return this._checked; }

    /**
     * Sets the checked status of the item.
     * @param p_checked 0 = unchecked, 1 = checked
     */
    public void setChecked(int p_checked) { this._checked = p_checked; }

    @Override
    public String toString() { return _item; }
}
