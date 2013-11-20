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

/**
 * This fragment displays all current lists in an expandable list view.
 */
public class ListsFragment extends Fragment {

    private ListsExpandableListAdapter _adapter;
    private UsTwoService _serviceRef;
    private Lists _lists;
    private Context _context;
    private boolean _isViewable = false;
    private boolean _bound = false;

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
            _adapter = new ListsExpandableListAdapter(_context, _lists);
            ((ExpandableListView) getView().findViewById(R.id.expandableListView_list)).setAdapter(_adapter);
            refreshLists();
            _bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            _serviceRef = null;
            _bound = false;
        }
    };

    private void refreshLists(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (_isViewable) {
                    _adapter = ((ListsExpandableListAdapter) ((ExpandableListView) getView().findViewById(R.id.expandableListView_list))
                            .getExpandableListAdapter());
                    _adapter.updateLists();
                    _adapter.updateNames();
                    _adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        UsTwo.ACTIVE_FRAGMENT = 2;
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
        Lists lists;

        if (_lists != null)
            lists = _lists;
        else
            lists = new Lists();

        _adapter = new ListsExpandableListAdapter(_context, lists);
        ((ExpandableListView)view.findViewById(R.id.expandableListView_list)).setAdapter(_adapter);
        if (_lists != null)
            refreshLists();

        _isViewable = true;
    }

    //region Unbinding
    @Override
    public void onDestroyView() {
        if (!_bound)
            _context.unbindService(_serviceConnection);
        _context = null;
        _lists = null;
        _serviceRef = null;
        _adapter = null;
        _isViewable = false;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (!_bound)
            _context.unbindService(_serviceConnection);
        _context = null;
        _lists = null;
        _serviceRef = null;
        _adapter = null;
        _isViewable = false;
        super.onDestroy();
    }

    @Override
    public void onPause() {
        if (!_bound)
            _context.unbindService(_serviceConnection);
        _context = null;
        _lists = null;
        _serviceRef = null;
        _adapter = null;
        _isViewable = false;
        super.onPause();
    }
    //endregion
}
