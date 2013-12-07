package com.mill_e.ustwo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Class representing the Lists data model.
 */
public class Lists extends UsTwoDataModel{

    private final LinkedList<ListList> _lists = new LinkedList<ListList>();
    private final LinkedList<String> _listNames = new LinkedList<String>();
    private final List<ListList> _safeLists = Collections.unmodifiableList(_lists);
    private ListsDBOpenHelper _dbOpener;

    private static boolean FINISHED_LOADING = false;

    private DataModelChangeListener _listsChangeListener;

    //region UsTwoDataModel
    @Override
    public void setUpDatabase(Context p_context){
        _dbOpener = new ListsDBOpenHelper(p_context, ListsDBOpenHelper.DATABASE_NAME, null, ListsDBOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = _dbOpener.getWritableDatabase();
        loadDatabase(db);
        _dbOpener.close();
    }

    @Override
    public void setDataModelChangeListener(DataModelChangeListener l) {
        this._listsChangeListener = l;
    }

    @Override
    public boolean isEmpty() { return _lists.isEmpty(); }

    @Override
    public void clearModel(){
        _lists.clear();
        _listNames.clear();
        _dbOpener.getWritableDatabase().delete(ListsDBOpenHelper.LISTS_DATABASE_TABLE, null, null);
        _dbOpener.close();
        notifyListener();
    }

    @Override
    public void closeDatabase(){ _dbOpener.close(); }

    private void loadDatabase(SQLiteDatabase p_db) {
        String[] result_columns = new String[] { ListsDBOpenHelper.KEY_TIMESTAMP, ListsDBOpenHelper.KEY_SENDER, ListsDBOpenHelper.KEY_LIST_NAME, ListsDBOpenHelper.KEY_LIST_ITEM, ListsDBOpenHelper.KEY_CHECKED };

        Cursor cursor = p_db.query(ListsDBOpenHelper.LISTS_DATABASE_TABLE, result_columns, null, null, null, null, null);

        int timeStampIndex = cursor.getColumnIndexOrThrow(ListsDBOpenHelper.KEY_TIMESTAMP);
        int senderIndex = cursor.getColumnIndexOrThrow(ListsDBOpenHelper.KEY_SENDER);
        int nameIndex = cursor.getColumnIndexOrThrow(ListsDBOpenHelper.KEY_LIST_NAME);
        int itemIndex = cursor.getColumnIndexOrThrow(ListsDBOpenHelper.KEY_LIST_ITEM);
        int checkedIndex = cursor.getColumnIndexOrThrow(ListsDBOpenHelper.KEY_CHECKED);

        while (cursor.moveToNext())
            addItem((ListItem) new ListItem(cursor.getString(nameIndex), cursor.getString(itemIndex), cursor.getInt(checkedIndex)).setPayloadInfo(cursor.getLong(timeStampIndex), cursor.getString(senderIndex)));

        notifyListener();
        FINISHED_LOADING = true;
    }

    /**
     * Returns a boolean to indicate whether the data model has finished loading from the SQLite database.
     * @return Boolean indicating whether the data model has finished loading from the SQLite database
     */
    public boolean isFinishedLoading(){ return this.FINISHED_LOADING; }

    private void notifyListener() {
        if (_listsChangeListener != null)
            _listsChangeListener.onDataModelChange(this);
    }
    //endregion

    /**
     * Adds a ListItem object to the data model.
     * @param p_item The ListItem to add
     */
    public void addItem(ListItem p_item){
        String listName = p_item.getListName();
        if (_listNames.contains(listName)){
            _lists.get(_listNames.indexOf(listName)).addItem(p_item);
        }
        else{
            ListList tempList = new ListList(listName);
            tempList.addItem(p_item);
            _lists.add(tempList);
            _listNames.add(listName);
        }

        ContentValues newVals = new ContentValues();
        newVals.put(ListsDBOpenHelper.KEY_TIMESTAMP, p_item.getTimeStamp());
        newVals.put(ListsDBOpenHelper.KEY_SENDER, p_item.getSender());
        newVals.put(ListsDBOpenHelper.KEY_LIST_NAME, p_item.getListName());
        newVals.put(ListsDBOpenHelper.KEY_LIST_ITEM, p_item.getItem());
        newVals.put(ListsDBOpenHelper.KEY_CHECKED, p_item.isChecked());
        _dbOpener.getWritableDatabase().insert(ListsDBOpenHelper.LISTS_DATABASE_TABLE, null, newVals);
        _dbOpener.close();
        notifyListener();
    }

    /**
     * Checks if the specified list contains the specified item, will return false if the list does not exist.
     * @param p_list The list to check
     * @param p_item The item to search for
     * @return Boolean indicator
     */
    public boolean containsItem(String p_list, String p_item){
        if (_listNames.contains(p_list))
            return _lists.get(_listNames.indexOf(p_list)).containsItem(p_item);
        return false;
    }

    /**
     * Returns all current lists.
     * @return A list of ListList objects
     */
    public List<ListList> getLists(){ return _safeLists; }

    /**
     * Creates a new list.
     * @param p_name The name of the new list
     */
    public void createList(String p_name){
        _lists.add(new ListList(p_name));
        _listNames.add(p_name);
        notifyListener();
    }

    /**
     * Adds an existing List object to the data model.
     * @param p_list The new list
     */
    public void addList(ListList p_list){
        _lists.add(p_list);
        _listNames.add(p_list.getName());
        notifyListener();
    }

    /**
     * Gets the specified list.
     * @param p_name The name of the list to return
     * @return The requested list
     */
    public List<ListItem> getList(String p_name){
        if (!_listNames.contains(p_name))
            return null;
        return _lists.get(_listNames.indexOf(p_name)).getList();
    }

    /**
     * Removes the specified list.
     * @param p_name The name of the list to remove
     * @return The removed list
     */
    public List<ListItem> removeList(String p_name){
        List<ListItem> tempList = _lists.remove(_listNames.indexOf(p_name)).getList();
        _listNames.remove(p_name);
        notifyListener();
        return tempList;
    }

    /**
     * Gets a list of the list names.
     * @return A list of the list names
     */
    public List<String> getListNames(){ return Collections.unmodifiableList(_listNames); }
}
