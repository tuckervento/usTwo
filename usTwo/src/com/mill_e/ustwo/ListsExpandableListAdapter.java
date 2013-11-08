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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/**
 * Created by Owner on 11/5/13.
 */
public class ListsExpandableListAdapter implements ExpandableListAdapter {

    private final DataSetObservable _dataSetObservable = new DataSetObservable();
    private List<ListList> _lists;
    private final LayoutInflater _inflater;
    private List<String> _listNames;

    public ListsExpandableListAdapter(Context p_context,  List<ListList> p_lists, List<String> p_names){
        _lists = p_lists;
        _listNames = p_names;
        _inflater = (LayoutInflater) p_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateLists(List<ListList> p_lists){
        _lists = p_lists;
    }

    public void updateNames(List<String> p_listNames) {
        _listNames = p_listNames;
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
        return _lists.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return _lists.get(i).sizeOfList();
    }

    @Override
    public Object getGroup(int i) {
        return _lists.get(i);
    }

    @Override
    public Object getChild(int i, int i2) {
        return _lists.get(i).getItem(i2).getItem();
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
            ListItem item = _lists.get(groupPosition).getItem(childPosition);
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
