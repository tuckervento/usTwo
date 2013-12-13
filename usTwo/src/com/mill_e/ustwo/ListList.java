package com.mill_e.ustwo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Object representing an UsTwo List.
 */
public class ListList extends TransmissionPayload{
    private final LinkedList<ListItem>_items;
    public static String JSON_TYPE = "LIST";
    private String _name;

    /**
     * Creates a new ListList.
     * @param p_name Name of the new list
     */
    public ListList(String p_name){
        this._items = new LinkedList<ListItem>();
        this._name = p_name;
    }

    @Override
    public Map<String, String> getMap(){
        Map<String, String> map = super.getMap();
        map.put("Type", JSON_TYPE);
        map.put("Name", this._name);
        return map;
    }

    /**
     * Adds an item to the list.
     * @param p_item Name of the item to add
     * @return Boolean indicating successful add
     */
    public boolean addItem(ListItem p_item){ return this._items.add(p_item); }

    /**
     * Checks if the associated list contains the provided item.
     * @param p_item The item to check
     * @return Boolean indicating containment of the item
     */
    public boolean containsItem(String p_item){
        for (int i = 0; i < _items.size(); i++)
            if (_items.get(i).toString().contentEquals(p_item))
                return true;

        return false;
    }

    /**
     * Gets the item at the specified index.
     * @param p_idx Index to retrieve
     * @return The item at the index
     */
    public ListItem getItem(int p_idx){ return this._items.get(p_idx); }

    /**
     * Sets the name of the list.
     * @param p_name The name to set
     */
    public void setName(String p_name){ this._name = p_name; }

    /**
     * Gets the size of the list.
     * @return Size of the list
     */
    public int sizeOfList(){ return this._items.size(); }

    /**
     * Gets the list.
     * @return The list of ListItem objects
     */
    public List<ListItem> getList(){ return Collections.unmodifiableList(this._items); }

    /**
     * Gets the name of the list.
     * @return Name of the list
     */
    public String getName(){ return this._name; }

    /**
     * Returns int indicating whether the item at the specified index is checked or not.
     * @param p_idx Index to check
     * @return 0 = unchecked, 1 = checked
     */
    public int isItemChecked(int p_idx) { return this._items.get(p_idx).isChecked(); }

    /**
     * Sets the specified item's checked status.
     * @param p_item The item to check
     * @param p_checked The check value to set
     */
    public void checkItem(String p_item, int p_checked) {
        int ind = indexOfItem(p_item);
        if (ind != -1)
            _items.get(ind).setChecked(p_checked);
    }

    /**
     * Removes and returns the item at the specified index.
     * @param p_idx The index to remove
     * @return The item that was located at the index
     */
    public ListItem removeItem(int p_idx){ return this._items.remove(p_idx); }

    private int indexOfItem(String p_item){
        for (int i = 0; i < _items.size(); i++)
            if (_items.get(i).getItem().contentEquals(p_item))
                return i;
        return -1;
    }
}
