package com.alexandru.developer.aplicatie_studenti.action_bar;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.alexandru.developer.aplicatie_studenti.MyActivity;
import com.alexandru.developer.aplicatie_studenti.R;

/**
 * Created by Alexandru on 7/2/14.
 */
public class NonCurrentWeekActivity extends ListActivity {
    public static final String NAME_OF_WEEK_NUMBER = "week_number";
    public static final String PARTIAL_NAME_BACKUP_FILE = "saptamana_";
    public static final String NAME_OF_WEEK = "week";

    private ListViewAdapterNonCurWeek adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.non_current_week_activity);

        Intent intent = getIntent();
        int weekNumber = intent.getIntExtra(NAME_OF_WEEK_NUMBER, MyActivity.WEEKS_IN_SEMESTER);
        String backupFileName = PARTIAL_NAME_BACKUP_FILE + weekNumber;
        if(Build.VERSION.SDK_INT >= 11)
            getActionBar().setTitle("Săptămâna " + weekNumber);
        else
            this.setTitle("Săptămâna " + weekNumber);

        this.setListAdapter(new ListViewAdapterNonCurWeek(this,
                backupFileName,
                weekNumber));
        try{
            this.setListAdapter(new ListViewAdapterNonCurWeek(this, backupFileName, weekNumber));

        }catch (NullPointerException e){
            finish();
            e.printStackTrace();
        }
    }

}
