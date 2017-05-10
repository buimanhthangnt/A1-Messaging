package com.etspteam.a1_messaging.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etspteam.a1_messaging.R;

public class IncogtiveChatFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_incogtive_chat, container, false);
        ((MainActivity) getActivity()).setToolbarAndStatusBarColor(R.color.incogtive, R.color.incogtive_dark);
        return v;
    }

}
