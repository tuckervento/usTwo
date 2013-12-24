package com.mill_e.ustwo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

/**
 * This fragment displays all current lists in an expandable list view.
 */
public class ListsFragment extends Fragment {

    private ListsExpandableListAdapter _adapter;
    private Lists _lists;
    private boolean _isViewable = false;

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
                getFragmentManager().beginTransaction().replace(R.id.root_view, new ListsCreateNewFragment()).addToBackStack(null).commit();
            }
        });

        v.findViewById(R.id.button_add_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.root_view, new ListsAddItemFragment(_lists.getListNames())).addToBackStack(null).commit();
            }
        });

        v.findViewById(R.id.button_back_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        _isViewable = true;

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        UsTwoService serviceRef = ((UsTwo)getActivity()).getService();

        if (serviceRef != null){
            _lists = serviceRef.getListsModel();
        }else
            _lists = new Lists();

        _lists.setDataModelChangeListener(new UsTwoDataModel.DataModelChangeListener() {
            @Override
            public void onDataModelChange(UsTwoDataModel lists) {
                refreshLists();
            }
        });

        _adapter = new ListsExpandableListAdapter(getActivity(), _lists);
        _adapter.setService(((UsTwo) getActivity()).getService());
        ((ExpandableListView)view.findViewById(R.id.expandableListView_list)).setAdapter(_adapter);
        if (_lists != null)
            refreshLists();
        _isViewable = true;
    }

    //region Unbinding
    @Override
    public void onDestroyView() {
        _lists = null;
        if (_adapter != null){
            _adapter.cleanUp();
            _adapter = null;
        }
        _isViewable = false;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        _lists = null;
        if (_adapter != null){
            _adapter.cleanUp();
            _adapter = null;
        }
        _isViewable = false;
        super.onDestroy();
    }

    @Override
    public void onPause() {
        _lists = null;
        if (_adapter != null){
            _adapter.cleanUp();
            _adapter = null;
        }
        _isViewable = false;
        super.onPause();
    }
    //endregion
}
