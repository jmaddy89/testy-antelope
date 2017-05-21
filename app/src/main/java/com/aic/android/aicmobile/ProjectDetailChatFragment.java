package com.aic.android.aicmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by JLM on 5/19/2017.
 */

public class ProjectDetailChatFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Declare your first fragment here
        return inflater.inflate(R.layout.fragment_project_detail_chat, container, false);
    }
}
