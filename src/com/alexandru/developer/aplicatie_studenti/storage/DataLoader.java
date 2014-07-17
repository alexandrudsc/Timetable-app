package com.alexandru.developer.aplicatie_studenti.storage;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import com.alexandru.developer.aplicatie_studenti.MyActivity;
import com.alexandru.developer.aplicatie_studenti.R;
import com.alexandru.developer.aplicatie_studenti.app_widget.ListRemoteViewsFactory;
import com.alexandru.developer.aplicatie_studenti.view_pager.Course;
import com.alexandru.developer.aplicatie_studenti.view_pager.MyListViewAdapter;
import com.alexandru.developer.aplicatie_studenti.view_pager.PagerSlidingTabStrip;
import com.alexandru.developer.aplicatie_studenti.view_pager.ViewPagerAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Alexandru on 6/14/14.
 * Loads the json file in a background thread.
 * Must now for where it's called: from main activity or from remote service for widget's list
 * From main activity - view pager not null
 * From service - list remote views factory not null (the remoteViewsFactory for widget service)
 * When this thread is finished, display the content in the view pager
 * If it's called from the widget remote service, then display the content in the widget view
 *
 */
public class DataLoader extends AsyncTask<URL, Void, Void> {

    private Context context;
    private MyActivity activity;
    private ViewPager viewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private FragmentManager fragmentManager;

    private JSONArray jsonArray;
    private ListRemoteViewsFactory listRemoteViewsFactory;
    private String widgetProviderName;
    public static boolean widgetForTomorrow;

