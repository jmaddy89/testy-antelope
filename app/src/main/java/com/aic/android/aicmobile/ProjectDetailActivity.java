package com.aic.android.aicmobile;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.aic.android.aicmobile.backend.aicDataAPI.model.Projects;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ProjectDetailActivity extends AppCompatActivity {

    private static final String PROJ_NUMBER = "0";
    private static final String PROJ_CUSTOMER = "AIC";
    private static final String PROJ_DESCRIPTION = "Mobile";

    private Projects mProject = new Projects();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProject.setProjectNum(Integer.parseInt(getIntent().getExtras().getString(PROJ_NUMBER)));
        mProject.setCustomer(getIntent().getExtras().getString(PROJ_CUSTOMER));
        mProject.setProjectDesc(getIntent().getExtras().getString(PROJ_DESCRIPTION));

        setContentView(R.layout.activity_project_detail);
        setTitle(mProject.getProjectDesc());

        // Initializing tab and pager views
        TabLayout tabLayout = (TabLayout) findViewById(R.id.project_detail_tab_layout);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.project_detail_view_pager);

        // Making new tabs and adding to tabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Overview"));
        tabLayout.addTab(tabLayout.newTab().setText("Chat"));
        tabLayout.addTab(tabLayout.newTab().setText("Expenses"));
        tabLayout.addTab(tabLayout.newTab().setText("Change Orders"));
        tabLayout.addTab(tabLayout.newTab().setText("Time Entries"));

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        Bundle bundle = new Bundle();
        bundle.putSerializable(PROJ_NUMBER, mProject.getProjectNum());

        // Adding fragments to a list
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, ProjectDetailOverviewFragment.class.getName(), bundle));
        fragments.add(Fragment.instantiate(this, ProjectDetailChatFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, ProjectDetailExpensesFragment.class.getName(), bundle));
        fragments.add(Fragment.instantiate(this, ProjectDetailChangeOrderFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, ProjectDetailTimeFragment.class.getName()));

        // Attaching fragments into tabLayout with ViewPager
        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager(), fragments));
        tabLayout.setupWithViewPager(viewPager);


    }

    public static void updateUI() {

    }
}
