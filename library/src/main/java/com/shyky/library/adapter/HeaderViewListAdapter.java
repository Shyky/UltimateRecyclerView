package com.shyky.library.adapter;
/**
 * Created by Shyky on 2017/6/21.
 */

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;

import com.shyky.library.widget.UltimateRecyclerView;

import java.util.ArrayList;

/**
 * ListAdapter used when a ListView has header views. This ListAdapter
 * wraps another one and also keeps track of the header views and their
 * associated data objects.
 * <p>This is intended as a base class; you will probably not need to
 * use this class directly in your own code.
 */
public class HeaderViewListAdapter extends ListAdapter implements Filterable {
    /**
     * 取值为Integer.MAX_VALUE + 10是为了防止外部的Adapter，即被包装的Adapter中的view type与其冲突
     */
    private static final int TYPE_HEADER = Integer.MAX_VALUE + 10;
    private static final int TYPE_FOOTER = Integer.MIN_VALUE - 10;
    private Context context;
    private RecyclerView.Adapter adapter;
    // These two ArrayList are assumed to NOT be null.
    // They are indeed created when declared in ListView and then shared.
    private ArrayList<UltimateRecyclerView.FixedView> headerViews;
    private ArrayList<UltimateRecyclerView.FixedView> footerViews;

    // Used as a placeholder in case the provided info views are indeed null.
    // Currently only used by some CTS tests, which may be removed.
    static final ArrayList<UltimateRecyclerView.FixedView> EMPTY_INFO_LIST =
            new ArrayList<UltimateRecyclerView.FixedView>();
    boolean mAreAllFixedViewsSelectable;
    private final boolean mIsFilterable;

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        public final FrameLayout container;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            container = (FrameLayout) itemView;
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        public final FrameLayout container;