    public DataLoader(MyActivity activity,
                      ListRemoteViewsFactory listRemoteViewsFactory, Context context, String widgetProviderName){
        if(activity != null){
            //Thread spawn by main activity
            this.activity = activity;
            this.context = activity;
            this.viewPager = (ViewPager) activity.findViewById(R.id.view_pager);
            this.pagerSlidingTabStrip = (PagerSlidingTabStrip) activity.findViewById(R.id.sliding_tabs);
            this.fragmentManager = activity.getSupportFragmentManager();
        }
        else{
            //Thread spawn by remote views service for widget
            this.listRemoteViewsFactory = listRemoteViewsFactory;
            this.context = context;
            ViewPagerAdapter.context = context;

            this.widgetProviderName = widgetProviderName;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(URL ... urls) {
        if(context != null){
            try {

                SharedPreferences preferences = context.getSharedPreferences(MyActivity.PREFERENCES_FILE_NAME,
                        Context.MODE_PRIVATE);
                boolean appAtFirstRun = preferences.getBoolean(MyActivity.FIRST_RUN, true);

                SharedPreferences timeOrganiser = context.getSharedPreferences(MyActivity.TIME_ORGANISER_FILE_NAME,
                        Context.MODE_PRIVATE);

                final int currentWeek = timeOrganiser.getInt(MyActivity.WEEK_OF_SEMESTER, MyActivity.WEEKS_IN_SEMESTER);

                ViewPagerAdapter.context = context;
                DBAdapter dbAdapter = new DBAdapter(context);
                dbAdapter.open();
                JSONArray day = null;
                ArrayList<Course >courses = null;

                if(appAtFirstRun){

                    /*URL url = urls[0];//
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    InputStream inputStream = connection.getInputStream();
                    */
                    InputStream inputStream = context.getResources().openRawResource(R.raw.orar_usv_1111b);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder stringBuilder = new StringBuilder();
                    JSONObject jsonObject;
                    String line;

                    while((line = bufferedReader.readLine()) != null){
                        stringBuilder.append(line);
                    }
                    inputStream.close();
                    jsonObject = new JSONObject(stringBuilder.toString());
                    jsonArray = jsonObject.getJSONArray(ViewPagerAdapter.NAME_OF_DAYS);

                    //Save start and end date.Save holidays' dates.

                    if(timeOrganiser.getLong(MyActivity.START_DATE, 0) == 0){
                        SharedPreferences.Editor editor = timeOrganiser.edit();
                        JSONObject jsonTimeOrganiser = jsonObject.getJSONObject(ViewPagerAdapter.NAME_OF_SEMESTER_ORG);

                        editor.putLong(MyActivity.START_DATE,
                                Long.valueOf(jsonTimeOrganiser.get(ViewPagerAdapter.NAME_OF_START_DATE).toString()) );
                        editor.putLong(MyActivity.END_DATE,
                                Long.valueOf(jsonTimeOrganiser.get(ViewPagerAdapter.NAME_OF_END_DATE).toString()) );

                        JSONArray jsonHolidays = jsonTimeOrganiser.getJSONArray(ViewPagerAdapter.NAME_OF_HOLIDAYS);
                        JSONObject holiday;
                        int numberOfHolidays = jsonHolidays.length();
                        editor.putInt(MyActivity.NUMBER_OF_HOLIDAYS, numberOfHolidays);
                        for(int i = 0; i<numberOfHolidays; i++){
                            holiday = jsonHolidays.getJSONObject(i);
                            editor.putString(MyActivity.HOLIDAY + "_" + i,
                                    holiday.get(ViewPagerAdapter.NAME_OF_START_DATE).toString() + "-"
                                            + holiday.get(ViewPagerAdapter.NAME_OF_END_DATE).toString());
                        }
                        editor.commit();

                    }

                    ViewPagerAdapter.listViewAdapters = new MyListViewAdapter[ViewPagerAdapter.NUM_DAYS];

                    for(int i = 0; i < ViewPagerAdapter.NUM_DAYS; i++){
                        if(i != 0 && i != 6)
                            try {
                                day = jsonArray.getJSONObject(i-1).getJSONArray(ViewPagerAdapter.days[i]);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        else
                            day = null;

                        if(day != null){
                            courses = ViewPagerAdapter.getCoursesFromJSONArray(day);
                            ArrayList<Course> coursesForToday = new ArrayList<Course>();
                            for(int j = 0; j < courses.size(); j++){
                                Course c = courses.get(j);
                                dbAdapter.insertCourse(c, ViewPagerAdapter.days[i]);
                                if(isCourseInWeek(currentWeek, c))
                                    coursesForToday.add(c);
                            }
                            ViewPagerAdapter.listViewAdapters[i] = new MyListViewAdapter(context,coursesForToday);

                        }
                    }

                    dbAdapter.close();

                    preferences.edit().putBoolean(MyActivity.FIRST_RUN, false).commit();

                    // The list view adapter for every day of current week uses the current week value.
                    //Must be set now.Also the remote views agapter uses it.
                    MyActivity.setCurrentWeek(context);

                    //connection.disconnect();
                }
                else{
                    if(listRemoteViewsFactory != null){
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                            //Thread created by widget service
                            Log.d("LOADING THREAD", "from widget service");
                            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                            final ComponentName appWidget = new ComponentName(context, widgetProviderName);
                            final int[] appWidgetsId = appWidgetManager.getAppWidgetIds(appWidget);

                            Calendar calendar = Calendar.getInstance();
                            final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                            if(widgetForTomorrow){
                                ArrayList<Course> tomorrowCourses= null;
                                if(dayOfWeek != 7)
                                    tomorrowCourses = dbAdapter.getCourses(currentWeek,
                                            ViewPagerAdapter.days[dayOfWeek ]);
                                else
                                    tomorrowCourses = dbAdapter.getCourses(currentWeek + 1,
                                            ViewPagerAdapter.days[0]);
                                listRemoteViewsFactory.setValues(tomorrowCourses);
                            }
                            else {
                                final ArrayList<Course> todayCourses = dbAdapter.getCourses(currentWeek,
                                                            ViewPagerAdapter.days[dayOfWeek - 1]);
                                listRemoteViewsFactory.setValues(todayCourses);
                            }
                            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetsId, R.id.widget_list);
                        }
                    }else{

                        ViewPagerAdapter.listViewAdapters = new MyListViewAdapter[ViewPagerAdapter.NUM_DAYS];

                        for(int i = 0; i < ViewPagerAdapter.NUM_DAYS; i++){
                            ViewPagerAdapter.listViewAdapters[i] = new MyListViewAdapter(context,
                                    dbAdapter.getCourses(currentWeek, ViewPagerAdapter.days[i]));
                        }
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        // The list view adapter for every day of current week uses the current week value.
        //Must be set now.Also the remote views agapter uses it.
        MyActivity.setCurrentWeek(context);

        if(activity != null){

            //Thread createad by main activity
            Log.d("LOADING THREAD", "from main");
            ViewPagerAdapter adapter = new ViewPagerAdapter(fragmentManager, context);
            //ViewPagerAdapter.setDataToAdapters(jsonArray);
            viewPager.setAdapter(adapter);
            //ViewPagerAdapter.refreshContentToAll();
            pagerSlidingTabStrip.setViewPager(viewPager);

            final int lastVPPosition = activity.retrieveLastPosition();
            if(lastVPPosition == -1){
                Calendar cal = Calendar.getInstance();
                viewPager.setCurrentItem(cal.get(Calendar.DAY_OF_WEEK) - 1);
            }else
                viewPager.setCurrentItem(lastVPPosition);

            activity.initializeActionBar();

        }else
            ;

    }

    private boolean isCourseInWeek(int week, Course c){
        if (!c.info.equals(ViewPagerAdapter.COURSES_IN_EVEN_WEEK) && !c.info.equals(ViewPagerAdapter.COURSES_IN_ODD_WEEK))
            return true;

        if ((c.info.equals(ViewPagerAdapter.COURSES_IN_ODD_WEEK) && (week % 2 == 1)) ||
                (c.info.equals(ViewPagerAdapter.COURSES_IN_EVEN_WEEK) && week % 2 == 0))
                    return true;

        return false;
    }

}
