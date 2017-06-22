package com.shyky.library.adapter.base;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Shyky on 2017/6/21.
 */
public abstract class BaseAdapter extends RecyclerView.Adapter {
    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    public abstract Object getItem(int position);

    public boolean isEmpty() {
        return getItemCount() == 0;
    }
}