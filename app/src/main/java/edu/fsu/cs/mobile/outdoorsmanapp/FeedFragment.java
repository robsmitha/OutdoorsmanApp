package edu.fsu.cs.mobile.outdoorsmanapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    private static final String TAG = FeedFragment.class.getCanonicalName()+"ErrorChecking";

    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onResume(){

        super.onResume();
        //test to check feed data updates
        Log.i(TAG, ((MainActivity)getActivity()).getFeedHRArrayList().toString());

    }

}
