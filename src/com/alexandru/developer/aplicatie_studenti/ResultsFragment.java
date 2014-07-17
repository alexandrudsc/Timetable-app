package com.alexandru.developer.aplicatie_studenti;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.alexandru.developer.aplicatie_studenti.MyActivity;
import com.alexandru.developer.aplicatie_studenti.R;
import com.alexandru.developer.aplicatie_studenti.action_bar.NonCurrentWeekActivity;
import com.alexandru.developer.aplicatie_studenti.view_pager.ViewPagerAdapter;

import java.util.HashMap;

/**
 * Created by Alexandru on 7/14/14.
 */
public class ResultsFragment extends Fragment {
    private AbsPres absPres;
    private Context context;
    public FragmentManager fm;
    public ResultsFragment(Context context, FragmentManager fm, String name, String type, String info) {
        this.context = context;
        this.fm = fm;
        absPres = getResults(name, type, info);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.results_fragment_layout, container, false);
        TableLayout tableLayout = (TableLayout) fragmentView.findViewById(R.id.table);
        ImageButton exitBtn = (ImageButton) fragmentView.findViewById(R.id.close_results_button);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.anim.anim_fragments, 100);
                DetailsFragment fr = new DetailsFragment(fm);
                fm.popBackStack();
            }
        });
        createTable(absPres, tableLayout);
        return fragmentView;
    }

    private AbsPres getResults(String name, String type, String info){
        if(name == null || type == null || info == null)
            return null;
        AbsPres result= new AbsPres();
        result.absences = result.presences = 0;
        int i;
        if(info.equals(ViewPagerAdapter.COURSES_IN_EVEN_WEEK)){
            for(i = 2; i <= MyActivity.WEEKS_IN_SEMESTER; i+=2){
                boolean wasPresent = false;
                if(context.getSharedPreferences(NonCurrentWeekActivity.PARTIAL_NAME_BACKUP_FILE + i,
                        Context.MODE_PRIVATE).getBoolean(name + "_" + type, false)){
                    result.presences ++;
                    wasPresent = true;
                }
                else
                    result.absences ++;


                result.table.put(i, wasPresent);
            }
        }else if(info.equals(ViewPagerAdapter.COURSES_IN_ODD_WEEK)){
            for(i = 1; i <= MyActivity.WEEKS_IN_SEMESTER; i+=2){
                boolean wasPresent = false;
                if(context.getSharedPreferences(NonCurrentWeekActivity.PARTIAL_NAME_BACKUP_FILE + i,
                        Context.MODE_PRIVATE).getBoolean(name + "_" + type, false)){
                    result.presences ++;
                    wasPresent = true;
                }
                else
                    result.absences ++;

                result.table.put(i, wasPresent);
            }
        }else{
            for(i = 1; i <= MyActivity.WEEKS_IN_SEMESTER; i++){
                boolean wasPresent = false;
                if(context.getSharedPreferences(NonCurrentWeekActivity.PARTIAL_NAME_BACKUP_FILE + i,
                        Context.MODE_PRIVATE).getBoolean(name + "_" + type, false)){
                    result.presences ++;
                    wasPresent= true;
                }
                else
                    result.absences ++;
                result.table.put(i, wasPresent);
            }
        }

        return result;
    }

    private TableLayout createTable(AbsPres absPres, TableLayout tableLayout){

        if(absPres == null)
            return tableLayout;

        int total = absPres.absences + absPres.presences;

        TableRow.LayoutParams tableRowTitleParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        tableRowTitleParams.weight = 1;
        tableRowTitleParams.span = 3;

        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        tableRowParams.weight = 1;

        TableRow tableRow = new TableRow(context);
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setPadding(1, 1, 1, 1);
        tableRow.setLayoutParams(tableRowTitleParams);

        TextView tv = new TextView(context);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(2, 2, 2, 2);
        tv.setText("Total: " + total);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        tableRow.addView( tv);
        tableLayout.addView(tableRow);

        tableRow = new TableRow(context);
        tableRow.setLayoutParams(tableRowParams);
        for(int i = 1; i <= 3; i++){
            tv = new TextView(context);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundColor(Color.WHITE);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            tv.setPadding(1, 1, 1, 1);
            tv.setBackgroundResource(R.drawable.cell_shape);
            switch (i){
                case 1:
                    break;
                case 2:
                    tv.setText(absPres.presences + " prezențe ");
                    break;
                case 3:
                    tv.setText(absPres.absences + " absențe");
                    break;
            }
            tableRow.addView(tv);
        }
        tableLayout.addView(tableRow);

        for(int i = 1; i <= MyActivity.WEEKS_IN_SEMESTER; i++){

            Boolean wasPresent = absPres.table.get(i);
            if(wasPresent != null){
                tableRow = new TableRow(context);
                tableRow.setLayoutParams(tableRowParams);
                tableRow.setPadding(1, 1, 1, 1);

                for(int j = 1; j <=3; j++){
                    tv = new TextView(context);
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    tv.setPadding(1, 1, 1, 1);
                    tv.setBackgroundResource(R.drawable.cell_shape);
                    switch (j){
                        case 1:
                            tv.setText("Săptămâna "+ i);
                            break;
                        case 2:
                            if( wasPresent )
                                tv.setText("X");
                            break;
                        case 3:
                            if(!wasPresent )
                                tv.setText("X");
                            break;
                    }
                    tableRow.addView(tv);
                }
                tableLayout.addView(tableRow);
            }
        }

        return tableLayout;
    }

    private class AbsPres{
        public int absences;
        public int presences;
        HashMap<Integer, Boolean> table = new HashMap<Integer, Boolean>();
    }


}
