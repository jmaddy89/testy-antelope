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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}
