package com.mill_e.ustwo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
/**
 * Created by Owner on 11/7/13.
 */
public class ListList {
    private final LinkedList<ListItem>_items;
    private String _name;

    public ListList(String p_name){
        this._items = new LinkedList<ListItem>();
        this._name = p_name;
    }

    public boolean addItem(String p_item, int p_checked){ return this._items.add(new ListItem(this._name, p_item, p_checked)); }

    public ListItem getItem(int p_idx){ return this._items.get(p_idx); }

    public void setName(String p_name){ this._name = p_name; }

    public int sizeOfList(){ return this._items.size(); }

    public List<ListItem> getList(){ return Collections.unmodifiableList(this._items); }

    public String getName(){ return this._name; }

    public void isItemChecked(int p_idx) { this._items.get(p_idx).isChecked(); }

    public ListItem removeItem(int p_idx){ return this._items.remove(p_idx); }
}
