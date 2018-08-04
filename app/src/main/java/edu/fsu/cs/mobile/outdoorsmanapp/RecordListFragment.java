package edu.fsu.cs.mobile.outdoorsmanapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordListFragment extends Fragment {


    private static final String TAG = RecordListFragment.class.getCanonicalName();

    private static final String mHarvestID = "harvest_id";


    public RecordListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_record_list, container, false);

        Log.i(TAG, "RecordListFragment.onCreateView()");


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerView);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        Log.i(TAG, "RecordListFragment.onViewCreated()");

        ArrayList<HarvestRecord> arrayList = ((MainActivity)getActivity()).getHarvestRecordArrayList();

        if(!arrayList.isEmpty()){

            RecordListAdapter mAdapter = new RecordListAdapter(getActivity(), arrayList);

            mRecyclerView.setAdapter(mAdapter);
            Log.i(TAG, "RecordListFragment.onViewCreated():setAdapter()");
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            mAdapter.setOnItemClickListener(new OnItemClick() {
                @Override public void onItemClicked(View view, int position, Object data) {

                    HarvestRecord harvestRecord = (HarvestRecord)data;
                    Bundle bundle = new Bundle();
                    bundle.putInt(mHarvestID, harvestRecord.getId());
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
        void onItemClicked(View view, int position, Object data);
    }
}
