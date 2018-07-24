package edu.fsu.cs.mobile.outdoorsmanapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

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

                    mMarker.setTag(item);



                    hashMapMarker.put(item,mMarker);
                }

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean  onMarkerClick(Marker marker) {

                        HarvestRecord harvestRecord = (HarvestRecord)marker.getTag();
                        mParamHarvestId = harvestRecord.getId();

                        CameraPosition cameraPosition = new CameraPosition.Builder().target(harvestRecord.getLatLng()).zoom(ZOOM_LEVEL_5).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        BindRecyclerView(getView());
                        return false;
                    }
                });


                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        Log.d("TAG","onInfoWindowClick");
                        showHarvestSubmissionDialog();
                        position = marker.getPosition();
                    }
                });
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng point) {


                    }
                });

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

    private Calendar c;
    private int harvestRecordCounter;
    private int formTypeId;
    private String formType;
    private long harvestDate;
    private LatLng position;

    private void showHarvestSubmissionDialog(){
        Context context = getContext();


        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Submit New Harvest");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView textView = new TextView(getContext());
        final Button button = new Button(getContext());
        button.setText("Confirm");



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                harvestRecordCounter = ((MainActivity)getActivity()).getHarvestRecordArrayList().size();

                HarvestRecord harvestRecord = new HarvestRecord();
                harvestRecord.setId(++harvestRecordCounter);    //TODO: use id from firebase
                harvestRecord.setTypeId(formTypeId);
                harvestRecord.setType(formType);
                harvestRecord.setDate(harvestDate);
                harvestRecord.setLatLng(position);
                ((MainActivity)getActivity()).addHarvestRecordArrayListItem(harvestRecord);

                textView.setText(formType+" Submitted");
                textView.setVisibility(View.VISIBLE);


                LatLng location = harvestRecord.getLatLng();
                String title = "Type: " + harvestRecord.getType();
                String snippet = "Date: " + harvestRecord.getDateString();

                mMarker = googleMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(title)
                        .snippet(snippet));

                mMarker.setTag(harvestRecord);



                hashMapMarker.put(harvestRecord,mMarker);
            }
        });

        ListView listView = new ListView(getContext());

        String[] form_types = getResources().getStringArray(R.array.form_types);
        listView = AddListViewAdapter(listView, form_types);

        listView.setClickable(true);
        final ListView finalListView = listView;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object listItemObj = finalListView.getItemAtPosition(position);
                String type = (String)listItemObj;
                formTypeId = position;
                formType = type;
                finalListView.setVisibility(View.INVISIBLE);


                textView.setText("Please confirm "+formType+" form.");
                textView.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);

                //ConfirmFormType(formType,position); //NOTE: passing the position int as the typeId, see ~/res/values/R.arrays.form_types
                //TODO: Back up with data, set up web api or leave as it (hardcoded in strings.xml

            }
        });

        button.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);



        final CalendarView calendarView = new CalendarView(getContext());
        c = Calendar.getInstance();

        calendarView.setMaxDate(c.getTimeInMillis());
        calendarView.setDate(c.getTimeInMillis(),false,true);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int day) {
                c = Calendar.getInstance();
                c.clear();
                month++;
                c.set(year,  month, day, 11, 59);
                harvestDate = c.getTimeInMillis();
                calendarView.setVisibility(View.GONE);
                finalListView.setVisibility(View.VISIBLE);
            }

        });


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        textView.setLayoutParams(lp);
        calendarView.setLayoutParams(lp);
        layout.setLayoutParams(lp);

        layout.addView(textView);
        layout.addView(calendarView);
        layout.addView(listView);
        layout.addView(button);


        alertDialog.setView(layout);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CLOSE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
    private ListView AddListViewAdapter(ListView mylistview, String[] strings){
        //create ArrayList to bind adapater
        ArrayList<String> adapterList = new ArrayList<>(Arrays.asList(strings));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1, adapterList);
        //set adapter
        mylistview.setAdapter(arrayAdapter);

        return mylistview;
    }
}
