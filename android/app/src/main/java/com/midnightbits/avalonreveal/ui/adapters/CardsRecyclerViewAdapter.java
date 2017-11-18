package com.midnightbits.avalonreveal.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.midnightbits.avalonreveal.R;
import com.midnightbits.avalonreveal.avalon.*;

import java.util.ArrayList;
import java.util.List;

public class CardsRecyclerViewAdapter extends RecyclerView.Adapter<CardsRecyclerViewAdapter.ViewHolder> {
    static class AdapterMap {
        static class Item {
            final int cardId;
            final LinesRecyclerViewAdapter adapter;
            Item(int cardId, LinesRecyclerViewAdapter adapter) {
                this.cardId = cardId;
                this.adapter = adapter;
            }
        }

        List<Item> map = new ArrayList<>();
        Fragment fragment;
        AdapterMap(Fragment f) { fragment = f; }

        LinesRecyclerViewAdapter get(int id) {
            for (Item item : map) {
                if (item.cardId == id)
                    return item.adapter;
            }

            LinesRecyclerViewAdapter ret = new LinesRecyclerViewAdapter(fragment);
            map.add(new Item(id, ret));
            return ret;
        }

        @SuppressWarnings("unused")
        void updateAdapters(Cards cards) {
            // remove adapters for removed items
            int i = 0;
            int len = map.size();
            for (; i < len; ++i) {
                Item item = map.get(i);

                boolean remove = true;
                for (Card card : cards.cards()) {
                    if (card.id() == item.cardId) {
                        remove = false;
                        break;
                    }
                }
                if (remove) {
                    map.remove(i);
                    --i;
                    --len;
                    break;
                }
            }

            for (Card lines : cards.cards()) {
                LinesRecyclerViewAdapter item = get(lines.id());
                item.updateDataSet(lines);
            }
        }
    }

    private Cards cards = Cards.make(new G.dialog[]{});
    private AdapterMap sub;
    public CardsRecyclerViewAdapter(Fragment fragment) {
        sub = new AdapterMap(fragment);
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_cards_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mItem = cards.get(position);
        LinesRecyclerViewAdapter adapter = sub.get(holder.mItem.id());
        adapter.updateDataSet(holder.mItem);
        holder.mLinesView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public long getItemId(int position) {
        return cards.get(position).id();
    }

    public void updateDataSet(Cards cards) {
        this.cards = cards;
        Log.i("AvalonReveal", "new data set:\n" + cards.expandString(sub.fragment.getResources()));
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final RecyclerView mLinesView;
        Card mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mLinesView = view.findViewById(R.id.list_actions);
        }
    }
}
