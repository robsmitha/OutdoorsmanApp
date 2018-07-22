package edu.fsu.cs.mobile.outdoorsmanapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FormFragment extends Fragment {

    public static int harvestRecordCounter = 0;
    public final static int case_submit_again = 0;
    public final static int case_record_list = 1;
    long date;
    Calendar c;

    public FormFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_form, container, false);

        BindFormTypes(view);

        return view;

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

        date = calendarViewCurrentDate.getDate();

        calendarViewCurrentDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int day) {
                c = Calendar.getInstance();
                c.clear();
                month++;
                c.set(year,  month, day, 11, 59);
                date = c.getTimeInMillis();
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
                    switchCurrentDate.setText("Enter the Date");
                    calendarViewCurrentDate.setVisibility(View.VISIBLE);
                }else{
                    //we're going to grab the current time at the time of submission
                    //calendarViewCurrentDate.setDate(System.currentTimeMillis());
                    calendarViewCurrentDate.setEnabled(false);
                    switchCurrentDate.setText("Record the Current Date");
                    calendarViewCurrentDate.setVisibility(View.INVISIBLE);
                }
            }
        });


        //TODO: determine if phone has location services on
        //TODO: apply "checked" logic / show TextViews if not checked
        final EditText editTextLatitude = view.findViewById(R.id.editTextLatitude);
        editTextLatitude.setVisibility(View.VISIBLE);
        editTextLatitude.setText("-34");
        editTextLatitude.setEnabled(false);

        final EditText editTextLongitude = view.findViewById(R.id.editTextLongitude);
        editTextLongitude.setVisibility(View.VISIBLE);
        editTextLongitude.setText("151");
        editTextLongitude.setEnabled(false);


        final Switch switchCurrentLocation = view.findViewById(R.id.switchRecordLocation);
        switchCurrentLocation.setVisibility(View.VISIBLE);
        switchCurrentLocation.setChecked(true);

        switchCurrentLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position


                //reset errors
                editTextLatitude.setError(null);
                editTextLongitude.setError(null);

                if(!isChecked){
                    editTextLatitude.setText(null); //clear fields
                    editTextLongitude.setText(null);
                    editTextLatitude.setEnabled(true);  //enable for input
                    editTextLongitude.setEnabled(true);
                    switchCurrentLocation.setText("Enter Location");    //update label
                }else{

                    editTextLatitude.setText("-34");    //set default values
                    editTextLongitude.setText("151");
                    editTextLatitude.setEnabled(false); //disable
                    editTextLongitude.setEnabled(false);
                    switchCurrentLocation.setText("Record Current Location");   //update label
                }
            }
        });

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

    private void ConfirmFormType(String formType, int typeId){

        View view = getView();
        boolean isValid = true;

        Switch switchCurrentDate = view.findViewById(R.id.switchRecordCurrentDate);
        CalendarView calendarViewCurrentDate = view.findViewById(R.id.calendarViewCurrentDate);

        long timeStamp = System.currentTimeMillis();//grab current time

        if(!switchCurrentDate.isChecked()){
            if(calendarViewCurrentDate.getDate() == 0){
                isValid = false;
            }
            else {
                timeStamp = date;  //set in calendarViewCurrentDate.setOnDateChangeListener()
            }
        }

        Switch switchCurrentLocation = view.findViewById(R.id.switchRecordLocation);
        EditText editTextLatitude = view.findViewById(R.id.editTextLatitude);
        EditText editTextLongitude = view.findViewById(R.id.editTextLongitude);

        double latitude = -34;
        double longitude = 151;

        if(!switchCurrentLocation.isChecked()){
            if(editTextLatitude.getText().toString().isEmpty()){
                editTextLatitude.setError("Please enter a date");
                isValid = false;
            }else{
                latitude = Double.parseDouble(editTextLatitude.getText().toString());
            }
            if(editTextLongitude.getText().toString().isEmpty()){
                editTextLongitude.setError("Please enter a date");
                isValid = false;
            }else{
                longitude = Double.parseDouble(editTextLongitude.getText().toString());
            }
        }

        TextView textView = view.findViewById(R.id.textView4);

        if(isValid){
            switchCurrentDate.setVisibility(View.INVISIBLE);
            calendarViewCurrentDate.setVisibility(View.INVISIBLE);
            switchCurrentLocation.setVisibility(View.INVISIBLE);
            editTextLatitude.setVisibility(View.INVISIBLE);
            editTextLongitude.setVisibility(View.INVISIBLE);


            //Add this record to the HarvestRecordArrayList in MainActivity
            //TODO: add records to firebase, get location serivce, check if switches for date & calendar are "checked"
            HarvestRecord harvestRecord = new HarvestRecord();
            harvestRecord.setId(harvestRecordCounter++);    //TODO: use id from firebase
            harvestRecord.setTypeId(typeId);
            harvestRecord.setType(formType);
            harvestRecord.setDate(timeStamp);
            harvestRecord.setLatLng(new LatLng(latitude, longitude));
            ((MainActivity)getActivity()).addHarvestRecordArrayListItem(harvestRecord);


            //Set UI message
            textView.setText(formType + " Form Type submitted at: " + harvestRecord.getDateString());


            //rebind listview with "after submit options", what to do after submission
            String[] form_types_after_submit_options = getResources().getStringArray(R.array.form_types_after_submit_options);
            ListView mainListView = view.findViewById(R.id.mainListView);
            mainListView = AddListViewAdapter(mainListView, form_types_after_submit_options);
            mainListView.setClickable(true);

            final ListView finalMainListView = mainListView;
            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    switch (position){
                        case case_submit_again:
                            //rebind
                            BindFormTypes(getView());   //Submit Another
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
        ArrayList<String> adapterList = new ArrayList<>();
        adapterList.addAll(Arrays.asList(strings));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (getContext(), android.R.layout.simple_list_item_1, adapterList);
        //set adapter
        mylistview.setAdapter(arrayAdapter);

        return mylistview;
    }
}
