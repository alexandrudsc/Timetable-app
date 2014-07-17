package com.alexandru.developer.aplicatie_studenti.view_pager;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alexandru.developer.aplicatie_studenti.R;

import java.util.ArrayList;

/**
 * Created by Alexandru on 6/13/14.
 */
public class DayFragment extends ListFragment {

    public final String TAG = this.getClass().getCanonicalName().toUpperCase();

    private String title;
    private int position;
    public MyListViewAdapter adapter;

    public static DayFragment createFragment(String title, int position){

        DayFragment dayFragment = new DayFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("position", position);
        dayFragment.setArguments(args);

        return dayFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null){
            title = args.getString("title");
            position = args.getInt("position");
            Log.d(TAG, "Fragment " + title + " created");
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter = (MyListViewAdapter) this.getListAdapter();
        if(adapter == null)
            adapter = ViewPagerAdapter.listViewAdapters[position];
        else{
            outState.putInt("position", position);
            outState.putParcelableArrayList("values", adapter.getValues());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            int position = savedInstanceState.getInt("position");
            ArrayList<Course> arrayList = savedInstanceState.getParcelableArrayList("values");
            adapter = new MyListViewAdapter(this.getActivity(), arrayList);
            if(ViewPagerAdapter.listViewAdapters == null)
                ViewPagerAdapter.listViewAdapters = new MyListViewAdapter[ViewPagerAdapter.NUM_DAYS];
            ViewPagerAdapter.listViewAdapters[position] = adapter;
            Log.d("FRAGMENT",""+adapter);
            this.setListAdapter(adapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.day_fragment_layout, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();

        Log.d("FRAGMENT", "adapter set " + title);
        //this.setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.widget_item_layout, R.id.event_name, values));
        try{
            this.setListAdapter(ViewPagerAdapter.listViewAdapters[position]);
            //this.getListView().setItemsCanFocus(false);
        }catch (NullPointerException e){
            e.printStackTrace();
        }


    }
}
