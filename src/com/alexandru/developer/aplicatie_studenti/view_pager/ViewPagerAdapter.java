package com.alexandru.developer.aplicatie_studenti.view_pager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.*;
import android.util.Log;
import com.alexandru.developer.aplicatie_studenti.MyActivity;
import com.alexandru.developer.aplicatie_studenti.R;
import com.alexandru.developer.aplicatie_studenti.action_bar.ListViewAdapterNonCurWeek;
import com.alexandru.developer.aplicatie_studenti.action_bar.SpinnerElementSelectedEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Alexandru on 6/13/14.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final String TAG = this.getClass().getCanonicalName().toUpperCase();
    public static final int NUM_DAYS = 7;
    private String[] daysFullName;

    public static Context context;
    private DayFragment[] fragments = new DayFragment[NUM_DAYS];


    //List view adapter for each day of the current week.
    public static MyListViewAdapter[] listViewAdapters ;

    public static final String[] days={"duminica", "luni", "marti", "miercuri", "joi", "vineri", "sambata"};

    public static final String NAME_OF_DAYS = "zile";
    public static final String NAME_OF_COURSE = "nume";
    public static final String FULL_NAME = "nume_complet";
    public static final String TYPE_OF_COURSE = "tip";
    public static final String INFO_ABOUT_REPETITION = "info";
    public static final String INFO_ABOUT_LOCATION = "locatie";
    public static final String INFO_ABOUT_TIME = "interval_orar";
    public static final String PROF = "cadru_didactic";
    public static final String COURSES_IN_EVEN_WEEK = "saptamanile pare";
    public static final String COURSES_IN_ODD_WEEK = "saptamanile impare";

    public static final String NAME_OF_SEMESTER_ORG = "organizare_semestru";
    public static final String NAME_OF_START_DATE = "start";
    public static final String NAME_OF_END_DATE = "stop";
    public static final String NAME_OF_HOLIDAYS = "vacante";

    public ViewPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        ViewPagerAdapter.context = context;
        if(daysFullName == null)
            daysFullName = context.getResources().getStringArray(R.array.days_of_week_full_name);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(daysFullName == null)
            daysFullName = context.getResources().getStringArray(R.array.days_of_week_full_name);
        return daysFullName[position];
    }

    @Override
    public Fragment getItem(int position) {
        if(daysFullName == null)
            daysFullName = context.getResources().getStringArray(R.array.days_of_week_full_name);
        if(fragments[position] != null){
            Log.d("FRAGMENT ADAPTER", "fragment already created");
            return fragments[position];
        }
        DayFragment dayFragment = DayFragment.createFragment(daysFullName[position], position);
        fragments[position] = dayFragment;
        return dayFragment;
    }

    @Override
    public int getCount() {
        return NUM_DAYS;
    }

    public static ArrayList<Course> getCoursesFromJSONArray(JSONArray jsonArray){
        //The json array contains the courses from an entire day.
        //This method return these.
        JSONObject courseJSON;
        String name, fullName, type, info, location, time, prof;
        int  i = 0;
        int n = jsonArray.length();
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyActivity.TIME_ORGANISER_FILE_NAME,
                                                                            Context.MODE_PRIVATE);
        int weekOfSemester = sharedPreferences.getInt(MyActivity.WEEK_OF_SEMESTER, MyActivity.WEEKS_IN_SEMESTER);
        ArrayList<Course> courses = new ArrayList<Course>();

        try {
            while (i<n){
                courseJSON = jsonArray.getJSONObject(i);
                name = courseJSON.getString(NAME_OF_COURSE);
                fullName = courseJSON.getString(FULL_NAME);
                type = courseJSON.getString(TYPE_OF_COURSE);
                info = courseJSON.getString(INFO_ABOUT_REPETITION);
                time = courseJSON.getString(INFO_ABOUT_TIME);
                location = courseJSON.getString(INFO_ABOUT_LOCATION);
                prof = courseJSON.getString(PROF);

                courses.add(new Course(name, fullName, type, location, time, prof, info));

                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public static boolean isAnyAdapterNull(){
        for(int i = 0;i < NUM_DAYS; i++)
            if(listViewAdapters[i] == null)
                return true;
        return false;
    }
}
