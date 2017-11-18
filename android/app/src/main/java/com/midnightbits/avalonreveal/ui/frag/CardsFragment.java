package com.midnightbits.avalonreveal.ui.frag;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.midnightbits.avalonreveal.R;
import com.midnightbits.avalonreveal.avalon.*;
import com.midnightbits.avalonreveal.ui.adapters.CardsRecyclerViewAdapter;

import java.util.Set;

public class CardsFragment extends Fragment {
    private CardsRecyclerViewAdapter mAdapter = new CardsRecyclerViewAdapter(this);
    private SharedPreferences.OnSharedPreferenceChangeListener mListener =
        new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(Option.PREFERENCE_NAME)) {
                    Set<String> string_options = sharedPreferences.getStringSet(key, null);
                    G.dialog[] options = null;
                    if (string_options != null)
                        options = Option.fromPreferences(string_options);

                    if (options == null)
                        options = new G.dialog[]{};

                    mAdapter.updateDataSet(Cards.make(options));
                }
            }
        };

    public CardsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void updateDataSet(G.dialog[] options) {
        mAdapter.updateDataSet(Cards.make(options));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cards, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        G.dialog[] options;
        SharedPreferences preferences = context.getSharedPreferences(Option.PREFERENCE_FILE, Context.MODE_PRIVATE);
        Set<String> string_options = preferences.getStringSet(Option.PREFERENCE_NAME, null);
        if (string_options == null) {
            Log.e("AvalonReveal", "No options in the preferences yet.");
            options = new G.dialog[]{};
        } else {
            options = Option.fromPreferences(string_options);
        }
        preferences.registerOnSharedPreferenceChangeListener(mListener);

        mAdapter.updateDataSet(Cards.make(options));
    }

    @Override
    public void onDetach() {
        SharedPreferences preferences = getContext().getSharedPreferences(Option.PREFERENCE_FILE, Context.MODE_PRIVATE);
        preferences.unregisterOnSharedPreferenceChangeListener(mListener);
        super.onDetach();
    }
}
