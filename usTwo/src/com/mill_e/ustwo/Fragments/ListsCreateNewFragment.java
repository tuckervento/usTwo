package com.mill_e.ustwo.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mill_e.ustwo.R;
import com.mill_e.ustwo.UsTwo;
import com.mill_e.ustwo.UsTwoService;

/**
 * This fragment allows the user to create a new list.
 */
class ListsCreateNewFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.list_create_list_layout, container, false);
        final UsTwoService service = ((UsTwo)getActivity()).getService();
        v.findViewById(R.id.button_list_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service.createList(((EditText)v.findViewById(R.id.editText_list_name)).getText().toString());
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
