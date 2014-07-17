package com.alexandru.developer.aplicatie_studenti;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.alexandru.developer.aplicatie_studenti.action_bar.MySpinnerAdapter;
import com.alexandru.developer.aplicatie_studenti.action_bar.SpinnerElementSelectedEvent;
import com.alexandru.developer.aplicatie_studenti.storage.DataLoader;
import com.alexandru.developer.aplicatie_studenti.view_pager.PagerSlidingTabStrip;
import com.alexandru.developer.aplicatie_studenti.view_pager.ViewPagerAdapter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;


public class MyActivity extends ActionBarActivity{
    public static final String CURRENT_WEEK_PROGR_PREFS = "current_week_progress";
    public static final String PREFERENCES_FILE_NAME = "preferences";
    public static final String TIME_ORGANISER_FILE_NAME = "time_organiser";

    public static final String FIRST_RUN = "first_run";

    public static final String WEEK_OF_SEMESTER = "week_of_semester";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "stop_date";
    public static final String HOLIDAY = "holiday";
    public static final String  NUMBER_OF_HOLIDAYS = "no_of_holiday";
    public static final String VP_LAST_POSITION = "vp_last_position";

    public static long WEEK_IN_MILLIS = 7 * 24 * 3600 * 1000;
    public static int WEEKS_IN_SEMESTER = 14;
    public static int IS_HOLIDAY = -1;

    public ViewPager viewPager;
    public PagerSlidingTabStrip pagerSlidingTabStrip;
    private  ActionBarDrawerToggle drawerToggle;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCurrentWeek(this);
        setContentView(R.layout.main);

        //viewPager = (ViewPager) this.findViewById(R.id.view_pager);
        //pagerSlidingTabStrip = (PagerSlidingTabStrip) this.findViewById(R.id.sliding_tabs);
        DrawerLayout drawerLayout = (DrawerLayout)this.findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(this,
                                                drawerLayout,
                                                R.drawable.ic_navigation_drawer,
                                                R.string.title_nav_opened,
                                                R.string.title_nav_closed){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeActionBar();
        initializeNavDrawer();
    }
    /*@Override
    protected void onResume() {
        super.onResume();

        //Test if the async task hasn't just finished.If true no need to execute it again.
        //But if list view adapters are null, async task was finished long time ago, so there it's
        //isn't anything stored on RAM.The file must be loaded again.
        if(ViewPagerAdapter.listViewAdapters == null || ViewPagerAdapter.isAnyAdapterNull()){
            Log.d("MAIN ON_RESUME", "new loader");
            loadJSONFileAndSetData();
        }
        else{
            Log.d("MAIN ON_RESUME", "old loader");
            setDataFromLoadedFile();
            final int lastPosition = retrieveLastPosition();
            if(lastPosition == -1){
                Calendar cal = Calendar.getInstance();
                viewPager.setCurrentItem(cal.get(Calendar.DAY_OF_WEEK) - 1);
            }else
                viewPager.setCurrentItem(lastPosition);
            setCurrentWeek(this);
            initializeActionBar();
        }

        initializeNavDrawer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        final int lastPosition = viewPager.getCurrentItem();
        SharedPreferences sharedPreferences = this.getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(VP_LAST_POSITION, lastPosition);
        editor.commit();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager)this.getSystemService(SEARCH_SERVICE);

        SearchView  searchView = (SearchView) MenuItemCompat.getActionView((menu.findItem(R.id.search)));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(VP_LAST_POSITION);
        editor.commit();
        super.onDestroy();

    }

    public void showDialog(){

    }

    public static void setCurrentWeek(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(TIME_ORGANISER_FILE_NAME, MODE_PRIVATE);

        final long startDateInMillis = sharedPreferences.getLong(START_DATE, 0);
        final long endDateInMillis = sharedPreferences.getLong(END_DATE, 0);
        final long currentTimeInMillis = System.currentTimeMillis();
        int currentWeek ;
        Log.d("MAIN", "now "+currentTimeInMillis+" end "+endDateInMillis);
        if(currentTimeInMillis > endDateInMillis)
            currentWeek = WEEKS_IN_SEMESTER;
        else
            if(currentTimeInMillis < startDateInMillis)
                currentWeek = 1;
            else{
                final long vacationTime = calculateVacationTime(context);
                if(vacationTime == IS_HOLIDAY){
                    Log.d("MAIN", "is holiday");
                    currentWeek = sharedPreferences.getInt(MyActivity.WEEK_OF_SEMESTER, 1);
                }
                else
                    currentWeek = (int) ((currentTimeInMillis - startDateInMillis - vacationTime) / WEEK_IN_MILLIS);
            }
        if(sharedPreferences.getInt(WEEK_OF_SEMESTER, -1) !=  currentWeek){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(WEEK_OF_SEMESTER, currentWeek);
            editor.commit();
        }
    }

    private static long calculateVacationTime(Context context){
        long freeTime = 0;
        final SharedPreferences timeOrg = context.getSharedPreferences(TIME_ORGANISER_FILE_NAME, MODE_PRIVATE);
        final int numberOfHolidays = timeOrg.getInt(NUMBER_OF_HOLIDAYS, 0);
        String holiday;
        for(int i = 0; i< numberOfHolidays; i++){
            holiday = timeOrg.getString(MyActivity.HOLIDAY + "_" + i, "0-0");
            final long durationOfHoliday = calculateDurationOfHoliday(holiday);
            if(durationOfHoliday == IS_HOLIDAY)
                return IS_HOLIDAY;
            freeTime += calculateDurationOfHoliday(holiday);
        }
        return freeTime;
    }

    private static long calculateDurationOfHoliday(String holiday){
        long time = 0 ;
        String[] startEnd = holiday.split("-");
        final long start = Long.valueOf(startEnd[0]);
        final long end =  Long.valueOf(startEnd[1]);
        time = end - start;
        if(System.currentTimeMillis() >= start && System.currentTimeMillis()<=end)
            return IS_HOLIDAY;
        return time;
    }

    private void loadJSONFileAndSetData(){
        URL[] urls = new URL[1];
        try {
            urls[0] = new URL("https://docs.google.com/uc?authuser=0&id=0B81UjA_fISK7QWNhdEFxS3Rqanc&export=download");
            new DataLoader(this, null, null, null).execute(urls);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void setDataFromLoadedFile(){
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), this));
        pagerSlidingTabStrip.setViewPager(viewPager);

    }

    public void initializeActionBar(){
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        final MySpinnerAdapter spinnerAdapter = new MySpinnerAdapter(this);

        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(spinnerAdapter, new SpinnerElementSelectedEvent(this));
        actionBar.setSelectedNavigationItem(getSharedPreferences(TIME_ORGANISER_FILE_NAME, MODE_PRIVATE)
                                            .getInt(WEEK_OF_SEMESTER, WEEKS_IN_SEMESTER) - 1);

    }

    public void initializeNavDrawer(){
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();


        ListView drawerList = (ListView) findViewById(R.id.left_drawer);

        drawerList.setAdapter(new NavDrawerAdapter(this));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            }
        });
        drawerLayout.setDrawerListener(drawerToggle);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    public int retrieveLastPosition(){
        SharedPreferences sharedPreferences = this.getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt(VP_LAST_POSITION, -1);
    }


}