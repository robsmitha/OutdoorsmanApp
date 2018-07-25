package edu.fsu.cs.mobile.outdoorsmanapp;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordListFragment extends Fragment {


    private static final String TAG = RecordListFragment.class.getCanonicalName();

    private static final String mHarvestID = "harvest_id";

    private RecyclerView mRecyclerView;
    private RecordListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<HarvestRecord> myHarvestRecords;


    public RecordListFragment() {
        // Required empty public constructor
    }

    private void populateHarvestRecords(){



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_record_list, container, false);

        Log.i(TAG, "RecordListFragment.onCreateView()");


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        Log.i(TAG, "RecordListFragment.onViewCreated()");

        ArrayList<HarvestRecord> arrayList = ((MainActivity)getActivity()).getHarvestRecordArrayList();

        if(!arrayList.isEmpty()){

            //TODO: Add sort when harvestIdis
            /*
            Collections.sort(arrayList, new Comparator<HarvestRecord>() {
                @Override
                public int compare(HarvestRecord lhs, HarvestRecord rhs) {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return Long.compare(rhs.getDate(), lhs.getDate());
                }
            });
            */
            mAdapter = new RecordListAdapter(getActivity(), arrayList);

            mRecyclerView.setAdapter(mAdapter);
            Log.i(TAG, "RecordListFragment.onViewCreated():setAdapter()");
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            mAdapter.setOnItemClickListener(new OnItemClick() {
                @Override public void onItemClicked(View view, int position, Object data) {

                    HarvestRecord harvestRecord = (HarvestRecord)data;
                    Bundle bundle = new Bundle();
                    bundle.putInt(mHarvestID, harvestRecord.getId());   //harvest record ID matches the position for debugging purposes
                    //TODO:base harvestId on firebase recordId
                    MapFragment mapFragment = new MapFragment();
                    mapFragment.setArguments(bundle);
                    ((MainActivity)getActivity()).OnFragmentReplaced(mapFragment);
                }
            });
        }else{
            //TODO:Show message when user has no records
        }
    }

    public interface OnItemClick {
        public void onItemClicked(View view, int position, Object data);
    }
}
