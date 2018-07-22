package edu.fsu.cs.mobile.outdoorsmanapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FormFragment extends Fragment {

    public static int harvestRecordCounter = 0;
    public final static int case_submit_again = 0;
    public final static int case_record_list = 1;

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

        //TODO: determine if phone has location services on
        //TODO: apply "checked" logic / show TextViews if not checked
        Switch switchCurrentLocation = view.findViewById(R.id.switchRecordLocation);
        switchCurrentLocation.setVisibility(View.VISIBLE);
        switchCurrentLocation.setChecked(true);

        //TODO: apply "checked" logic / show calendar if not checked
        Switch switchCurrentDate = view.findViewById(R.id.switchRecordCurrentDate);
        switchCurrentDate.setVisibility(View.VISIBLE);
        switchCurrentDate.setChecked(true);

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

        Switch switchCurrentDate = view.findViewById(R.id.switchRecordCurrentDate);
        Switch switchCurrentLocation = view.findViewById(R.id.switchRecordLocation);
        switchCurrentDate.setVisibility(View.INVISIBLE);
        switchCurrentLocation.setVisibility(View.INVISIBLE);

        TextView textView = view.findViewById(R.id.textView4);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        //Set UI message
        textView.setText(formType + " Form Type submitted at: " + timeStamp);




        //Add this record to the HarvestRecordArrayList in MainActivity
        //TODO: add records to firebase, get location serivce, check if switches for date & calendar are "checked"
        HarvestRecord harvestRecord = new HarvestRecord();
        harvestRecord.setId(harvestRecordCounter++);    //TODO: use id from firebase
        harvestRecord.setTypeId(typeId);
        harvestRecord.setType(formType);
        harvestRecord.setDate(timeStamp);
        harvestRecord.setLatLng(new LatLng(-34, 151));
        ((MainActivity)getActivity()).addHarvestRecordArrayListItem(harvestRecord);



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
