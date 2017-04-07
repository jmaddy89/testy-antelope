package com.aic.android.aicmobile;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.aic.android.aicmobile.backend.aicDataAPI.model.Projects;

import java.util.ArrayList;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(mProject.getProjectDesc());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    public static void updateUI() {

    }
}
