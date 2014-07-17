package com.alexandru.developer.aplicatie_studenti;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alexandru.developer.aplicatie_studenti.storage.DataLoader;
import com.alexandru.developer.aplicatie_studenti.view_pager.MyListViewAdapter;
import com.alexandru.developer.aplicatie_studenti.view_pager.PagerSlidingTabStrip;
import com.alexandru.developer.aplicatie_studenti.view_pager.ViewPagerAdapter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by Alexandru on 7/12/14.
 */
public class TimetableFragment extends Fragment {
    public ViewPager viewPager;
    public PagerSlidingTabStrip pagerSlidingTabStrip;
    private ActionBarDrawerToggle drawerToggle;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Timetable", "create frag");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.timetable_frag_layout, container, false);

        viewPager = (ViewPager) fragmentView.findViewById(R.id.view_pager);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) fragmentView.findViewById(R.id.sliding_tabs);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
            MyActivity.setCurrentWeek(getActivity());
            //initializeActionBar();
        }

        //initializeNavDrawer();

    }


    @Override
    public void onPause() {
        super.onPause();
        final int lastPosition = viewPager.getCurrentItem();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MyActivity.PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MyActivity.VP_LAST_POSITION, lastPosition);
        editor.commit();
    }

    /*@Override
    public void onDestroy() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MyActivity.PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(MyActivity.VP_LAST_POSITION);
        editor.commit();
        super.onDestroy();

    }*/

    private void loadJSONFileAndSetData(){
        URL[] urls = new URL[1];
        try {
            urls[0] = new URL("https://docs.google.com/uc?authuser=0&id=0B81UjA_fISK7QWNhdEFxS3Rqanc&export=download");
            new DataLoader((MyActivity) getActivity(), null, null, null).execute(urls);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void setDataFromLoadedFile(){
        if(viewPager.getAdapter() == null){

            viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), ViewPagerAdapter.context));
            pagerSlidingTabStrip.setViewPager(viewPager);
        }
    }

    public int retrieveLastPosition(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MyActivity.PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE);
        return sharedPreferences.getInt(MyActivity.VP_LAST_POSITION, -1);
    }

}
