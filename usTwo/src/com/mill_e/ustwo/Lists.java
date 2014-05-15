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

    public interface ListItemCheckedChangedListener {
        public void onListItemCheckedChanged(String p_listname, String p_item, int p_checked);
    }

    private final LinkedList<ListList> _lists = new LinkedList<ListList>();
    private final LinkedList<String> _listNames = new LinkedList<String>();
    private final List<ListList> _safeLists = Collections.unmodifiableList(_lists);
    private ListsDBOpenHelper _dbOpener;

    private static boolean FINISHED_LOADING = false;

    private DataModelChangeListener _listsChangeListener;

    private ListItemCheckedChangedListener _listCheckedChangeListener;

    public void setListItemCheckedChangedListener(ListItemCheckedChangedListener l){ this._listCheckedChangeListener = l; }

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

        int timestampIndex = cursor.getColumnIndexOrThrow(ListsDBOpenHelper.KEY_TIMESTAMP);
        int senderIndex = cursor.getColumnIndexOrThrow(ListsDBOpenHelper.KEY_SENDER);
        int nameIndex = cursor.getColumnIndexOrThrow(ListsDBOpenHelper.KEY_LIST_NAME);
        int itemIndex = cursor.getColumnIndexOrThrow(ListsDBOpenHelper.KEY_LIST_ITEM);
        int checkedIndex = cursor.getColumnIndexOrThrow(ListsDBOpenHelper.KEY_CHECKED);

        while (cursor.moveToNext())
            addItem((ListItem) new ListItem(cursor.getString(nameIndex), cursor.getString(itemIndex), cursor.getInt(checkedIndex)).setPayloadInfo(cursor.getLong(timestampIndex), cursor.getString(senderIndex)));
        cursor.close();
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
    public ListItem addItem(ListItem p_item){
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

        return p_item;
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
     * Edits an existing item in the List data model.
     * @param p_timestamp The timestamp of the item
     * @param p_listName The name of the list containing the item
     * @param p_listItem The item
     * @param p_checked The item's checked status
     * @return The edited ListItem
     */
    public ListItem editItem(long p_timestamp, String p_listName, String p_listItem, int p_checked){
        boolean found = false;
        String sender = "";

        for (int i = 0; i < _lists.size(); i++)
            for (int j = 0; j < _lists.get(i).sizeOfList(); j++)
                if (_lists.get(i).getItem(j).getTimeStamp() == p_timestamp){
                    sender = _lists.get(i).getItem(j).getSender();
                    found = true;
                    _lists.get(i).removeItem(j);
                    break;
                }

        if (!found)
            return null;

        _dbOpener.getWritableDatabase().delete(ListsDBOpenHelper.LISTS_DATABASE_TABLE, ListsDBOpenHelper.KEY_TIMESTAMP + " = " + String.valueOf(p_timestamp), null);
        _dbOpener.close();

        return addItem((ListItem) new ListItem(p_listName, p_listItem, p_checked).setPayloadInfo(p_timestamp, sender));
    }

    /**
     * Removes an existing item in the List data model.
     * @param p_timestamp The timestamp of the item to remove
     * @param p_listName The name of the list containing the item
     * @return The removed ListItem
     */
    public void removeItem(long p_timestamp, String p_listName){

        for (int i = 0; i < _lists.size(); i++)
            if (_lists.get(i).getName().contentEquals(p_listName))
                _lists.get(i).removeItem(p_timestamp);

        _dbOpener.getWritableDatabase().delete(ListsDBOpenHelper.LISTS_DATABASE_TABLE, ListsDBOpenHelper.KEY_TIMESTAMP + " = " + String.valueOf(p_timestamp), null);
        _dbOpener.close();
    }

    /**
     * Sets the checked value for the specified ListItem.
     * @param p_listName The name of the list containing the item
     * @param p_listItem The item
     * @param p_checked 0 = unchecked, 1 = checked
     */
    public void checkItem(String p_listName, String p_listItem, int p_checked){
        if (checkItemWithoutNotifyingListener(p_listName, p_listItem, p_checked))
            _listCheckedChangeListener.onListItemCheckedChanged(p_listName, p_listItem, p_checked);
    }

    /**
     * Sets the checked value for the specified ListItem without notifying the CheckedChangedListener.
     * @param p_listName The name of the list containing the item
     * @param p_listItem The item
     * @param p_checked 0 = unchecked, 1 = checked
     * @return Boolean indicating whether the check was successful
     */
    public boolean checkItemWithoutNotifyingListener(String p_listName, String p_listItem, int p_checked){
        if (!containsItem(p_listName, p_listItem))
            return false;

        _lists.get(_listNames.indexOf(p_listName)).checkItem(p_listItem, p_checked);

        ContentValues newVals = new ContentValues();
        newVals.put(ListsDBOpenHelper.KEY_CHECKED, p_checked);

        _dbOpener.getWritableDatabase().update(ListsDBOpenHelper.LISTS_DATABASE_TABLE, newVals,
                ListsDBOpenHelper.KEY_LIST_NAME + " = \"" + p_listName + "\" AND " + ListsDBOpenHelper.KEY_LIST_ITEM + " = \"" + p_listItem + "\"", null);
        _dbOpener.close();
        return true;
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
