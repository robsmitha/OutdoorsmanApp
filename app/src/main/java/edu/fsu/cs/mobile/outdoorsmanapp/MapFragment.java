package edu.fsu.cs.mobile.outdoorsmanapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecordListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private final static String ARG_PARAM_HARVEST_ID = "harvest_id";
    private int mParamHarvestId;

    MapView mMapView;
    private GoogleMap googleMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParamHarvestId = getArguments().getInt(ARG_PARAM_HARVEST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);


        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                ArrayList<HarvestRecord> harvestRecordEntries = ((MainActivity)getActivity()).getHarvestRecordArrayList();

                //this gets one unique HarvestRecord
                HarvestRecord harvestRecord = harvestRecordEntries.get(mParamHarvestId);    //NOTE: HarvestId matches position in ArrayList for debugging purposes

                LatLng location = harvestRecord.getLatLng();

                googleMap.addMarker(new MarkerOptions().position(location).title("Latitude = " + location.latitude).snippet("Longitude = " + location.longitude));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(8).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.getUiSettings().setZoomControlsEnabled(true);

            }
        });


        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<HarvestRecord> arrayList = new ArrayList<>();
        arrayList.add(((MainActivity)getActivity()).getHarvestRecordArrayList().get(mParamHarvestId));  //add single HarvestRecord

        if(!arrayList.isEmpty()){
            mAdapter = new RecordListAdapter(getActivity(), arrayList);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            mAdapter.setOnItemClickListener(new RecordListFragment.OnItemClick() {
                @Override public void onItemClicked(View view, int position, Object data) {
                    HarvestRecord harvestRecord = (HarvestRecord)data;


                }
            });
        }


        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}
