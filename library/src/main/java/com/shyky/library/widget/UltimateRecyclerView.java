package com.shyky.library.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.WrapperListAdapter;

import com.shyky.library.adapter.ExpandableAdapter;
import com.shyky.library.adapter.HeaderViewListAdapter;
import com.shyky.library.adapter.ListAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Shyky on 2017/6/21.
 */
public class UltimateRecyclerView extends RecyclerView {
    ////////////
    ////AdapterView -- Common
    /**
     * View to show if there are no items to show.
     */
    private View emptyView;
    //////////////////////
    //ListView
    private Drawable divider;
    private int dividerHeight;
    private boolean dividerIsOpaque;
    private ArrayList<FixedView> headerViews;//= Lists.newArrayList();
    private ArrayList<FixedView> footerViews;// = Lists.newArrayList();
    private boolean headerDividersEnabled;
    private boolean footerDividersEnabled;
    private boolean areAllItemsSelectable = true;
    /**
     * The listener that receives notifications when an item is clicked.
     */
    private OnItemClickListener onItemClickListener;
    /**
     * The adapter containing the data to be displayed by this view
     */
    private ListAdapter listAdapter;
    /////////////////////////
    //GridView
    private int columnWidth;
    private int requestedColumnWidth;
    private int requestedNumColumns;
    private int horizontalSpacing;
    private int requestedHorizontalSpacing;
    private int verticalSpacing;
    ///////////////////////
    /**
     * The indicator drawn next to a group.
     */
    private Drawable groupIndicator;
    /**
     * The indicator drawn next to a child.
     */
    private Drawable childIndicator;
    /**
     * Gives us Views through group+child positions
     */
    private ExpandableListAdapter expandableListAdapter;
    /**
     * Gives us Views through group+child positions
     */
    private ExpandableAdapter expandableAdapter;
    private OnGroupClickListener onGroupClickListener;
    private OnGroupExpandListener onGroupExpandListener;
    private OnGroupCollapseListener onGroupCollapseListener;

    ///////////////////
    //RecyclerView

    /**
     * Should be used by subclasses to listen to changes in the dataset
     */
    private RecyclerView.AdapterDataObserver dataSetObserver;
    /**
     * The adapter containing the data to be displayed by this view
     */
    protected Adapter internalAdapter;
    private GestureDetector gestureDetector;

    private final class InternalAdapterDataObserver extends AdapterDataObserver {
        protected AdapterDataObserver internalDataObserver;

