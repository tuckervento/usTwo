package com.mill_e.ustwo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;

/**
 * Custom PopupWindow for editing list items.
 */
public class ListItemEditPopup extends PopupWindow {

    /**
     * This interface allows the popup to callback for edits.
     */
    public interface ListItemEditListener{
        public void editFinished(String p_newItem);
    }

    private ListItemEditListener _listener;

    public ListItemEditPopup(Context p_context, String p_item){
        super(p_context);

        setContentView(LayoutInflater.from(p_context).inflate(R.layout.popupwindow_list_item_edit, null));
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        View view = getContentView();
        setFocusable(true);

        final EditText editItem = (EditText) view.findViewById(R.id.editText_list_item_edit);
        editItem.setText(p_item);

        view.findViewById(R.id.button_edit_listItem_popupClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        view.findViewById(R.id.button_edit_listItem_popupSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _listener.editFinished(editItem.getText().toString());
                dismiss();
            }
        });
    }

    public void setListener(ListItemEditListener p_listener){
        _listener = p_listener;
    }
}
