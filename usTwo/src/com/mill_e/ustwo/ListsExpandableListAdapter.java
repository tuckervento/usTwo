package com.mill_e.ustwo;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Custom adapter for the expandable Lists view.
 */
public class ListsExpandableListAdapter implements ExpandableListAdapter {

    private final DataSetObservable _dataSetObservable = new DataSetObservable();
    private List<ListList> _listsList;
    private final LayoutInflater _inflater;
    private List<String> _listNames;
    private final Lists _lists;

    public ListsExpandableListAdapter(Context p_context,  Lists p_lists){
        _lists = p_lists;
        _listNames = _lists.getListNames();
        _listsList = _lists.getLists();
        _inflater = (LayoutInflater) p_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateLists(){
        _listsList = _lists.getLists();
    }

    public void updateNames() {
        _listNames = _lists.getListNames();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver p_dataSetObserver) {
        _dataSetObservable.registerObserver(p_dataSetObserver);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver p_dataSetObserver) {
        _dataSetObservable.unregisterObserver(p_dataSetObserver);
    }

    public void notifyDataSetChanged(){
        _dataSetObservable.notifyChanged();
    }

    public void notifyDataSetInvalidated(){
        _dataSetObservable.notifyInvalidated();
    }

    @Override
    public int getGroupCount() {
        return _listsList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return _listsList.get(i).sizeOfList();
    }

    @Override
    public Object getGroup(int i) {
        return _listsList.get(i);
    }

    @Override
    public Object getChild(int i, int i2) {
        return _listsList.get(i).getItem(i2).getItem();
    }

    @Override
    public long getGroupId(int i) {
        return (long)i;
    }

    @Override
    public long getChildId(int i, int i2) {
        return (long)i2;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null){ //If we start using a different layout for expanded vs not, this will need to be changed
            convertView = _inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
            convertView.setTag(_listNames.get(groupPosition));
            ((TextView)convertView.findViewById(android.R.id.text1)).setText(_listNames.get(groupPosition));
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = _inflater.inflate(R.layout.list_item_layout, parent, false);
            ListItem item = _listsList.get(groupPosition).getItem(childPosition);
            convertView.setTag(item.getItem());
            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox_list_item);
            checkBox.setText(item.getItem());
            checkBox.setChecked(item.isChecked() == 1);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return _lists.isEmpty();
    }

    @Override
    public void onGroupExpanded(int i) {

    }

    @Override
    public void onGroupCollapsed(int i) {

    }

    @Override
    public long getCombinedChildId(long l, long l2) {
        return l * 10000L + l2;
    }

    @Override
    public long getCombinedGroupId(long l) {
        return l * 10000L;
    }
}
