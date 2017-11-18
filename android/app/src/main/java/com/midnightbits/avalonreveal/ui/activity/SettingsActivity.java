package com.midnightbits.avalonreveal.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.midnightbits.avalonreveal.R;
import com.midnightbits.avalonreveal.avalon.G;
import com.midnightbits.avalonreveal.avalon.Option;
import com.midnightbits.avalonreveal.ui.frag.OptionsFragment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SettingsActivity extends BackNavActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences preferences = getSharedPreferences(Option.PREFERENCE_FILE, Context.MODE_PRIVATE);
        Set<String> string_options = preferences.getStringSet(Option.PREFERENCE_NAME, null);
        if (string_options == null) {
            Log.i("AvalonReveal", "Creating empty options set to stop settings from appearing on next start.");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putStringSet(Option.PREFERENCE_NAME, new HashSet<String>());
            editor.apply();
        }
    }
}
