package com.mill_e.ustwo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Object representing an UsTwo List.
 */
public class ListList extends TransmissionPayload{
    private final LinkedList<ListItem>_items;
    private String _name;

    /**
     * Creates a new ListList.
     * @param p_name Name of the new list
     */
    public ListList(String p_name){
        this._items = new LinkedList<ListItem>();
        this._name = p_name;
    }

    /**
     * Adds an item to the list.
     * @param p_item Name of the item to add
     * @param p_checked 0 = not checked, 1 = checked
     * @return Boolean indicating successful add
     */
    public boolean addItem(String p_item, int p_checked){ return this._items.add(new ListItem(this._name, p_item, p_checked)); }

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
     * Removes and returns the item at the specified index.
     * @param p_idx The index to remove
     * @return The item that was located at the index
     */
    public ListItem removeItem(int p_idx){ return this._items.remove(p_idx); }
}
