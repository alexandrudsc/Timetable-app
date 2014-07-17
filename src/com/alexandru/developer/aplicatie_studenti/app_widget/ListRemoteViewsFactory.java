package com.alexandru.developer.aplicatie_studenti.app_widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.alexandru.developer.aplicatie_studenti.R;
import com.alexandru.developer.aplicatie_studenti.view_pager.Course;
import com.alexandru.developer.aplicatie_studenti.view_pager.ViewPagerAdapter;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Alexandru on 5/30/14.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<Course> valuesToday;
    private int mWidgetId;
    private Context mContext;

    public ListRemoteViewsFactory(Context context, Intent intent){
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        mContext = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (valuesToday == null)
            return 0;
        return valuesToday.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // Construct a remote views object based on the app widget item XML file,
        // and set the text based on the position.This will be a row in the list

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_layout);
        remoteViews.setTextViewText(R.id.event_name_widget, valuesToday.get(position).name+" "+
                                    valuesToday.get(position).type);
        remoteViews.setTextViewText(R.id.event_description_widget, valuesToday.get(position).time+" "+
                                    valuesToday.get(position).location);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public void setValues(ArrayList<Course> newValues){
        if(this.valuesToday != null){
            this.valuesToday.clear();
            this.valuesToday.addAll(newValues);
        }else
            this.valuesToday = newValues;
    }

    public boolean hasValues(){
        return valuesToday == null || valuesToday.size() == 0;
    }

}
