package com.midnightbits.avalonreveal.avalon;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

public final class Card {
    private final List<DialogItem> items_;
    private final int id_;

    Card(int id, List<DialogItem> items) {
        items_ = items;
        id_ = id;
    }

    public static Card empty() {
        return new Card(-1, new ArrayList<DialogItem>());
    }

    public final List<DialogItem> items() {
        return items_;
    }
    public final int id() { return id_; }
    public final DialogItem get(int position) { return items_.get(position); }
    public final int size() { return items_.size(); }

    public String expandString(Resources res) {
        StringBuilder sb = new StringBuilder();

        boolean first = true;
        for (DialogItem item : items_) {
            if (first) first = false;
            else sb.append("\n");
            sb.append(res.getString(item.text.text));
        }

        return sb.toString();
    }
}
