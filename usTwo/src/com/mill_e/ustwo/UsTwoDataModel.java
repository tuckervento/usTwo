package com.mill_e.ustwo;

import android.content.Context;

/**
 * Abstract class defining characteristics of the UsTwo data models.
 */
public abstract class UsTwoDataModel {

    /**
     * Determines whether the data model is empty.
     * @return Boolean indicating whether the associated data model is empty
     */
    public abstract boolean isEmpty();

    /**
     * This interface listens to changes in a given UsTwoDataModel.
     */
    public interface DataModelChangeListener{

        /**
         * Called when the associated data model changes.
         * @param dataModel The associated data model object
         */
        public void onDataModelChange(UsTwoDataModel dataModel);
    }

    /**
     * Sets up the SQLite database for the data model.
     * @param p_context Context used to access SQLite databases
     */
    public abstract void setUpDatabase(Context p_context);

    /**
     * Sets the DataModelChangeListener for this data model.
     * @param l The DataModelChangeListener
     */
    public abstract void setDataModelChangeListener(DataModelChangeListener l);

    /**
     * Clears the associated UsTwoDataModel.
     */
    public abstract void clearModel();
}
