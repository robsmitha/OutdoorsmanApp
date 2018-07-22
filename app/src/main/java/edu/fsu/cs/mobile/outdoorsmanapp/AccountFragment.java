package edu.fsu.cs.mobile.outdoorsmanapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    public AccountFragment() {
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
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        TextView textView = view.findViewById(R.id.textViewAccount);


        //TODO: Set up badges

        final String[] form_types = getResources().getStringArray(R.array.form_types);

        final ArrayList<HarvestRecord> arrayList =((MainActivity)getActivity()).getHarvestRecordArrayList();
        ListView mainListView = view.findViewById(R.id.mainListView);
        mainListView = AddListViewAdapter(mainListView, form_types);
        mainListView.setClickable(true);

        final ListView finalMainListView = mainListView;
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object listItemObj = finalMainListView.getItemAtPosition(position);
                String formType = (String)listItemObj;
                int count = 0;
                if(!arrayList.isEmpty()){
                    for(int i = 0; i < arrayList.size(); i++){
                        if(arrayList.get(i).getType().equals(formType)){
                            count++;
                        }
                    }

                }
                CharSequence message = ""+formType+ " : "+ count;
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton imageButtonForm = (ImageButton) view.findViewById(R.id.imageButtonForm);
        imageButtonForm.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FormFragment formFragment = new FormFragment();
                ((MainActivity)getActivity()).OnFragmentReplaced(formFragment);
            }
        });

        ImageButton imageButtonRecords = (ImageButton) view.findViewById(R.id.imageButtonRecords);
        imageButtonRecords.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RecordListFragment recordListFragment = new RecordListFragment();
                ((MainActivity)getActivity()).OnFragmentReplaced(recordListFragment);
            }
        });
        return view;
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
