package com.midnightbits.avalonreveal.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.midnightbits.avalonreveal.R;
import com.midnightbits.avalonreveal.avalon.*;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private boolean mPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences(Option.PREFERENCE_FILE, MODE_PRIVATE);
        Set<String> options = preferences.getStringSet(Option.PREFERENCE_NAME, null);
        if (options == null) {
            Log.e("AvalonReveal", "No options in the preferences. NOW SHOW SETTINGS.");
            startActivity(new Intent(this, SettingsActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_play).setVisible(!mPlaying);
        menu.findItem(R.id.action_stop).setVisible(mPlaying);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_play) {
            playScript();
            return true;
        }

        if (id == R.id.action_stop) {
            stopScript();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void playScript() {
        mPlaying = true;
        invalidateOptionsMenu();
    }

    private void stopScript() {
        mPlaying = false;
        invalidateOptionsMenu();
    }
}