        public FooterViewHolder(View itemView) {
            super(itemView);
            container = (FrameLayout) itemView;
        }
    }

    public HeaderViewListAdapter(Context context, ArrayList<UltimateRecyclerView.FixedView> headerViews,
                                 ArrayList<UltimateRecyclerView.FixedView> footerViews,
                                 RecyclerView.Adapter adapter) {
        this.context = context;
        this.adapter = adapter;
        mIsFilterable = adapter instanceof Filterable;

        if (headerViews == null) {
            this.headerViews = EMPTY_INFO_LIST;
        } else {
            this.headerViews = headerViews;
        }

        if (footerViews == null) {
            this.footerViews = EMPTY_INFO_LIST;
        } else {
            this.footerViews = footerViews;
        }
        mAreAllFixedViewsSelectable =
                areAllListInfosSelectable(headerViews)
                        && areAllListInfosSelectable(footerViews);
    }

    public int getHeadersCount() {
        return headerViews.size();
    }

    public int getFootersCount() {
        return footerViews.size();
    }

    @Override
    public boolean isEmpty() {
        if (adapter == null) {
            return true;
        } else {
            if (adapter instanceof ListAdapter) {
                return ((ListAdapter) adapter).isEmpty();
            }
        }
        return false;
    }

    private boolean areAllListInfosSelectable(ArrayList<UltimateRecyclerView.FixedView> infos) {
        if (infos != null) {
            for (UltimateRecyclerView.FixedView info : infos) {
                if (!info.isSelectable) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean removeHeader(View v) {
        for (int i = 0; i < headerViews.size(); i++) {
            UltimateRecyclerView.FixedView info = headerViews.get(i);
            if (info.view == v) {
                headerViews.remove(i);

                mAreAllFixedViewsSelectable =
                        areAllListInfosSelectable(headerViews)
                                && areAllListInfosSelectable(footerViews);

                return true;
            }
        }

        return false;
    }

    public boolean removeFooter(View v) {
        for (int i = 0; i < footerViews.size(); i++) {
            UltimateRecyclerView.FixedView info = footerViews.get(i);
            if (info.view == v) {
                footerViews.remove(i);

                mAreAllFixedViewsSelectable =
                        areAllListInfosSelectable(headerViews)
                                && areAllListInfosSelectable(footerViews);

                return true;
            }
        }

        return false;
    }

    public boolean areAllItemsEnabled() {
        if (adapter != null && adapter instanceof ListAdapter) {
            return mAreAllFixedViewsSelectable && ((ListAdapter) adapter).areAllItemsEnabled();
        } else {
            return true;
        }
    }

    public boolean isEnabled(int position) {
        // Header (negative positions will throw an IndexOutOfBoundsException)
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {
            return headerViews.get(position).isSelectable;
        }

        // Adapter
        final int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if (adapter != null && adapter instanceof ListAdapter) {
            adapterCount = adapter.getItemCount();
            if (adjPosition < adapterCount) {
                return ((ListAdapter) adapter).isEnabled(adjPosition);
            }
        }

        // Footer (off-limits positions will throw an IndexOutOfBoundsException)
        return footerViews.get(adjPosition - adapterCount).isSelectable;
    }

    public long getItemId(int position) {
        int numHeaders = getHeadersCount();
        if (adapter != null && position >= numHeaders) {
            int adjPosition = position - numHeaders;
            int adapterCount = adapter.getItemCount(); //.getCount();
            if (adjPosition < adapterCount) {
                return adapter.getItemId(adjPosition);
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        if (adapter != null) {
            return getFootersCount() + getHeadersCount() + adapter.getItemCount();
        } else {
            return getFootersCount() + getHeadersCount();
        }
    }
//    public boolean hasStableIds() {
//        if (adapter != null) {
//            return adapter.hasStableIds();
//        }
//        return false;
//    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                final FrameLayout headerContainer = new FrameLayout(context);
                return new HeaderViewHolder(headerContainer);
            case TYPE_FOOTER:
                final FrameLayout footerContainer = new FrameLayout(context);
                return new FooterViewHolder(footerContainer);
            default:
                return adapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        {
//            // Header (negative positions will throw an IndexOutOfBoundsException)
//            int numHeaders = getHeadersCount();
//            if (position < numHeaders) {
////                return headerViews.get(position).view;
//
//                final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
//                // 解决java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
//                headerViewHolder.container.removeAllViews();
//                headerViewHolder.container.addView(headerViews.get(position).view);
////                break;
//            }
//
//            // Adapter
//            final int adjPosition = position - numHeaders;
//            int adapterCount = 0;
//            if (adapter != null) {
//                adapterCount = adapter.getItemCount();
//                if (adjPosition < adapterCount) {
////                    return adapter.getView(adjPosition, convertView, parent);
//
//                    adapter.onBindViewHolder(holder, adjPosition);
//                }
//            }
//
//            // Footer (off-limits positions will throw an IndexOutOfBoundsException)
////            return footerViews.get(adjPosition - adapterCount).view;
//            ///
//            final FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
//            // 解决java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
//            footerViewHolder.container.removeAllViews();
//            footerViewHolder.container.addView(footerViews.get(adjPosition - adapterCount).view);
//        }

        final int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_HEADER:
                final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                // 解决java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
                headerViewHolder.container.removeAllViews();
                headerViewHolder.container.addView(getHeader(position));
                break;
            case TYPE_FOOTER:
                final FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                // 解决java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
                footerViewHolder.container.removeAllViews();
                footerViewHolder.container.addView(getFooter(position));
                break;
            default:
                final int itemPosition = position - getHeaderCount();

                adapter.onBindViewHolder(holder, itemPosition);
        }
    }

//    @Override
//    public int getItemViewType(int position) {
////        return super.getItemViewType(position);
////    }
////
////    public int getItemViewTyspe(int position) {
//        int numHeaders = getHeadersCount();
//        if (adapter != null && position >= numHeaders) {
//            int adjPosition = position - numHeaders;
//            int adapterCount = adapter.getItemCount();
//            if (adjPosition < adapterCount) {
//                return adapter.getItemViewType(adjPosition);
//            }
//        }
//
//        return AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
//    }

    @Override
    public int getItemViewType(int position) {
        if (position < getHeaderCount()) {
            return TYPE_HEADER;
        }
        if (hasFooter() && position >= getHeaderCount() + adapter.getItemCount()) {
            return TYPE_FOOTER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        // 解决当RecyclerView的LayoutManager为GridLayoutManager时，header view和footer view显示不正常
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) layoutManager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    // 当item view type为header或footer时返回单位格为1
                    final int itemViewType = getItemViewType(position);
                    return itemViewType == TYPE_HEADER || itemViewType == TYPE_FOOTER ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        if (params != null && params instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) params;
            // 当item view type为header或footer时设置为全屏显示
            final int itemViewType = holder.getItemViewType();
            p.setFullSpan(itemViewType == TYPE_HEADER || itemViewType == TYPE_FOOTER);
        }
    }

    public int getHeaderCount() {
        return headerViews.size();
    }

    public View getHeader(int index) {
        return headerViews.get(index).view;
    }

    public int getFooterCount() {
        return headerViews.size();
    }

    public View getFooter(int index) {
        final int footerIndex = index - (getHeaderCount() + adapter.getItemCount());
        return footerViews.get(footerIndex).view;
    }

    public boolean hasFooter() {
        return getFooterCount() != 0;
    }

    @Override
    public Object getItem(int position) {
        // Header (negative positions will throw an IndexOutOfBoundsException)
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {
            return headerViews.get(position).data;
        }

        // Adapter
        final int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if (adapter != null && adapter instanceof ListAdapter) {
            adapterCount = adapter.getItemCount();
            if (adjPosition < adapterCount) {
                return ((ListAdapter) adapter).getItem(adjPosition);
            }
        }

        // Footer (off-limits positions will throw an IndexOutOfBoundsException)
        return footerViews.get(adjPosition - adapterCount).data;
    }

    public int getViewTypeCount() {
        if (adapter != null) {
            // TODO: 2017/6/21
            return 0;//adapter.getViewTypeCount();
        }
        return 1;
    }

    public void registerDataSetObserver(RecyclerView.AdapterDataObserver observer/*DataSetObserver observer*/) {
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);// .registerDataSetObserver(observer);
        }
    }

    public void unregisterDataSetObserver(RecyclerView.AdapterDataObserver observer) {
        if (adapter != null) {
            adapter.unregisterAdapterDataObserver(observer);//.unregisterDataSetObserver(observer);
        }
    }

    public Filter getFilter() {
        if (mIsFilterable) {
            return ((Filterable) adapter).getFilter();
        }
        return null;
    }

//    @Override
//    public ListAdapter getWrappedAdapter() {
//        return adapter;
//    }
}