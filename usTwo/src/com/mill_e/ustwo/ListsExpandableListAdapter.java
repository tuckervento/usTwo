package com.mill_e.ustwo;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.PopupWindow;
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
    private PopupWindow _longPressWindow;
    private ListItemEditPopup _editWindow;
    private UsTwoService _service;
    private Context _context;

    public ListsExpandableListAdapter(Context p_context,  Lists p_lists){
        _context = p_context;
        _lists = p_lists;
        _listNames = _lists.getListNames();
        _listsList = _lists.getLists();
        _inflater = (LayoutInflater) p_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setService(UsTwoService p_service){
        _service = p_service;
    }

    public void cleanUp(){
        _service = null;
        _context = null;

        if (_longPressWindow != null && _longPressWindow.isShowing()){
            _longPressWindow.dismiss();
            _longPressWindow = null;
        }

        if (_editWindow != null && _editWindow.isShowing()){
            _editWindow.dismiss();
            _editWindow = null;
        }
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
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, final ViewGroup parent) {
        if (convertView == null)
            convertView = _inflater.inflate(R.layout.list_item_layout, parent, false);

        final ListItem item = _listsList.get(groupPosition).getItem(childPosition);
        convertView.setTag(item.getItem());
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox_list_item);
        checkBox.setText(item.getItem());
        checkBox.setChecked(item.isChecked() == 1);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _lists.checkItem(item.getListName(), item.getItem(), ((CheckBox)view).isChecked() ? 1 : 0);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                View windowView = _inflater.inflate(R.layout.popupwindow_list_longpress_layout, null);
                _longPressWindow = new PopupWindow(windowView);

                windowView.findViewById(R.id.button_list_longpress_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _editWindow = new ListItemEditPopup(_context, item.getItem());
                        _editWindow.setListener(new ListItemEditPopup.ListItemEditListener() {
                            @Override
                            public void editFinished(String p_newItem) {
                                _service.editListItem(item.getTimeStamp(), item.getListName(), item.getItem(), p_newItem, item.isChecked());
                                notifyDataSetChanged();
                            }
                        });
                        _editWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
                        _longPressWindow.dismiss();
                    }
                });

                windowView.findViewById(R.id.button_list_longpress_remove).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        _service.removeListItem(item.getItem(), item.getTimeStamp(), item.getListName());
                        notifyDataSetInvalidated();
                        _longPressWindow.dismiss();
                    }
                });

                _longPressWindow.setTouchable(true);
                _longPressWindow.setOutsideTouchable(true);
                _longPressWindow.setBackgroundDrawable(new BitmapDrawable());
                _longPressWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
                _longPressWindow.update(0, 0, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                return true;
            }
        });

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
