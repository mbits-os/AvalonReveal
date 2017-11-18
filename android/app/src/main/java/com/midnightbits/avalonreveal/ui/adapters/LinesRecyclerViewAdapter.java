package com.midnightbits.avalonreveal.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.midnightbits.avalonreveal.R;
import com.midnightbits.avalonreveal.avalon.Card;
import com.midnightbits.avalonreveal.avalon.DialogItem;
import com.midnightbits.avalonreveal.avalon.G;

public class LinesRecyclerViewAdapter extends RecyclerView.Adapter<LinesRecyclerViewAdapter.ViewHolder> {
    private Card lines = Card.empty();
    private Fragment fragment;
    public LinesRecyclerViewAdapter(Fragment fragment) {
        this.fragment = fragment;
        setHasStableIds(true);
    }

    @Override
    public int getItemViewType(int position) {
        DialogItem item = lines.get(position);
        if (item.text.action == G.action.warning) {
            if (item.text.actor == G.actor.all)
                return R.layout.action_reminder_all;
            return R.layout.action_simple;
        }
        return R.layout.action_avatar;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewLayout) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewLayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mItem = lines.get(position);
        holder.mContentView.setText(holder.mItem.text.text);
        if (holder.mAvatarView != null)
            holder.mAvatarView.setImageResource(holder.mItem.text.avatar());
    }

    @Override
    public int getItemCount() {
        return lines.size();
    }

    @Override
    public long getItemId(int position) {
        return lines.get(position).id;
    }

    public void updateDataSet(Card lines) {
        this.lines = lines;
        Log.i("AvalonReveal", "new data set:\n" + lines.expandString(fragment.getResources()));
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mAvatarView;
        final TextView mContentView;
        DialogItem mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mAvatarView = (ImageView) view.findViewById(R.id.avatar);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
