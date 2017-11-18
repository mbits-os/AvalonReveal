package com.midnightbits.avalonreveal.avalon;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public final class Option {
    public static final String PREFERENCE_FILE = "AvalonReveal";
    public static final String PREFERENCE_NAME = "options";

    public final G.dialog dialog;
    public final int title;
    public final int description;
    public final G.dialog[] deps;

    Option(G.dialog dialog, int title, int descr, G.dialog... deps) {
        this.dialog = dialog;
        this.title = title;
        this.description = descr;
        this.deps = deps;
    }

    Option(G.dialog dialog, int title, G.dialog... deps) {
        this(dialog, title, 0, deps);
    }

    @Nullable
    public static Option get(G.dialog id) {
        for (Option opt : G.options) {
            if (opt.dialog == id) {
                return opt;
            }
        }
        return null;
    }

    public static Set<String> forPreferences(G.dialog[] selection) {
        Set<String> result = new HashSet<>(selection.length);
        for (G.dialog dialog : selection)
            result.add(dialog.name());
        return result;
    }

    public static G.dialog[] fromPreferences(Set<String> pref) {
        G.dialog[] result = new G.dialog[pref.size()];
        int id = 0;
        for (String name : pref) {
            try {
                G.dialog val = Enum.valueOf(G.dialog.class, name);
                result[id++] = val;
            } catch(RuntimeException ex) {
                Log.w("AvalonReveal", ex.getClass().getSimpleName() + ": " + ex.getMessage());
            }
        }
        return result;
    }
}
