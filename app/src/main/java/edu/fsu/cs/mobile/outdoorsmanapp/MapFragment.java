package edu.fsu.cs.mobile.outdoorsmanapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener {

    private RecyclerView mRecyclerView;
    private RecordListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private GestureDetector mDetector;

    public final static String ARG_PARAM_HARVEST_ID = "harvest_id";
    private int mParamHarvestId = 0;

    MapView mMapView;
    private GoogleMap googleMap;
    private final static int ZOOM_LEVEL_5 = 5;
    private final static int ZOOM_LEVEL_11 = 11;
    private HashMap<HarvestRecord,Marker> hashMapMarker = new HashMap<>();
    Marker mMarker;

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

                //Add markers and create hashmap
                for (HarvestRecord item : harvestRecordEntries) {

                    LatLng location = item.getLatLng();
                    String title = "Type: " + item.getType();
                    String snippet = "Date: " + item.getDateString();

                    mMarker = googleMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(title)
                            .snippet(snippet));
                    hashMapMarker.put(item,mMarker);
                }

                //this gets one unique HarvestRecord
                HarvestRecord harvestRecord = harvestRecordEntries.get(mParamHarvestId);    //NOTE: HarvestId matches position in ArrayList for debugging purposes

                LatLng location = harvestRecord.getLatLng();

                CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(ZOOM_LEVEL_5).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                //Show marker
                Marker thisMarker = hashMapMarker.get(harvestRecord);
                thisMarker.showInfoWindow();;

                googleMap.getUiSettings().setZoomControlsEnabled(true);
            }
        });

       // googleMap.setOnMarkerClickListener(this);

        BindRecyclerView(rootView);


        // this is the view we will add the gesture detector to
        View myView = rootView.findViewById(R.id.recyclerView);
        // get the gesture detector
        mDetector = new GestureDetector(getActivity(), new MyGestureListener());
        // Add a touch listener to the view
        // The touch listener passes all its events on to the gesture detector
        myView.setOnTouchListener(touchListener);


        return rootView;
    }

    private void BindRecyclerView(View view){
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

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

                    float zoomLvl = googleMap.getCameraPosition().zoom == ZOOM_LEVEL_5 ? ZOOM_LEVEL_11 : ZOOM_LEVEL_5;
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(harvestRecord.getLatLng()).zoom(zoomLvl).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    Marker marker = hashMapMarker.get(harvestRecord);
                    marker.showInfoWindow();
                }
            });
        }
    }
    // This touch listener passes everything on to the gesture detector.
    // That saves us the trouble of interpreting the raw touch events
    // ourselves.
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // pass the events to the gesture detector
            // a return value of true means the detector is handling it
            // a return value of false means the detector didn't
            // recognize the event
            return mDetector.onTouchEvent(event);

        }
    };

    // In the SimpleOnGestureListener subclass you should override
    // onDown and any other gesture that you want to detect.
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("TAG","onDown: ");

            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("TAG", "onSingleTapConfirmed: ");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.i("TAG", "onLongPress: ");
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("TAG", "onDoubleTap: ");

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.i("TAG", "onScroll: ");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            Log.d("TAG", "onFling: ");

            ArrayList<HarvestRecord> harvestRecordEntries = ((MainActivity)getActivity()).getHarvestRecordArrayList();

            HarvestRecord harvestRecord = harvestRecordEntries.get(mParamHarvestId);

            if(harvestRecordEntries.indexOf(harvestRecord) == (harvestRecordEntries.size() -1)){
                //current item is last, reset to first
                mParamHarvestId = 0;
                harvestRecord = harvestRecordEntries.get(mParamHarvestId);
            }else {
                harvestRecord = harvestRecordEntries.get(++mParamHarvestId);
            }

            CameraPosition cameraPosition = new CameraPosition.Builder().target(harvestRecord.getLatLng()).zoom(ZOOM_LEVEL_5).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            BindRecyclerView(getView());

            Marker marker = hashMapMarker.get(harvestRecord);
            marker.showInfoWindow();;
            return true;


        }

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
    @Override
    public boolean onMarkerClick(final Marker marker) {


        return true;
    }
}
