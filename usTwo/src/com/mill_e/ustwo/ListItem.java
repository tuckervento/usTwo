package com.mill_e.ustwo;

/**
 * Created by Owner on 11/5/13.
 */
public class ListItem extends TransmissionPayload {

    private final String _listName;
    private final String _item;
    private final int _checked;

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

    public String getListName() { return this._listName; }

    public String getItem() { return this._item; }

    public int isChecked() { return this._checked; }
}
