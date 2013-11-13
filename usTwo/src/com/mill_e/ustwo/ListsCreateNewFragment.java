package com.mill_e.ustwo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * This fragment allows the user to create a new list.
 */
public class ListsCreateNewFragment extends Fragment{

    private final UsTwoService _usTwoService;

    public ListsCreateNewFragment(UsTwoService p_usTwoService){
        _usTwoService = p_usTwoService;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.list_create_list_layout, container, false);

        v.findViewById(R.id.button_list_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _usTwoService.createList(((EditText)v.findViewById(R.id.editText_list_name)).getText().toString());
                getFragmentManager().popBackStack();
            }
        });

        v.findViewById(R.id.button_back_list_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { getFragmentManager().popBackStack(); }
        });

        return v;
    }
}