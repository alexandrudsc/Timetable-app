package com.alexandru.developer.aplicatie_studenti;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.alexandru.developer.aplicatie_studenti.view_pager.Course;

/**
 * Created by Alexandru on 7/14/14.
 */
public class DetailsFragment extends Fragment {

    private Course course;
    private Context context;
    private FragmentManager fm;

    public DetailsFragment(FragmentManager fm) {
        super();
        this.fm = fm;
    }


    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        course = args.getParcelable("course");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.details_fragment_layout, container, true);

        TextView courseTime = null, courseProf, courseLocation;
        final LayoutInflater inflaterForResult = inflater;
        final Context contextForResult = inflater.getContext();
        Button courseButton = (Button)fragmentView.findViewById(R.id.course_button);
        courseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.anim.anim_fragments, 0);
                ResultsFragment fr = new ResultsFragment(contextForResult,fm, course.name, course.type, course.info);
                ft.replace(R.id.course_fragment_container, fr, "replacement");
                ft.addToBackStack("replacement");
                ft.commit();
            }
        });
        try{

        context = inflater.getContext();
        courseProf = (TextView) fragmentView.findViewById(R.id.course_prof);
        courseTime = (TextView) fragmentView.findViewById(R.id.course_time);
        courseLocation = (TextView) fragmentView.findViewById(R.id.course_location);

        courseProf.setText(course.prof);
        courseTime.setText(course.time);
        courseLocation.setText(course.location);
        }
        catch (NullPointerException e ){
            courseTime.setText("Niciun rezultat");
        }
        return fragmentView;
    }

}
