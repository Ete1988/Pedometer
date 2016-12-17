package com.mueller.mobileSports.general;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mueller.mobileSports.account.SessionManager;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.user.UserProfileData;

import java.util.Locale;

/**
 * Created by Ete on 11/10/2016.
 */

public class SettingsActivity extends BottomBarButtonManager {

    private int physicalActivityLevel, StepGoal;
    private TextView mActivityLevelText, mCurrentStepGoalText;
    private UserProfileData myData;
    private SessionManager sessionManager;
    private String[] goalsValuesArray = {"5000", "6000", "7000", "8000", "9000", "10000", "Other? Please set here!"};

    private String[] activityLevelTextArray = {"0: Avoid walking or exertion, for example, always use elevator, drive " +
            "whenever possible instead of walking",
            "1: Walk for pleasure, routinely use stairs, occasionally exercise " +
                    "sufficiently to cause heavy breathing or perspiration",
            "2: 10 to 60 minutes per week",
            "3: Over 60 minutes per week",
            "4: run less than 1 mile (1.6 km) per week or spend less than 30 minutes " +
                    "per week in comparable physical activity",
            "5: run 1 to 5 mile (1.6 to 8 km) per week or spend 30 to 60 minutes per " +
                    "week in comparable physical activity",
            "6: run 5 to 10 mile (8 to 16 km) per week or spend 1 hour to 3 hours per " +
                    "week in comparable physical activity",
            "7: run over 10 miles (16 km) per week or spend over 3 hours per week " +
                    "in comparable physical activity"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        sessionManager = new SessionManager(this);
        mActivityLevelText = (TextView) findViewById(R.id.level);
        mCurrentStepGoalText = (TextView) findViewById(R.id.stepGoalView);
        myData = new UserProfileData();
        physicalActivityLevel = myData.getActivityLevel();
        StepGoal = myData.getStepGoal();
        mappingWidgets();

        setLevel();
        setGoal();

        setSupportActionBar(myToolbar);
        setTitle("Settings");
    }

    @Override
    protected void mappingWidgets() {
        super.mappingWidgets();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    public void setActivityLevelDialog(View v) {

        AlertDialog.Builder activityLevelDialog = new AlertDialog.Builder(this);
        activityLevelDialog.setTitle("Select your activity mActivityLevelText");
        activityLevelDialog.setSingleChoiceItems(activityLevelTextArray, -1, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                physicalActivityLevel = item;
                setLevel();
                dialog.dismiss();

            }
        });
        AlertDialog alert = activityLevelDialog.create();
        alert.show();
    }

    public void setGoalDialog(View v) {

        AlertDialog.Builder activityLevelDialog = new AlertDialog.Builder(this);
        activityLevelDialog.setTitle("Set your StepGoal for today");
        activityLevelDialog.setSingleChoiceItems(goalsValuesArray, -1, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                StepGoal = item;
                setGoal();
                dialog.dismiss();

            }
        });
        AlertDialog alert = activityLevelDialog.create();
        alert.show();

    }

    public void editGoal() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Please enter daily Goal");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int positiveButton) {
                String foo = input.getText().toString();
                StepGoal = Integer.parseInt(foo);
                mCurrentStepGoalText.setText(input.getText());
                myData.setStepGoal(StepGoal);
                dialog.dismiss();

            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int negativeButton) {
                //Put actions for CANCEL button here, or leave in blank
                mCurrentStepGoalText.setText(String.format(Locale.getDefault(), "%d", myData.getStepGoal()));
                dialog.dismiss();

            }
        });
        alert.show();

    }

    private void setLevel() {

        switch (physicalActivityLevel) {
            case 0:
                mActivityLevelText.setText("0");
                break;
            case 1:
                mActivityLevelText.setText("1");
                break;
            case 2:
                mActivityLevelText.setText("2");
                break;
            case 3:
                mActivityLevelText.setText("3");
                break;
            case 4:
                mActivityLevelText.setText("4");
                break;
            case 5:
                mActivityLevelText.setText("5");
                break;
            case 6:
                mActivityLevelText.setText("6");
                break;
            case 7:
                mActivityLevelText.setText("7");
                break;
            default:
                mActivityLevelText.setText(" ");
                break;
        }

        myData.setActivityLevel(physicalActivityLevel);
    }

    private void setGoal() {

        switch (StepGoal) {

            case 0:
                StepGoal = 5000;
                mCurrentStepGoalText.setText(R.string.fiveTH);
                break;
            case 1:
                StepGoal = 6000;
                mCurrentStepGoalText.setText(R.string.sixTH);
                break;
            case 2:
                StepGoal = 7000;
                mCurrentStepGoalText.setText(R.string.sevenTH);
                break;
            case 3:
                StepGoal = 8000;
                mCurrentStepGoalText.setText(R.string.eigthTH);
                break;
            case 4:
                StepGoal = 9000;
                mCurrentStepGoalText.setText(R.string.itsOverNineTHousand);
                break;
            case 5:
                StepGoal = 10000;
                mCurrentStepGoalText.setText(R.string.tenTH);
                break;
            case 6:
                editGoal();
                break;
            default:
                mCurrentStepGoalText.setText(String.format(Locale.getDefault(), "%d", myData.getStepGoal()));
                break;
        }
        myData.setStepGoal(StepGoal);
    }


    @Override
    protected void onResume() {
        super.onResume();
        sessionManager.checkLogin();
    }
}


