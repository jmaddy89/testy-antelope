package com.aic.android.aicmobile;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aic.android.aicmobile.backend.aicDataAPI.model.ProjectBreakdown;
import com.aic.android.aicmobile.backend.aicDataAPI.AicDataAPI;
import com.aic.android.aicmobile.backend.aicDataAPI.model.Projects;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JLM on 5/19/2017.
 */

public class ProjectDetailOverviewFragment extends Fragment {

    private static final String APP_URL = "https://aic-mobile-5fdf1.appspot.com/_ah/api/";
    private static final String PROJ_NUMBER = "0";



    private PieChart mBreakdownChart;
    private ProjectBreakdown mBreakdownInfo;
    private Projects mProjects = new Projects();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_project_detail_overview, container, false);

        mProjects.setProjectNum((int) getArguments().getSerializable(PROJ_NUMBER));

        mBreakdownChart = (PieChart) v.findViewById(R.id.project_detail_overview_breakdown_chart);

        new getProjectBreakdown().execute(mProjects.getProjectNum());

        return v;
    }

    private void setData() {



        mBreakdownChart.setUsePercentValues(true);
        mBreakdownChart.getDescription().setEnabled(false);
        mBreakdownChart.setExtraOffsets(5, 10, 5, 5);

        mBreakdownChart.setDragDecelerationFrictionCoef(0.95f);

//        mChart.setCenterTextTypeface(mTfLight);
//        mChart.setCenterText(generateCenterSpannableText());

        mBreakdownChart.setDrawHoleEnabled(true);
        mBreakdownChart.setHoleColor(Color.WHITE);

        mBreakdownChart.setTransparentCircleColor(Color.WHITE);
        mBreakdownChart.setTransparentCircleAlpha(110);

        mBreakdownChart.setHoleRadius(58f);
        mBreakdownChart.setTransparentCircleRadius(61f);

        mBreakdownChart.setDrawCenterText(true);

        mBreakdownChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mBreakdownChart.setRotationEnabled(true);
        mBreakdownChart.setHighlightPerTapEnabled(true);

        mBreakdownChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        mBreakdownChart.setEntryLabelColor(Color.BLACK);


        List<PieEntry> entries = new ArrayList<>();

        if (mBreakdownInfo.getLaborBudget() > 0) {
            entries.add(new PieEntry(mBreakdownInfo.getLaborBudget(), "Labor"));
        }
        if (mBreakdownInfo.getMaterialBudget() > 0) {
            entries.add(new PieEntry(mBreakdownInfo.getMaterialBudget(), "Materials"));
        }
        if (mBreakdownInfo.getSubcontractorBudget() > 0) {
            entries.add(new PieEntry(mBreakdownInfo.getSubcontractorBudget(), "Subcontractors"));
        }
        if (mBreakdownInfo.getContingencyBudget() > 0) {
            entries.add(new PieEntry(mBreakdownInfo.getContingencyBudget(), "Contingency"));
        }

        PieDataSet set = new PieDataSet(entries, "");
        PieData data = new PieData(set);
        mBreakdownChart.setData(data);
        mBreakdownChart.invalidate(); // refresh

//        PieDataSet dataSet = new PieDataSet(entries, "Election Results");

        set.setDrawIcons(false);

        set.setSliceSpace(3f);
        set.setIconsOffset(new MPPointF(0, 40));
        set.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
//
//        for (int c : ColorTemplate.PASTEL_COLORS)
//            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        set.setColors(colors);
        //dataSet.setSelectionShift(0f);

//        PieData data = new PieData(set);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);


        Legend l = mBreakdownChart.getLegend();
        l.setEnabled(false);
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l.setWordWrapEnabled(true);
//        l.setDrawInside(false);
//        l.setXEntrySpace(7f);
//        l.setYEntrySpace(0f);
//        l.setYOffset(0f);


    }

    /*
Asychrounous task to download project list
 */
    private class getProjectBreakdown extends AsyncTask<Integer, Void, ProjectBreakdown> {
        private AicDataAPI myApiService = null;
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading project data");
            this.dialog.show();
        }

        @Override
        protected ProjectBreakdown doInBackground(Integer... params) {
            if (myApiService == null) {
                AicDataAPI.Builder builder = new AicDataAPI.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        //Root url of google cloud project
                        .setRootUrl(APP_URL);
                myApiService = builder.build();
            }


            try {
                return myApiService.getProjectBreakdown(params[0]).execute();
            } catch (IOException e) {
                return new ProjectBreakdown();
            }
        }

        @Override
        protected void onPostExecute(ProjectBreakdown s) {

            mBreakdownInfo = s;
            setData();

            // Dismiss loading dialog
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }
    }
}
