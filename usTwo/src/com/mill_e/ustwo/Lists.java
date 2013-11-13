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

    private DataModelChangeListener _listsChangeListener;

    //region UsTwoDataModel
    @Override
    public void setUpDatabase(Context p_context){
        _dbOpener = new ListsDBOpenHelper(p_context, ListsDBOpenHelper.DATABASE_NAME, null, ListsDBOpenHelper.DATABASE_VERSION);
        SQLiteDatabase db = _dbOpener.getWritableDatabase();
        loadDatabase(db);
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
        notifyListener();
    }

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
            _listsChangeListener.onDataModelChange(this);
    }
    //endregion

    /**
     * Simulate mass adding of items.
     */
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

    /**
     * Adds a ListItem object to the data model.
     * @param p_item The ListItem to add
     */
    public void addItem(ListItem p_item){ addItem(p_item.getListName(), p_item.getItem(), p_item.isChecked());}

    /**
     * Adds an item to the specified list.
     * @param p_name The name of the list to add to
     * @param p_item The item to add
     */
    public void addItem(String p_name, String p_item){ addItem(p_name, p_item, 0); }

    /**
     * Adds an item to the specified list, with a specified checked value.
     * @param p_name The name of the list to add to
     * @param p_item The item to add
     * @param p_checked 0 = item unchecked, 1 = item checked
     */
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

    /**
     * Returns all current lists.
     * @return A list of ListList objects
     */
    public List<ListList> getLists() { return _safeLists; }

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
    public void createList(ListList p_list){
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
