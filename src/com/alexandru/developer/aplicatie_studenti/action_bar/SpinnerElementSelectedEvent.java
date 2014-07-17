package com.alexandru.developer.aplicatie_studenti.action_bar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import com.alexandru.developer.aplicatie_studenti.MyActivity;
import com.alexandru.developer.aplicatie_studenti.view_pager.Course;
import com.alexandru.developer.aplicatie_studenti.view_pager.ViewPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Alexandru on 6/1/14.
 */
public class SpinnerElementSelectedEvent implements ActionBar.OnNavigationListener {
    public static final String
                        ACTION_LAUNCH_EVENT_ACTIVITY = "com.alexandru.developer.action.LAUNCH_NON_CURRENT_WEEK_ACTIVITY";
    public static final String
                        CATEGORY_NON_CURRENT_WEEK = "com.alexandru.developer.category.SECOND_ACTIVITY";
    public static NonCurrentWeekDay[] coursesNonCurrentWeek = new NonCurrentWeekDay[7];

    private Context context;

    public SpinnerElementSelectedEvent(Context context) {
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(int position, long itemId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyActivity.TIME_ORGANISER_FILE_NAME,
                                                                            Context.MODE_PRIVATE);
        int weekOfSemester = sharedPreferences.getInt(MyActivity.WEEK_OF_SEMESTER, MyActivity.WEEKS_IN_SEMESTER);
        if(position != (weekOfSemester -1)){

            Intent intent = new Intent(context, NonCurrentWeekActivity.class);
            intent.setAction(ACTION_LAUNCH_EVENT_ACTIVITY);

            intent.putExtra(NonCurrentWeekActivity.NAME_OF_WEEK_NUMBER, position + 1);
            context.startActivity(intent);
        }
        return true;
    }

    public static class NonCurrentWeekDay{
        public ArrayList<Course> values = new ArrayList<Course>();
    }

}
