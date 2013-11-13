package com.mill_e.ustwo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
/**
 * Created by Owner on 11/5/13.
 */
public class Lists {

    public interface ListsChangeListener{
        void onListsChange(Lists lists);
    }

    private final LinkedList<ListList> _lists = new LinkedList<ListList>();
    private final LinkedList<String> _listNames = new LinkedList<String>();
    private final List<ListList> _safeLists = Collections.unmodifiableList(_lists);
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
        if (_listNames.contains(p_name))
            _lists.get(_listNames.indexOf(p_name)).addItem(p_item, p_checked);
        else{
            ListList tempList = new ListList(p_name);
            tempList.addItem(p_item, p_checked);
            _lists.add(tempList);
            _listNames.add(p_name);
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
        _listNames.clear();
        _dbOpener.getWritableDatabase().delete(ListsDBOpenHelper.LISTS_DATABASE_TABLE, null, null);
        notifyListener();
    }

    public void setListsChangeListener(ListsChangeListener l) { _listsChangeListener = l; }

    public List<ListList> getLists() { return _safeLists; }

    public void createList(String p_name){
        _lists.add(new ListList(p_name));
        _listNames.add(p_name);
        notifyListener();
    }

    public List<ListItem> getList(String p_name){
        return _lists.get(_listNames.indexOf(p_name)).getList();
    }

    public List<ListItem> removeList(String p_name){
        List<ListItem> tempList = _lists.remove(_listNames.indexOf(p_name)).getList();
        _listNames.remove(p_name);
        notifyListener();
        return tempList;
    }

    public List<String> getListNames(){ return Collections.unmodifiableList(_listNames); }
}
