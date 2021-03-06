package edu.fsu.cs.mobile.outdoorsmanapp;


import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
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
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class FormFragment extends Fragment {
    MapView mMapView;
    Marker mMarker;
    private GoogleMap googleMap;
    private final static int ZOOM_LEVEL_5 = 5;

    public static int harvestRecordCounter = 0;
    public final static int case_submit_again = 0;
    public final static int case_record_list = 1;

    double latitude;
    double longitude;
    long harvestDate;
    Calendar c;

    private Location mainActivityLocation;

    public FormFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_form, container, false);
        mMapView = view.findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState);

        BindFormTypes(view);

        return view;

    }

    private void getLastLocation(){

        mainActivityLocation = ((MainActivity) Objects.requireNonNull(getActivity())).getCurrentLocation();
        latitude = mainActivityLocation.getLatitude();
        longitude = mainActivityLocation.getLongitude();

    }

    private void BindFormTypes(View view){

        final CalendarView calendarViewCurrentDate = view.findViewById(R.id.calendarViewCurrentDate);

        c  = Calendar.getInstance();

        calendarViewCurrentDate.setMaxDate(c.getTimeInMillis());
        
        calendarViewCurrentDate.setDate(c.getTimeInMillis(),false,true);
        calendarViewCurrentDate.setVisibility(View.INVISIBLE);
        calendarViewCurrentDate.setEnabled(false);

        final Switch switchCurrentDate = view.findViewById(R.id.switchRecordCurrentDate);
        switchCurrentDate.setVisibility(View.VISIBLE);
        switchCurrentDate.setChecked(true);

        harvestDate = calendarViewCurrentDate.getDate();

        calendarViewCurrentDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month,
                                            int day) {
                c = Calendar.getInstance();
                c.clear();
                month++;
                c.set(year,  month, day, 11, 59);
                harvestDate = c.getTimeInMillis();
                switchCurrentDate.setText(month+"/"+day+"/"+year);
            }

        });


        switchCurrentDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(!isChecked){
                    calendarViewCurrentDate.setDateTextAppearance(Calendar.getInstance().getFirstDayOfWeek());
                    calendarViewCurrentDate.setEnabled(true);
                    switchCurrentDate.setText(R.string.enterDate);
                    calendarViewCurrentDate.setVisibility(View.VISIBLE);
                }else{
                    //we're going to grab the current time at the time of submission
                    //calendarViewCurrentDate.setDate(System.currentTimeMillis());
                    calendarViewCurrentDate.setEnabled(false);
                    switchCurrentDate.setText(R.string.recCurDate);
                    calendarViewCurrentDate.setVisibility(View.INVISIBLE);
                }
            }
        });



        getLastLocation();

        final Switch switchCurrentLocation = view.findViewById(R.id.switchRecordLocation);
        switchCurrentLocation.setVisibility(View.VISIBLE);
        switchCurrentLocation.setChecked(true);
        //TODO: apply "checked" logic / show TextViews if not checked

        switchCurrentLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((MainActivity) getActivity()).getLastLocation();

                // do something, the isChecked will be
                // true if the switch is in the On position
                if(!isChecked){
                    latitude = 0;
                    longitude = 0;
                    mMapView.setVisibility(View.VISIBLE);
                    switchCurrentLocation.setText(R.string.pickLoc);    //update label
                }else{
                    getLastLocation();
                    mMapView.setVisibility(View.GONE);
                    switchCurrentLocation.setText(R.string.recCurLoc);   //update label
                }
            }
        });

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


                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onMapClick(LatLng point) {

                        if(mMarker != null){
                            mMarker.remove();
                        }

                        latitude = point.latitude;
                        longitude = point.longitude;
                        switchCurrentLocation.setText(getString(R.string.latitude)+latitude + getString(R.string.longitude)+longitude);

                        String title = getString(R.string.latitude)+latitude + latitude;
                        String snippet = getString(R.string.longitude) + longitude;

                        mMarker = googleMap.addMarker(new MarkerOptions()
                                .position(point)
                                .title(title)
                                .snippet(snippet));
                    }
                });

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(mainActivityLocation.getLatitude(),mainActivityLocation.getLongitude()))
                        .zoom(ZOOM_LEVEL_5).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.getUiSettings().setZoomControlsEnabled(true);
            }
        });
        mMapView.setVisibility(View.GONE);



        TextView textView = view.findViewById(R.id.textView4);
        textView.setText(R.string.title_form_fragment);


        String[] form_types = getResources().getStringArray(R.array.form_types);
        ListView mainListView = view.findViewById(R.id.mainListView);
        mainListView = AddListViewAdapter(mainListView, form_types);
        mainListView.setClickable(true);

        final ListView finalMainListView = mainListView;
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object listItemObj = finalMainListView.getItemAtPosition(position);
                String formType = (String)listItemObj;
                ConfirmFormType(formType,position); //NOTE: passing the position int as the typeId, see ~/res/values/R.arrays.form_types
                //TODO: Back up with data, set up web api or leave as it (hardcoded in strings.xml

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void ConfirmFormType(String formType, int typeId){

        View view = getView();
        boolean isValid = true;

        Switch switchCurrentDate = view.findViewById(R.id.switchRecordCurrentDate);
        CalendarView calendarViewCurrentDate = view.findViewById(R.id.calendarViewCurrentDate);

        long timeStamp = System.currentTimeMillis();//grab current time

        if(!switchCurrentDate.isChecked()){
            if(calendarViewCurrentDate.getDate() == 0){
                switchCurrentDate.setText(R.string.chooseDate);
                isValid = false;
            }
            else {
                timeStamp = harvestDate;  //set in calendarViewCurrentDate.setOnDateChangeListener()
            }
        }

        Switch switchCurrentLocation = view.findViewById(R.id.switchRecordLocation);

        if(longitude == 0 && latitude == 0){
            switchCurrentLocation.setText(R.string.chooseLoc);
            isValid = false;
        }

        TextView textView = view.findViewById(R.id.textView4);

        if(isValid){
            switchCurrentDate.setVisibility(View.INVISIBLE);
            calendarViewCurrentDate.setVisibility(View.INVISIBLE);
            switchCurrentLocation.setVisibility(View.INVISIBLE);

            if(mMarker != null){
                mMarker.remove();
            }
            mMapView.setVisibility(View.GONE);


            //Add this record to the HarvestRecordArrayList in MainActivity
            HarvestRecord harvestRecord = new HarvestRecord(((MainActivity)getActivity()).getCurrentUser());
            harvestRecord.setId(harvestRecordCounter++);
            harvestRecord.setTypeId(typeId);
            harvestRecord.setType(formType);
            harvestRecord.setDate(timeStamp);
            harvestRecord.setLatLng(new LatLng(latitude, longitude));

            ((MainActivity)getActivity()).updateRecord(harvestRecord);

            //Set UI message
            textView.setText(formType + getString(R.string.formSubmitted) + harvestRecord.getDateString());


            //rebind listview with "after submit options", what to do after submission
            String[] form_types_after_submit_options = getResources().getStringArray(R.array.form_types_after_submit_options);
            ListView mainListView = view.findViewById(R.id.mainListView);
            mainListView = AddListViewAdapter(mainListView, form_types_after_submit_options);
            mainListView.setClickable(true);

            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    switch (position){
                        case case_submit_again:
                            //rebind
                            BindFormTypes(Objects.requireNonNull(getView()));   //Submit Another
                            break;
                        case case_record_list:
                            //go to list
                            RecordListFragment recordListFragment = new RecordListFragment();
                            ((MainActivity)getActivity()).OnFragmentReplaced(recordListFragment);
                            break;
                        default:
                            //go home
                            AccountFragment accountFragment = new AccountFragment();
                            ((MainActivity)getActivity()).OnFragmentReplaced(accountFragment);
                            break;
                    }
                }
            });

        }else {
            textView.setText("Please fill out all fields");
        }
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
