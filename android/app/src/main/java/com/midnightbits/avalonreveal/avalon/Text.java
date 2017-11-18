package com.midnightbits.avalonreveal.avalon;

import com.midnightbits.avalonreveal.R;

import static com.midnightbits.avalonreveal.avalon.G.actor.*;

public final class Text {
    public final G.actor actor;
    public final G.action action;
    public final int text;

    Text(G.actor actor, G.action action, int text) {
        this.actor = actor;
        this.action = action;
        this.text = text;
    }

    public final int avatar() {
        return ActorUtils.avatar(actor);
    }
}
