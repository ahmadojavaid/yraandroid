package com.app.yourrestaurantapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.yourrestaurantapp.R;

public class FragmentInfoProfile extends Fragment {

    TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        textView = (TextView) rootView.findViewById(R.id.textView);
        textView.setText(Html.fromHtml(getResources().getString(R.string.profile)));

        return rootView;
    }

}