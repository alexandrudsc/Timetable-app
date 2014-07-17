package com.alexandru.developer.aplicatie_studenti.view_pager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.alexandru.developer.aplicatie_studenti.CheckBoxOnChangeListener;
import com.alexandru.developer.aplicatie_studenti.MyActivity;
import com.alexandru.developer.aplicatie_studenti.R;
import com.alexandru.developer.aplicatie_studenti.SearchableActivity;
import com.alexandru.developer.aplicatie_studenti.action_bar.ListViewAdapterNonCurWeek;
import com.alexandru.developer.aplicatie_studenti.action_bar.NonCurrentWeekActivity;
import com.alexandru.developer.aplicatie_studenti.action_bar.SpinnerElementSelectedEvent;

import java.util.ArrayList;

/**
 * Created by Alexandru on 6/16/14.
 */
public class MyListViewAdapter extends BaseAdapter {

    private ArrayList<Course> values;
    private Context context;

    private SharedPreferences currentWeeksProgress;
    private int currentWeek;
    private String currentWeekFileName;

    public MyListViewAdapter(Context context, ArrayList<Course> values) {
        this.context = context;
        this.values = values;
        this.currentWeek = context.getSharedPreferences(MyActivity.TIME_ORGANISER_FILE_NAME, Context.MODE_PRIVATE).
                getInt(MyActivity.WEEK_OF_SEMESTER, MyActivity.WEEKS_IN_SEMESTER);
        this.currentWeekFileName = NonCurrentWeekActivity.PARTIAL_NAME_BACKUP_FILE + currentWeek;

        currentWeeksProgress = context.getSharedPreferences(currentWeekFileName, Context.MODE_PRIVATE);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        if(values == null)
            return 0;
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_layout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.eventName = (TextView)convertView.findViewById(R.id.event_name);
            viewHolder.eventType = (TextView)convertView.findViewById(R.id.event_type);
            viewHolder.eventTime = (TextView)convertView.findViewById(R.id.event_time);
            viewHolder.eventLocation = (TextView)convertView.findViewById(R.id.event_location);

            viewHolder.eventCheckBox = (CheckBox)convertView.findViewById(R.id.event_checkbox);

            convertView.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) convertView.getTag();
        final Course c = values.get(position);
        viewHolder.eventName.setText(c.name);
        viewHolder.eventType.setText(c.type);
        viewHolder.eventTime.setText(c.time);
        viewHolder.eventLocation.setText(c.location);
        viewHolder.eventCheckBox.setOnCheckedChangeListener(
                                new CheckBoxOnChangeListener(currentWeekFileName,
                                                           c.name+ "_" +
                                                           c.type ) );

        boolean currentCourseProgress;
        if(currentWeeksProgress == null)
            currentWeeksProgress = context.getSharedPreferences(currentWeekFileName, Context.MODE_PRIVATE);

        currentCourseProgress = currentWeeksProgress.getBoolean(c.name+ "_" +
                c.type, false);
        if(currentCourseProgress)
            viewHolder.eventCheckBox.setChecked(true);

        RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.course_in_list_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CLICKED ON", c.fullName);
                Intent viewDetails = new Intent(context, SearchableActivity.class);
                viewDetails.setAction(SearchableActivity.actionViewDetails);
                Bundle args = new Bundle();
                args.putParcelable("course_to_view", c);
                viewDetails.putExtras(args);
                context.startActivity(viewDetails);
            }
        });

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return (values == null || values.size() == 0);
    }

    public void setValues(ArrayList<Course> values){
        this.values.clear();
        this.values.addAll(values);
        this.notifyDataSetChanged();
    }

    public ArrayList<Course> getValues(){
        return this.values;
    }

    public static class ViewHolder{
        public TextView eventName, eventType, eventTime, eventLocation;
        ImageView eventColor;
        CheckBox eventCheckBox;
    }

}

