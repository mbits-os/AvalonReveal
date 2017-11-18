package com.midnightbits.avalonreveal.avalon;

import android.support.annotation.Nullable;

final class MultiLine implements Line {
    private final DialogItem[] items_;

    MultiLine(DialogItem... items) {
        items_ = items;
    }

    @Override @Nullable
    public DialogItem get(G.dialog[] options) {
        int latest = pos_of(G.dialog.base);
        for (G.dialog option : options) {
            int pos = pos_of(option);
            if (pos > latest)
                latest = pos;
        }
        if (latest < 0 || latest >= items_.length)
            return null;
        return items_[latest];
    }

    private int pos_of(G.dialog option) {
        for (int pos = 0; pos < items_.length; ++pos) {
            if (option == items_[pos].origin)
                return pos;
        }
        return -1;
    }
}
