package com.midnightbits.avalonreveal.ui.adapters;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.midnightbits.avalonreveal.R;
import com.midnightbits.avalonreveal.avalon.G;
import com.midnightbits.avalonreveal.avalon.Option;

import java.util.ArrayList;
import java.util.List;

public class OptionsRecyclerViewAdapter extends RecyclerView.Adapter<OptionsRecyclerViewAdapter.ViewHolder> {
    public interface Listener {
        void onSelectionChanged(G.dialog[] selected);
    }

    static final class Item {
        final G.dialog dialog;
        final G.dialog[] deps;
        final int title;
        final int description;
        boolean selected = false;
        boolean enabled = true;
        // Drawable disabled = null;

        Item(Option opt) {
            dialog = opt.dialog;
            deps = opt.deps;
            title = opt.title;
            description = opt.description;
        }

        Item(boolean sel) {
            dialog = G.dialog.warnings;
            deps = new G.dialog[]{};
            title = R.string.titles__warnings__name;
            description = R.string.titles__warnings__descr;
            selected = sel;
        }

        final int avatar() {
            switch(dialog) {
                case percival: return R.drawable.ic_avatar_percival;
                case morgana: return R.drawable.ic_avatar_morgana;
                case mordred: return R.drawable.ic_avatar_mordred;
                case oberon: return R.drawable.ic_avatar_oberon;
                case lancelot: return R.drawable.ic_avatar_lancelot;
                case lancelot2: return R.drawable.ic_avatar_lancelot2;
                default: break;
            }
            return 0;
        }

        boolean enable() {
            if (enabled)
                return false;
            enabled = true;
            return true;
        }

        boolean disable() {
            if (!enabled)
                return false;
            enabled = false;
            selected = false;
            return true;
        }

        boolean select() {
            if (!enabled || selected)
                return false;
            selected = true;
            return true;
        }

        boolean clear() {
            if (!enabled || !selected)
                return false;
            selected = false;
            return true;
        }

        boolean setEnabled(boolean val) {
            if (val)
                return enable();
            return disable();
        }

        boolean setSelected(boolean val) {
            if (val)
                return select();
            return clear();
        }
    }

    private final List<Item> mItems = new ArrayList<>(G.options.length + 1);
    private final Listener mListener;

    public OptionsRecyclerViewAdapter(Listener listener) {
        mListener = listener;

        for (Option option : G.options)
            mItems.add(new Item(option));
        mItems.add(new Item(false));
        // resolveDependencies();
        setHasStableIds(true);
    }

    public void updateDataSet(G.dialog[] selection) {
        boolean changed = false;
        for (Item item : mItems) {
            boolean has = false;
            for (G.dialog dlg : selection) {
                if (dlg == item.dialog) {
                    has = true;
                    break;
                }
            }
            Log.i("AvatarReveal", "Adapter: selecting " + item.dialog.name());
            changed |= item.setSelected(has);
            changed |= item.enable();
        }
        changed |= resolveDependencies();
        if (changed)
            notifyDataSetChanged();
    }

    @Nullable
    private Item find(G.dialog id) {
        for (Item item : mItems) {
            if (item.dialog == id)
                return item;
        }
        return null;
    }

    private boolean resolveDependencies() {
        boolean changed = false;
        for (Item item : mItems) {
            if (item.deps.length < 1)
                continue;
            boolean enabled = item.selected;
            if (!enabled) {
                Log.i("AvatarReveal", "Adapter: " + item.dialog.name() + " was not selected. Will disable.");
            }
            for (G.dialog dep : item.deps) {
                Item d = find(dep);
                if (d != null) {
                    if (!enabled) {
                        Log.i("AvatarReveal", "Adapter:   disabling " + d.dialog.name());
                    }
                    changed |= d.setEnabled(enabled);
                }
            }
        }
        return changed;
    }

    private G.dialog[] selected() {
        int size = 0;
        for (Item item : mItems) {
            if (item.selected)
                ++size;
        }
        G.dialog[] result = new G.dialog[size];
        int i = 0;
        for (Item item : mItems) {
            if (item.selected)
                result[i++] = item.dialog;
        }
        return result;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_options_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mItems.get(position);
        if (holder.mAvatarView != null) {
            holder.mAvatarView.setImageResource(holder.mItem.avatar());
            holder.mAvatarView.setEnabled(holder.mItem.enabled);
        }

        holder.mTitleView.setText(holder.mItem.title);
        holder.mTitleView.setEnabled(holder.mItem.enabled);

        if (holder.mItem.description == 0)
            holder.mSubtitleView.setVisibility(View.GONE);
        else {
            holder.mSubtitleView.setVisibility(View.VISIBLE);
            holder.mSubtitleView.setText(holder.mItem.description);
            holder.mSwitchView.setEnabled(holder.mItem.enabled);
        }

        holder.mSwitchView.setChecked(holder.mItem.selected);
        holder.mSwitchView.setEnabled(holder.mItem.enabled);
        holder.mSwitchView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) { onSelected(holder); }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mSwitchView.toggle();
                onSelected(holder);
            }
        });
        // Log.i("AvalonReveal", "Binding to " + holder.toString());
    }

    private void onSelected(ViewHolder holder) {
        boolean checked = holder.mSwitchView.isChecked();
        Log.w("AvalonReveal", "onSelected(" + holder.toString() + ", " + (checked ? "" : "not ") + "checked)");
        boolean changed = holder.mItem.setSelected(checked);
        changed |= resolveDependencies();
        if (changed) {
            notifyDataSetChanged();
            if (mListener != null)
                mListener.onSelectionChanged(selected());
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) { return mItems.get(position).dialog.ordinal(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mAvatarView;
        final TextView mTitleView;
        final TextView mSubtitleView;
        final Switch mSwitchView;
        Item mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mAvatarView = view.findViewById(R.id.avatar);
            mTitleView = view.findViewById(R.id.title);
            mSubtitleView = view.findViewById(R.id.subtitle);
            mSwitchView = view.findViewById(R.id.option_switch);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "' (" + (mItem != null && mItem.enabled ? "enabled" : "disabled") + ")";
        }
    }
}
