package edu.fsu.cs.mobile.outdoorsmanapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;

public class HarvestRecord {

    int id;
    int typeId;
    String type;
    long date;
    LatLng latLng;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Calendar convertMillisToCalendar(long ms){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);

        return calendar;
    }
    public String getDateString() {

        Calendar calendar = convertMillisToCalendar(this.date);  //get self date

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        int mHour = calendar.get(Calendar.HOUR);
        int mMinute = calendar.get(Calendar.MINUTE);
        int mSecond = calendar.get(Calendar.SECOND);


        CharSequence AM_OR_PM = calendar.get(Calendar.AM_PM) == 1 ? "PM" : "AM";


        return mMonth + "/" + mDay+"/" + mYear + " " + mHour + ":" + mMinute + " "+ AM_OR_PM;

    }
}