        public InternalAdapterDataObserver() {
            // 通过反射获取父类观察者对象
            try {
                final Field field = getDeclaredField(RecyclerView.class, "mObserver");
                if (field != null) {
                    internalDataObserver = (AdapterDataObserver) field.get(UltimateRecyclerView.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Field getDeclaredField(Class clz, String fieldName) {
            Field field;
            for (; clz != Object.class; clz = clz.getSuperclass()) {
                try {
                    field = clz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return field;
                } catch (Exception e) {
                }
            }
            return null;
        }

        @Override
        public void onChanged() {
            if (internalDataObserver != null) {
                internalDataObserver.onChanged();
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (internalDataObserver != null) {
                internalDataObserver.onItemRangeChanged(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if (internalDataObserver != null) {
                internalDataObserver.onItemRangeChanged(positionStart, itemCount, payload);
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (internalDataObserver != null) {
                internalDataObserver.onItemRangeInserted(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (internalDataObserver != null) {
                internalDataObserver.onItemRangeRemoved(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (internalDataObserver != null) {
                internalDataObserver.onItemRangeMoved(fromPosition, toPosition, itemCount);
            }
        }
    }

    /**
     * A class that represents a fixed view in a list, for example a header at the top
     * or a footer at the bottom.
     */
    public class FixedView {
        /**
         * The view to add to the list
         */
        public View view;
        /**
         * The data backing the view. This is returned from {@link ListAdapter#getItem(int)}.
         */
        public Object data;
        /**
         * <code>true</code> if the fixed view should be selectable in the list
         */
        public boolean isSelectable;
    }

    /**
     * Interface definition for a callback to be invoked when an item in this
     * AdapterView has been clicked.
     */
    public interface OnItemClickListener {
        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param parent   The AdapterView where the click happened.
         * @param view     The view within the AdapterView that was clicked (this
         *                 will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param id       The row id of the item that was clicked.
         */
        void onItemClick(ViewGroup parent, View view, int position, long id);
    }

    /**
     * Interface definition for a callback to be invoked when a group in this
     * expandable list has been clicked.
     */
    public interface OnGroupClickListener {
        /**
         * Callback method to be invoked when a group in this expandable list has
         * been clicked.
         *
         * @param parent        The ExpandableListConnector where the click happened
         * @param v             The view within the expandable list/ListView that was clicked
         * @param groupPosition The group position that was clicked
         * @param id            The row id of the group that was clicked
         * @return True if the click was handled
         */
        boolean onGroupClick(UltimateRecyclerView parent, View v, int groupPosition, long id);
    }

    /**
     * Used for being notified when a group is expanded
     */
    public interface OnGroupExpandListener {
        /**
         * Callback method to be invoked when a group in this expandable list has
         * been expanded.
         *
         * @param groupPosition The group position that was expanded
         */
        void onGroupExpand(int groupPosition);
    }

    /**
     * Used for being notified when a group is collapsed
     */
    public interface OnGroupCollapseListener {
        /**
         * Callback method to be invoked when a group in this expandable list has
         * been collapsed.
         *
         * @param groupPosition The group position that was collapsed
         */
        void onGroupCollapse(int groupPosition);
    }

    private class ItemClickListener implements RecyclerView.OnItemTouchListener {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && onItemClickListener != null && gestureDetector.onTouchEvent(e)) {
                final int position = rv.getChildAdapterPosition(childView);
                onItemClickListener.onItemClick(rv, childView, position - getHeaderViewsCount(), rv.getAdapter().getItemId(position));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public UltimateRecyclerView(Context context) {
        this(context, null);
    }

    public UltimateRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UltimateRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        headerViews = new ArrayList<>();
        footerViews = new ArrayList<>();
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        addOnItemTouchListener(new ItemClickListener());
        // Set's the default LayoutManager
        setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Sets the adapter that provides data to this view.
     *
     * @param adapter The adapter that provides data to this view.
     */
    public void setAdapter(ExpandableListAdapter adapter) {
        expandableListAdapter = adapter;

        // Link the ListView (superclass) to the expandable list data through the connector
//        super.setAdapter(mConnector);
    }

    /**
     * Sets the adapter that provides data to this view.
     *
     * @param adapter The adapter that provides data to this view.
     */
    public void setAdapter(ExpandableAdapter adapter) {
        expandableAdapter = adapter;
    }

    public void setOnGroupClickListener(OnGroupClickListener onGroupClickListener) {
        this.onGroupClickListener = onGroupClickListener;
    }

    public void setOnGroupExpandListener(
            OnGroupExpandListener onGroupExpandListener) {
        this.onGroupExpandListener = onGroupExpandListener;
    }

    public void setOnGroupCollapseListener(OnGroupCollapseListener onGroupCollapseListener) {
        this.onGroupCollapseListener = onGroupCollapseListener;
    }

    /**
     * Sets the indicator to be drawn next to a group.
     *
     * @param groupIndicator The drawable to be used as an indicator. If the
     *                       group is empty, the state {@link android.R.attr#state_empty} will be
     *                       set. If the group is expanded, the state
     *                       {@link android.R.attr#state_expanded} will be set.
     */
    public void setGroupIndicator(Drawable groupIndicator) {
        this.groupIndicator = groupIndicator;
    }

    /**
     * Sets the indicator to be drawn next to a child.
     *
     * @param childIndicator The drawable to be used as an indicator. If the
     *                       child is the last child for a group, the state
     *                       {@link android.R.attr#state_last} will be set.
     */
    public void setChildIndicator(Drawable childIndicator) {
        this.childIndicator = childIndicator;
    }

    /**
     * Expand a group in the grouped list view
     *
     * @param groupPosition the group to be expanded
     * @return True if the group was expanded, false otherwise (if the group
     * was already expanded, this will return false)
     */
    public boolean expandGroup(int groupPosition) {
        return expandGroup(groupPosition, false);
    }

    /**
     * Expand a group in the grouped list view
     *
     * @param groupPosition the group to be expanded
     * @param animate       true if the expanding group should be animated in
     * @return True if the group was expanded, false otherwise (if the group
     * was already expanded, this will return false)
     */
    public boolean expandGroup(int groupPosition, boolean animate) {
        return false;
    }

    /**
     * Collapse a group in the grouped list view
     *
     * @param groupPosition position of the group to collapse
     * @return True if the group was collapsed, false otherwise (if the group
     * was already collapsed, this will return false)
     */
    public boolean collapseGroup(int groupPosition) {
        return false;
    }

    /////////////////////////////////
    //GridView

    /**
     * Set the width of columns in the grid.
     *
     * @param columnWidth The column width, in pixels.
     * @attr ref android.R.styleable#GridView_columnWidth
     */
    public void setColumnWidth(int columnWidth) {
        if (columnWidth != requestedColumnWidth) {
            requestedColumnWidth = columnWidth;
        }
    }

    /**
     * Set the number of columns in the grid
     *
     * @param numColumns The desired number of columns.
     * @attr ref android.R.styleable#GridView_numColumns
     */
    public void setNumColumns(int numColumns) {
        if (numColumns != requestedNumColumns) {
            requestedNumColumns = numColumns;
        }
    }

    /**
     * Set the amount of horizontal (x) spacing to place between each item
     * in the grid.
     *
     * @param horizontalSpacing The amount of horizontal space between items,
     *                          in pixels.
     * @attr ref android.R.styleable#GridView_horizontalSpacing
     */
    public void setHorizontalSpacing(int horizontalSpacing) {
        if (horizontalSpacing != requestedHorizontalSpacing) {
            requestedHorizontalSpacing = horizontalSpacing;
        }
    }

    /**
     * Set the amount of vertical (y) spacing to place between each item
     * in the grid.
     *
     * @param verticalSpacing The amount of vertical space between items,
     *                        in pixels.
     * @attr ref android.R.styleable#GridView_verticalSpacing
     * @see #getVerticalSpacing()
     */
    public void setVerticalSpacing(int verticalSpacing) {
        if (verticalSpacing != verticalSpacing) {
            this.verticalSpacing = verticalSpacing;
        }
    }

    /**
     * Returns the amount of vertical spacing between each item in the grid.
     *
     * @return The vertical spacing between items in pixels
     * @attr ref android.R.styleable#GridView_verticalSpacing
     * @see #setVerticalSpacing(int)
     */
    public int getVerticalSpacing() {
        return verticalSpacing;
    }

    /////////////
    //ListView

    /**
     * Returns the number of header views in the list. Header views are special views
     * at the top of the list that should not be recycled during a layout.
     *
     * @return The number of header views, 0 in the default implementation.
     */
    public int getHeaderViewsCount() {
        return headerViews == null ? 0 : headerViews.size();
    }

    /**
     * Add a fixed view to appear at the top of the list. If addHeaderView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * Note: When first introduced, this method could only be called before
     * setting the adapter with {@link #setAdapter(ListAdapter)}. Starting with
     * {@link android.os.Build.VERSION_CODES#KITKAT}, this method may be
     * called at any time. If the ListView's adapter does not extend
     * {@link HeaderViewListAdapter}, it will be wrapped with a supporting
     * instance of {@link WrapperListAdapter}.
     *
     * @param v The view to add.
     */
    public void addHeaderView(View v) {
        addHeaderView(v, null, true);
    }

    /**
     * Add a fixed view to appear at the top of the list. If this method is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * Note: When first introduced, this method could only be called before
     * setting the adapter with {@link #setAdapter(ListAdapter)}. Starting with
     * {@link android.os.Build.VERSION_CODES#KITKAT}, this method may be
     * called at any time. If the ListView's adapter does not extend
     * {@link HeaderViewListAdapter}, it will be wrapped with a supporting
     * instance of {@link WrapperListAdapter}.
     *
     * @param v            The view to add.
     * @param data         Data to associate with this view
     * @param isSelectable whether the item is selectable
     */
    public void addHeaderView(View v, Object data, boolean isSelectable) {
        final FixedView info = new FixedView();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        headerViews.add(info);
        areAllItemsSelectable &= isSelectable;

        // Wrap the adapter if it wasn't already wrapped.
        if (listAdapter != null) {
            if (!(listAdapter instanceof HeaderViewListAdapter)) {
                wrapHeaderListAdapterInternal();
            }

            // In the case of re-adding a header view, or adding one later on,
            // we need to notify the observer.
            if (dataSetObserver != null) {
                dataSetObserver.onChanged();
            }
        }
    }


    /**
     * Add a fixed view to appear at the bottom of the list. If addFooterView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * Note: When first introduced, this method could only be called before
     * setting the adapter with {@link #setAdapter(Adapter)}. Starting with
     * {@link android.os.Build.VERSION_CODES#KITKAT}, this method may be
     * called at any time. If the ListView's adapter does not extend
     * {@link android.widget.HeaderViewListAdapter}, it will be wrapped with a supporting
     * instance of {@link WrapperListAdapter}.
     *
     * @param v The view to add.
     */
    public void addFooterView(View v) {
        addFooterView(v, null, true);
    }

    /**
     * Add a fixed view to appear at the bottom of the list. If addFooterView is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     * <p>
     * Note: When first introduced, this method could only be called before
     * setting the adapter with {@link #setAdapter(Adapter)}. Starting with
     * {@link android.os.Build.VERSION_CODES#KITKAT}, this method may be
     * called at any time. If the ListView's adapter does not extend
     * {@link android.widget.HeaderViewListAdapter}, it will be wrapped with a supporting
     * instance of {@link WrapperListAdapter}.
     *
     * @param v            The view to add.
     * @param data         Data to associate with this view
     * @param isSelectable true if the footer view can be selected
     */
    public void addFooterView(View v, Object data, boolean isSelectable) {
        final FixedView info = new FixedView();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        footerViews.add(info);
        areAllItemsSelectable &= isSelectable;

        // Wrap the adapter if it wasn't already wrapped.
        if (listAdapter != null) {
            if (!(listAdapter instanceof HeaderViewListAdapter)) {
                wrapHeaderListAdapterInternal();
            }

            // In the case of re-adding a footer view, or adding one later on,
            // we need to notify the observer.
            if (dataSetObserver != null) {
                dataSetObserver.onChanged();
            }
        }
    }

    /**
     * Returns the number of footer views in the list. Footer views are special views
     * at the bottom of the list that should not be recycled during a layout.
     *
     * @return The number of footer views, 0 in the default implementation.
     */
    public int getFooterViewsCount() {
        return footerViews == null ? 0 : footerViews.size();
    }

    /**
     * @hide
     */
    protected void wrapHeaderListAdapterInternal() {
        listAdapter = wrapHeaderListAdapterInternal(headerViews, footerViews, listAdapter);
    }

    /**
     * @hide
     */
    protected HeaderViewListAdapter wrapHeaderListAdapterInternal(
            ArrayList<FixedView> headerViews,
            ArrayList<FixedView> footerViews,
            Adapter adapter) {
        return new HeaderViewListAdapter(getContext(), headerViews, footerViews, adapter);
    }

    /**
     * Sets the drawable that will be drawn between each item in the list.
     * <p>
     * <strong>Note:</strong> If the drawable does not have an intrinsic
     * height, you should also call {@link #setDividerHeight(int)}.
     *
     * @param divider the drawable to use
     * @attr ref R.styleable#ListView_divider
     */
    public void setDivider(@Nullable Drawable divider) {
        if (divider != null) {
            dividerHeight = divider.getIntrinsicHeight();
        } else {
            dividerHeight = 0;
        }
        this.divider = divider;
        dividerIsOpaque = divider == null || divider.getOpacity() == PixelFormat.OPAQUE;
    }

    /**
     * Sets the height of the divider that will be drawn between each item in the list. Calling
     * this will override the intrinsic height as set by {@link #setDivider(Drawable)}
     *
     * @param height The new height of the divider in pixels.
     */
    public void setDividerHeight(int height) {
        dividerHeight = height;
    }
    ////////////////
    //AdapterView -- Common

    /**
     * Sets the view to show if the adapter is empty
     */
//    @android.view.RemotableViewMethod
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    ////////////////////////
    ///RecyclerView
    @Override
    public void setAdapter(Adapter adapter) {
        if (internalAdapter != null && dataSetObserver != null) {
            internalAdapter.unregisterAdapterDataObserver(dataSetObserver);
        }
        if (headerViews.size() > 0 || footerViews.size() > 0) {
            internalAdapter = wrapHeaderListAdapterInternal(headerViews, footerViews, adapter);
        } else {
            internalAdapter = adapter;
        }
        if (internalAdapter != null) {
            dataSetObserver = new InternalAdapterDataObserver();
            internalAdapter.registerAdapterDataObserver(dataSetObserver);
        }
        super.swapAdapter(internalAdapter, true);
    }

    /**
     * Sets the data behind this ListView.
     * <p>
     * The adapter passed to this method may be wrapped by a {@link WrapperListAdapter},
     * depending on the ListView features currently in use. For instance, adding
     * headers and/or footers will cause the adapter to be wrapped.
     *
     * @param adapter The ListAdapter which is responsible for maintaining the
     *                data backing this list and for producing a view to represent an
     *                item in that data set.
     * @see #getAdapter()
     */
    public void setAdapter(ListAdapter adapter) {

    }
}