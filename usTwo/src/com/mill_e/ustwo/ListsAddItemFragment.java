package com.mill_e.ustwo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

/**
 * This fragment allows the user to add an item to a given list.
 */
public class ListsAddItemFragment extends Fragment {

    private class ListNameAdapter extends BaseAdapter implements SpinnerAdapter {
        private final List<String> _listNames;

        public ListNameAdapter(List<String> p_listNames){
            _listNames = p_listNames;
        }

        @Override
        public int getCount() {
            return _listNames.size();
        }

        @Override
        public Object getItem(int i) {
            return _listNames.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return getDropDownView(i, view, viewGroup);
        }

        @Override
        public View getDropDownView(int p_position, View p_convertView, ViewGroup p_parent){
            if (p_convertView == null)
                p_convertView = View.inflate(p_parent.getContext(), android.R.layout.simple_list_item_1, null);
            ((TextView)p_convertView.findViewById(android.R.id.text1)).setText(_listNames.get(p_position));
            return p_convertView;
        }
    }

    private final ListNameAdapter _adapter;
    private String _listName;

    public ListsAddItemFragment(List<String> p_listNames){
        _adapter = new ListNameAdapter(p_listNames);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.list_add_item_layout, container, false);
        final Spinner spinner = (Spinner)v.findViewById(R.id.spinner_list_names);
        final UsTwoService service = ((UsTwo)getActivity()).getService();

        spinner.setAdapter(_adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                _listName = (String)adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        v.findViewById(R.id.button_add_list_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    service.addListItem(_listName, ((EditText)v.findViewById(R.id.editText_list_item)).getText().toString(), 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                getFragmentManager().popBackStack();
            }
        });

        return v;
    }
}
