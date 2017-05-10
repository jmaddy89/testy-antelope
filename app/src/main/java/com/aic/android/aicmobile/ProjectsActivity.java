package com.aic.android.aicmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by jorda on 3/28/2017.
 */

public class ProjectsActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ProjectsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
    }



}
