package com.midnightbits.avalonreveal.avalon;

public final class DialogItem {
    public final G.dialog origin;
    public final int id;
    public final Text text;

    DialogItem(int id, Text text) {
        this(id, G.dialog.base, text);
    }

    DialogItem(int id, G.dialog origin, Text text) {
        this.origin = origin;
        this.id = id;
        this.text = text;
    }
}
