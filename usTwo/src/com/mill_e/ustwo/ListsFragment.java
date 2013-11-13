package com.mill_e.ustwo;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.LinkedList;
import java.util.List;

/**
 * This fragment displays all current lists in an expandable list view.
 */
public class ListsFragment extends Fragment {

    private ListsExpandableListAdapter _adapter;
    private UsTwoService _serviceRef;
    private Lists _lists;
    private Context _context;
    private boolean _isViewable = false;

    private ServiceConnection _serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            _serviceRef = ((UsTwoService.UsTwoBinder) iBinder).getService();
            _lists = _serviceRef.getListsModel();

            _lists.setDataModelChangeListener(new UsTwoDataModel.DataModelChangeListener() {
                @Override
                public void onDataModelChange(UsTwoDataModel lists) {
                    refreshLists();
                }
            });
            refreshLists();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) { _serviceRef = null; }
    };

    private void refreshLists(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (_isViewable){
                    ListsExpandableListAdapter adapter = ((ListsExpandableListAdapter) ((ExpandableListView) getView().findViewById(R.id.expandableListView_list))
                            .getExpandableListAdapter());
                    adapter.updateLists(_lists.getLists());
                    adapter.updateNames(_lists.getListNames());
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void simulate(){ _lists.simulate(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        UsTwoHome.ACTIVE_FRAGMENT = 2;
        View v = inflater.inflate(R.layout.fragment_list_view, container, false);

        //set button adapters
        v.findViewById(R.id.button_create_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.root_view, new ListsCreateNewFragment(_serviceRef)).addToBackStack(null).commit();
            }
        });

        v.findViewById(R.id.button_add_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.root_view, new ListsAddItemFragment(_serviceRef, _lists.getListNames())).addToBackStack(null).commit();
            }
        });

        v.findViewById(R.id.button_back_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
        _context = container.getContext();
        _context.bindService(new Intent(_context, UsTwoService.class), _serviceConnection, Context.BIND_WAIVE_PRIORITY);

        _isViewable = true;

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        List<ListList> lists;
        List<String> listNames;

        if (_lists != null){
            lists = _lists.getLists();
            listNames = _lists.getListNames();
        }else{
            lists = new LinkedList<ListList>();
            listNames = new LinkedList<String>();
        }
        _adapter = new ListsExpandableListAdapter(_context, lists, listNames);
        ((ExpandableListView)view.findViewById(R.id.expandableListView_list)).setAdapter(_adapter);
        if (_lists != null)
            refreshLists();

        _isViewable = true;
    }

    //region Unbinding
    @Override
    public void onDestroyView() {
        try{ _context.unbindService(_serviceConnection); }catch(IllegalArgumentException e){ e.printStackTrace(); }
        _isViewable = false;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        try{ _context.unbindService(_serviceConnection); }catch(IllegalArgumentException e){ e.printStackTrace(); }
        _isViewable = false;
        super.onDestroy();
    }

    @Override
    public void onPause() {
        try{ _context.unbindService(_serviceConnection); }catch(IllegalArgumentException e){ e.printStackTrace(); }
        super.onPause();
    }
    //endregion
}
