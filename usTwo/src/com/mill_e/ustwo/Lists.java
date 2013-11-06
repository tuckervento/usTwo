package com.mill_e.ustwo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Created by Owner on 11/5/13.
 */
public class Lists {

    public interface ListsChangeListener{
        void onListsChange(Lists lists);
    }

    private final HashMap<String, LinkedList<ListItem>> _lists = new HashMap<String, LinkedList<ListItem>>();
    private final Map<String, LinkedList<ListItem>> _safeLists = Collections.unmodifiableMap(_lists);
    private ListsDBOpenHelper _dbOpener;
    private ListsChangeListener _listsChangeListener;

    public void setUpDatabase(Context p_context){
        _dbOpener = new ListsDBOpenHelper(p_context, ListsDBOpenHelper.DATABASE_NAME, null, ListsDBOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = _dbOpener.getWritableDatabase();
        loadDatabase(db);
    }

    public boolean isEmpty() { return _lists.isEmpty(); }

    private void loadDatabase(SQLiteDatabase p_db) {
        String[] result_columns = new String[] { ListsDBOpenHelper.KEY_LIST_NAME, ListsDBOpenHelper.KEY_LIST_ITEM, ListsDBOpenHelper.KEY_CHECKED };

        Cursor cursor = p_db.query(ListsDBOpenHelper.LISTS_DATABASE_TABLE, result_columns, null, null, null, null, null);

        int nameIndex = cursor.getColumnIndexOrThrow(ListsDBOpenHelper.KEY_LIST_NAME);
        int itemIndex = cursor.getColumnIndexOrThrow(ListsDBOpenHelper.KEY_LIST_ITEM);
        int checkedIndex = cursor.getColumnIndexOrThrow(ListsDBOpenHelper.KEY_CHECKED);

        while (cursor.moveToNext())
            addItem(cursor.getString(nameIndex), cursor.getString(itemIndex), cursor.getInt(checkedIndex));

        notifyListener();
    }

    private void notifyListener() {
        if (_listsChangeListener != null)
            _listsChangeListener.onListsChange(this);
    }

    public void addItem(String p_name, String p_item){ addItem(p_name, p_item, 0); }

    public void simulate(){
        addItem("Test List 1", "Item 1");
        addItem("Test List 1", "Item 2");
        addItem("Test List 1", "Item 3");
        addItem("Test List 1", "Item 4");
        addItem("Test List 1", "Item 5", 1);
        addItem("Test List 2", "Item 1", 1);
        addItem("Test List 2", "Item 2", 1);
        addItem("Test List 2", "Item 3", 1);
        addItem("Test List 2", "Item 4", 1);
        addItem("Test List 2", "Item 5");
    }

    public void addItem(String p_name, String p_item, int p_checked){
        if (_lists.containsKey(p_name))
            _lists.get(p_name).add(new ListItem(p_name, p_item, p_checked));
        else{
            LinkedList<ListItem> tempList = new LinkedList<ListItem>();
            tempList.add(new ListItem(p_name, p_item, p_checked));
            _lists.put(p_name, tempList);
        }

        ContentValues newVals = new ContentValues();
        newVals.put(ListsDBOpenHelper.KEY_LIST_NAME, p_name);
        newVals.put(ListsDBOpenHelper.KEY_LIST_ITEM, p_item);
        newVals.put(ListsDBOpenHelper.KEY_CHECKED, p_checked);
        _dbOpener.getWritableDatabase().insert(ListsDBOpenHelper.LISTS_DATABASE_TABLE, null, newVals);
        notifyListener();
    }

    public void clearLists(){
        _lists.clear();
        _dbOpener.getWritableDatabase().delete(ListsDBOpenHelper.LISTS_DATABASE_TABLE, null, null);
        notifyListener();
    }

    public void setListsChangeListener(ListsChangeListener l) { _listsChangeListener = l; }

    public Map<String, LinkedList<ListItem>> getLists() { return _safeLists; }

    public void createList(String p_name){
        _lists.put(p_name, new LinkedList<ListItem>());
        notifyListener();
    }

    public List<ListItem> getList(String p_name){
        return Collections.unmodifiableList(_lists.get(p_name));
    }

    public List<ListItem> removeList(String p_name){
        List<ListItem> list = Collections.unmodifiableList(_lists.remove(p_name));
        notifyListener();
        return list;
    }

    public Set<String> getListNames(){ return _lists.keySet(); }
}
