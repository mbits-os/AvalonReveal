package com.midnightbits.avalonreveal.avalon;

import android.support.annotation.Nullable;

interface Line {
    @Nullable
    DialogItem get(G.dialog[] options);
}
