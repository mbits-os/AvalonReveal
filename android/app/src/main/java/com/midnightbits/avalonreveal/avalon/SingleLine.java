package com.midnightbits.avalonreveal.avalon;

import android.support.annotation.Nullable;

final class SingleLine implements Line {
    private final DialogItem item_;

    SingleLine(int id, Text text) {
        item_ = new DialogItem(id, text);
    }

    SingleLine(int id, G.dialog origin, Text text) {
        item_ = new DialogItem(id, origin, text);
    }

    @Override @Nullable
    public DialogItem get(G.dialog[] options) {
        if (item_.origin == G.dialog.base)
            return item_;

        for (G.dialog d : options) {
            if (d == item_.origin)
                return item_;
        }

        return null;
    }
}
