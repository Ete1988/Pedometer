package com.mueller.mobileSports.general;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.user.DailyData;
import com.mueller.mobileSports.user.UserData;
import com.mueller.mobileSports.user.UserSessionManager;
import com.mueller.mobileSports.utility.MyXAxisValueFormatter;
import com.mueller.mobileSports.utility.XYMarkerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sandra on 8/10/2016.
 * A screen to display users statistics
 */
public class StatisticsActivity extends GenericActivity implements OnChartValueSelectedListener {

    private TextView fromDateView;
    private TextView toDateView;
    private int year, month, day;
    private Date fromDate, toDate;
    private Spinner spinner;
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_layout);
        init();
        setUpNavigation();
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.getMenu().findItem(R.id.StatisticsBtn).setChecked(true);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View childLayout = inflater.inflate(R.layout.statistics_view, (ViewGroup) findViewById(R.id.myStatisticsView));
        frameLayout.addView(childLayout);
        initializeViews();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    hideAndInvalidateCharts();
                } else if (position == 1) {
                    if (checkIfDurationIsEntered()) {
                        hideAndInvalidateCharts();
                        lineChart.setVisibility(View.VISIBLE);
                        setUpStepData();
                    } else
                        spinner.setSelection(0);

                } else if (position == 2) {
                    if (checkIfDurationIsEntered()) {
                        hideAndInvalidateCharts();
                        lineChart.setVisibility(View.VISIBLE);
                        setUpHeartRate();
                    } else
                        spinner.setSelection(0);
                } else if (position == 3) {
                    if (checkIfDurationIsEntered()) {
                        hideAndInvalidateCharts();
                        lineChart.setVisibility(View.VISIBLE);
                        setUpPerformance();
                    } else
                        spinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setFromDate(View view) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        if (view.getId() == R.id.fromDateBtn) {

            DatePickerDialog picker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    fromDate = calendar.getTime();
                    String input = dateFormat.format(fromDate);
                    fromDateView.setText(input);

                }
            }, year, month, day);
            picker.getDatePicker().setMaxDate(System.currentTimeMillis());
            picker.show();
        } else if (view.getId() == R.id.toDateBtn) {

            final DatePickerDialog picker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    toDate = calendar.getTime();
                    String input = dateFormat.format(toDate);
                    toDateView.setText(input);
                }
            }, year, month, day);
            picker.getDatePicker().setMaxDate(System.currentTimeMillis());
            picker.getDatePicker().setMinDate(fromDate.getTime());
            picker.show();
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    protected void setUpNavigation() {
        super.setUpNavigation();
    }

    @Override
    protected void init() {
        super.init();
    }

    /**
     * Initializes most fields of activity
     */
    private void initializeViews() {

        spinner = (Spinner) findViewById(R.id.spinner_activity);
        fromDateView = (TextView) findViewById(R.id.fromDateTextView);
        toDateView = (TextView) findViewById(R.id.toDateTextView);
        Calendar calendar = Calendar.getInstance();
        lineChart = (LineChart) findViewById(R.id.LineChart1);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        spinner.setSelection(0);
    }

    /**
     * Set up LineChart to display Fatigue, Fitness and Performance data
     */
    private void setUpPerformance() {
        UserData userData = UserSessionManager.getUserData();
        ArrayList<DailyData> dailyData = userData.getDailyData();
        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<Entry> valuesFatigue = new ArrayList<>();
        ArrayList<Entry> valuesFitness = new ArrayList<>();
        ArrayList<Entry> valuesPerformance = new ArrayList<>();
        int i = 0;
        //Read saved data and extract TRIMP scores for specified time period
        for (DailyData data : dailyData) {
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            try {
                Date date = format.parse(data.getSessionDay());
                if (!date.after(toDate) && !date.before(fromDate)) {
                    valuesFatigue.add(new Entry(i, data.getTrainingSessionData().getFatigue()));
                    valuesFitness.add(new Entry(i, data.getTrainingSessionData().getFitness()));
                    valuesPerformance.add(new Entry(i, data.getTrainingSessionData().getPerformance()));
                    xValues.add(i++, data.getSessionDay());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //DataSets
        LineDataSet lineDataSet = new LineDataSet(valuesPerformance, "Performance");
        lineDataSet.setColor(Color.YELLOW);
        lineDataSet.setCircleColor(Color.YELLOW);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(9f);
        lineDataSet.setFillColor(Color.YELLOW);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);

        LineDataSet lineDataSet1 = new LineDataSet(valuesFitness, "Fitness");
        lineDataSet1.setColor(Color.GREEN);
        lineDataSet1.setCircleColor(Color.GREEN);
        lineDataSet1.setLineWidth(1f);
        lineDataSet1.setCircleRadius(3f);
        lineDataSet1.setDrawCircleHole(false);
        lineDataSet1.setValueTextSize(9f);
        lineDataSet1.setFillColor(Color.GREEN);
        lineDataSet1.setDrawFilled(true);
        lineDataSet1.setFormLineWidth(1f);
        lineDataSet1.setFormSize(15.f);

        LineDataSet lineDataSet2 = new LineDataSet(valuesFatigue, "Fatigue");
        lineDataSet2.setColor(Color.RED);
        lineDataSet2.setCircleColor(Color.RED);
        lineDataSet2.setLineWidth(1f);
        lineDataSet2.setCircleRadius(3f);
        lineDataSet2.setDrawCircleHole(false);
        lineDataSet2.setValueTextSize(9f);
        lineDataSet2.setFillColor(Color.RED);
        lineDataSet2.setDrawFilled(true);
        lineDataSet2.setFormLineWidth(1f);
        lineDataSet2.setFormSize(15.f);

        //xAxis Steps
        XAxis axis = lineChart.getXAxis();
        axis.setValueFormatter(new MyXAxisValueFormatter(xValues));
        axis.setGranularity(1f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);

        //Marker
        XYMarkerView mv = new XYMarkerView(this, new MyXAxisValueFormatter(xValues));

        lineChart.setData(new LineData(dataSets));
        lineChart.animateXY(2000, 2000);
        lineChart.setOnChartValueSelectedListener(this);
        lineChart.setMarker(mv);
        lineChart.setDescription(null);
        lineChart.setMaxVisibleValueCount(300);
        lineChart.invalidate();
    }

    /**
     * Set up LineChart to display Step Data
     */
    private void setUpStepData() {
        UserData userData = UserSessionManager.getUserData();
        ArrayList<DailyData> dailyData = userData.getDailyData();
        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<Entry> valueSetSteps = new ArrayList<>();
        int i = 0;
        //Read saved data and extract TRIMP scores for specified time period
        for (DailyData data : dailyData) {
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            try {
                Date date = format.parse(data.getSessionDay());
                if (!date.after(toDate) && !date.before(fromDate)) {
                    valueSetSteps.add(new Entry(i, data.getPedometerData().getDailyStepCount()));
                    xValues.add(i++, data.getSessionDay());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //DataSets
        LineDataSet lineDataSet = new LineDataSet(valueSetSteps, "Steps");
        lineDataSet.setColor(Color.BLACK);
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(9f);
        lineDataSet.setFillColor(Color.BLUE);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);

        //xAxis Steps
        XAxis axis = lineChart.getXAxis();
        axis.setValueFormatter(new MyXAxisValueFormatter(xValues));
        axis.setGranularity(1f);


        //Marker
        XYMarkerView mv = new XYMarkerView(this, new MyXAxisValueFormatter(xValues));

        lineChart.setData(new LineData(lineDataSet));
        lineChart.animateXY(2000, 2000);
        lineChart.setOnChartValueSelectedListener(this);
        lineChart.setMarker(mv);
        lineChart.setDescription(null);
        lineChart.setMaxVisibleValueCount(300);
        lineChart.invalidate();

    }

    /**
     * Set up LineChart to display Heart Rate Data
     */
    private void setUpHeartRate() {
        UserData userData = UserSessionManager.getUserData();
        ArrayList<DailyData> dailyData = userData.getDailyData();
        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<Entry> valuesHeartRate = new ArrayList<>();
        ArrayList<Entry> valuesMinHeartRate = new ArrayList<>();
        ArrayList<Entry> valuesMaxHeartRate = new ArrayList<>();
        int i = 0;
        //Read saved data and extract TRIMP scores for specified time period
        for (DailyData data : dailyData) {
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            try {
                Date date = format.parse(data.getSessionDay());
                if (!date.after(toDate) && !date.before(fromDate)) {
                    valuesHeartRate.add(new Entry(i, data.getHeartRateData().getAverageHeartRate()));
                    valuesMinHeartRate.add(new Entry(i, data.getHeartRateData().getMinHeartRate()));
                    valuesMaxHeartRate.add(new Entry(i, data.getHeartRateData().getMaxHeartRate()));
                    xValues.add(i++, data.getSessionDay());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //DataSets
        LineDataSet lineDataSet = new LineDataSet(valuesHeartRate, "HR");
        lineDataSet.setColor(Color.YELLOW);
        lineDataSet.setCircleColor(Color.YELLOW);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(9f);
        lineDataSet.setFillColor(Color.YELLOW);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);

        LineDataSet lineDataSet1 = new LineDataSet(valuesMinHeartRate, "MinHR");
        lineDataSet1.setColor(Color.GREEN);
        lineDataSet1.setCircleColor(Color.GREEN);
        lineDataSet1.setLineWidth(1f);
        lineDataSet1.setCircleRadius(3f);
        lineDataSet1.setDrawCircleHole(false);
        lineDataSet1.setValueTextSize(9f);
        lineDataSet1.setFillColor(Color.GREEN);
        lineDataSet1.setDrawFilled(true);
        lineDataSet1.setFormLineWidth(1f);
        lineDataSet1.setFormSize(15.f);

        LineDataSet lineDataSet2 = new LineDataSet(valuesMaxHeartRate, "MaxHR");
        lineDataSet2.setColor(Color.RED);
        lineDataSet2.setCircleColor(Color.RED);
        lineDataSet2.setLineWidth(1f);
        lineDataSet2.setCircleRadius(3f);
        lineDataSet2.setDrawCircleHole(false);
        lineDataSet2.setValueTextSize(9f);
        lineDataSet2.setFillColor(Color.RED);
        lineDataSet2.setDrawFilled(true);
        lineDataSet2.setFormLineWidth(1f);
        lineDataSet2.setFormSize(15.f);

        //xAxis Steps
        XAxis axis = lineChart.getXAxis();
        axis.setValueFormatter(new MyXAxisValueFormatter(xValues));
        axis.setGranularity(1f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);
        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);

        //Marker
        XYMarkerView mv = new XYMarkerView(this, new MyXAxisValueFormatter(xValues));

        lineChart.setData(new LineData(dataSets));
        lineChart.animateXY(2000, 2000);
        lineChart.setOnChartValueSelectedListener(this);
        lineChart.setMarker(mv);
        lineChart.setDescription(null);
        lineChart.setMaxVisibleValueCount(300);
        lineChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    private void hideAndInvalidateCharts() {
        lineChart.invalidate();
        lineChart.setVisibility(View.GONE);
    }

    private boolean checkIfDurationIsEntered() {
        if (fromDate == null || toDate == null) {
            Toast.makeText(this, "Please set the time period!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}



