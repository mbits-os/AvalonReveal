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
import com.midnightbits.avalonreveal.avalon.G;
import com.midnightbits.avalonreveal.avalon.Option;
import com.midnightbits.avalonreveal.ui.adapters.OptionsRecyclerViewAdapter;

import java.util.Set;

public class OptionsFragment extends Fragment {

    private OptionsRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OptionsFragment() {
        mAdapter = new OptionsRecyclerViewAdapter(new OptionsRecyclerViewAdapter.Listener() {
            @Override
            public void onSelectionChanged(G.dialog[] selected) {
                SharedPreferences.Editor editor =
                        getContext()
                                .getSharedPreferences(Option.PREFERENCE_FILE, Context.MODE_PRIVATE)
                                .edit();
                editor.putStringSet(Option.PREFERENCE_NAME, Option.forPreferences(selected));
                editor.apply();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_options, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
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

        mAdapter.updateDataSet(options);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
