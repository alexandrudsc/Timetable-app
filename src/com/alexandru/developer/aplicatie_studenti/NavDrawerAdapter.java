package com.alexandru.developer.aplicatie_studenti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

/**
 * Created by Alexandru on 7/1/14.
 */
public class NavDrawerAdapter extends BaseAdapter{
    private final int NAV_DRAWER_ELEM_COUNT = 5;
    private String[] titles;
    private Context context;

    public NavDrawerAdapter(Context context) {
        titles = context.getResources().getStringArray(R.array.drawer_list_elements);
        this.context = context;
    }

    @Override
    public int getCount() {
        return NAV_DRAWER_ELEM_COUNT;
    }

    @Override
    public Object getItem(int position) {
        return titles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                convertView = inflater.inflate(R.layout.nav_drawer_elem, parent, false);
        }

        TextView tv = (TextView)convertView.findViewById(R.id.nav_elem_tv);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.nav_elem_img);
        switch (position){
            case 0:
                imageView.setImageResource(android.R.drawable.ic_menu_week);
                break;
            case 1:
                imageView.setImageResource(android.R.drawable.ic_menu_agenda);
                break;
            case 2:
                imageView.setImageResource(android.R.drawable.ic_menu_myplaces);
                break;
            case 3:
                imageView.setImageResource(android.R.drawable.ic_menu_help);
                break;
            case 4:
                imageView.setImageResource(android.R.drawable.ic_menu_info_details);
        }
        tv.setText(titles[position]);

        return convertView;
    }
}
