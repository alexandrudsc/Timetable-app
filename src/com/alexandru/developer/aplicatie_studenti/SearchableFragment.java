package com.alexandru.developer.aplicatie_studenti;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alexandru.developer.aplicatie_studenti.view_pager.Course;

/**
 * Created by Alexandru on 7/14/14.
 */
public class SearchableFragment extends  Fragment{
    private Course course;
    private DetailsFragment detailsFragment;

    //private Button courseButton;
    private FrameLayout courseFragContainer;
    private FragmentManager fm;

    Bundle detailsFragmentArgs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public SearchableFragment(Course course, FragmentManager fm) {
        super();
        this.course = course;
        this.fm = fm;

        detailsFragmentArgs = new Bundle();
        detailsFragmentArgs.putParcelable("course", course);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView =  inflater.inflate(R.layout.searchable_fragment_layout, container, true);
        TextView courseTitle = (TextView) fragmentView.findViewById(R.id.course_title);


        courseFragContainer = (FrameLayout) fragmentView.findViewById(R.id.course_fragment_container);

        detailsFragment = new DetailsFragment(fm);
        detailsFragment.setArguments(detailsFragmentArgs);
        detailsFragment.onCreateView(inflater, courseFragContainer, savedInstanceState);

        if(course.fullName == null)
            courseTitle.setText(course.name + " " + course.type );
        else
            courseTitle.setText(course.fullName+ " " + course.type);

        return fragmentView;
    }

}
