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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
/**
 * Created by Owner on 11/5/13.
 */
public class ListsExpandableListAdapter implements ExpandableListAdapter {

    private final DataSetObservable _dataSetObservable = new DataSetObservable();
    private Map<String, LinkedList<ListItem>> _lists;
    private final LayoutInflater _inflater;
    private String[] _listNames;

    public ListsExpandableListAdapter(Context p_context,  Map<String, LinkedList<ListItem>> p_lists){
        _lists = p_lists;
        _inflater = (LayoutInflater) p_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        refreshListNames();
    }

    public void updateLists(Map<String, LinkedList<ListItem>> p_lists){
        _lists = p_lists;
    }

    private void refreshListNames(){
        Iterator<Map.Entry<String, LinkedList<ListItem>>> entries = _lists.entrySet().iterator();
        _listNames = new String[_lists.size()];
        int i = 0;
        while (entries.hasNext()){
            _listNames[i] = entries.next().getKey();
            i++;
        }
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
        refreshListNames(); //We might need to be passed a new Map of the lists as well...I'm not sure if that is passed by reference or not
        _dataSetObservable.notifyChanged();
    }

    public void notifyDataSetInvalidated(){

        refreshListNames();
        _dataSetObservable.notifyInvalidated();
    }

    @Override
    public int getGroupCount() {
        return _lists.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return _lists.get(_listNames[i]).size();
    }

    @Override
    public Object getGroup(int i) {
        return _lists.get(_listNames[i]);
    }

    @Override
    public Object getChild(int i, int i2) {
        return _lists.get(_listNames[i]).get(i2).getItem();
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
            convertView.setTag(_listNames[groupPosition]);
            ((TextView)convertView.findViewById(android.R.id.text1)).setText(_listNames[groupPosition]);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = _inflater.inflate(R.layout.list_item_layout, parent, false);
            ListItem item = _lists.get(_listNames[groupPosition]).get(childPosition);
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
