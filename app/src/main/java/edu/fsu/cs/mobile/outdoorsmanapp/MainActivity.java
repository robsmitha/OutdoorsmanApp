package edu.fsu.cs.mobile.outdoorsmanapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();
    private Toolbar mToolbar;
    private ArrayList<HarvestRecord> HarvestRecordArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        AccountFragment accountFragment = new AccountFragment();
        OnFragmentReplaced(accountFragment);
    }

    //Temporary ArrayList methods for testing
    public ArrayList<HarvestRecord> getHarvestRecordArrayList() {
        if(HarvestRecordArrayList == null){
            HarvestRecordArrayList = new ArrayList<>();
        }
        return HarvestRecordArrayList;
    }

    public void addHarvestRecordArrayListItem(HarvestRecord harvestRecord){
        if(HarvestRecordArrayList == null){
            HarvestRecordArrayList = new ArrayList<>();
        }
        HarvestRecordArrayList.add(harvestRecord);
    }
    //END Temporary ArrayList methods for testing

    public void OnFragmentReplaced(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                AccountFragment accountFragment = new AccountFragment();
                OnFragmentReplaced(accountFragment);
                break;
            case R.id.action_form:
                FormFragment formFragment = new FormFragment();
                OnFragmentReplaced(formFragment);
                break;
            case R.id.action_record_list:
                RecordListFragment recordListFragment = new RecordListFragment();
                OnFragmentReplaced(recordListFragment);
                break;
        }

        return true;
    }
}
