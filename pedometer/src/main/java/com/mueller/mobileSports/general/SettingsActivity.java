package com.mueller.mobileSports.general;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mueller.mobileSports.heartRate.HeartRateActivity;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.PedometerActivity;
import com.mueller.mobileSports.user.ProfileActivity;
import com.mueller.mobileSports.user.SessionManager;

import java.util.Locale;

/**
 * Created by Ete on 11/10/2016.
 * <p>
 * Activity that offers methods to adjust app relevant data.
 */

public class SettingsActivity extends GenericActivity {

    private final String[] goalsValuesArray = {"5000", "6000", "7000", "8000", "9000", "10000", "Other? Please set here!"};
    private final String[] activityLevelTextArray = {"0: Avoid walking or exertion, for example, always use elevator, drive " +
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
    private int physicalActivityLevel, stepGoal;
    private TextView mActivityLevelText, mCurrentStepGoalText;
    private SessionManager sessionManager;
    private SharedValues sharedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        setTitle("Settings");
    }

    private void init() {
        sharedValues = SharedValues.getInstance(this);
        sessionManager = new SessionManager(this);
        mappingWidgets();
        mActivityLevelText = (TextView) findViewById(R.id.level);
        mCurrentStepGoalText = (TextView) findViewById(R.id.stepGoalView);
        physicalActivityLevel = sharedValues.getInt("physicalActivityLevel");
        stepGoal = sharedValues.getInt("stepGoal");
        setLevel();
        setGoal();

    }

    @Override
    protected void mappingWidgets() {
        super.mappingWidgets();
    }

    @Override
    public void onClick(View v) {
        if (v == null)
            throw new NullPointerException(
                    "You are refering null object. "
                            + "Please check weather you had called super class method mappingWidgets() or not");
        if (v.getId() == R.id.PedometerBtn) {
            updateData(new Intent(this, PedometerActivity.class));
        } else if (v.getId() == R.id.ProfileBtn) {
            updateData(new Intent(this, ProfileActivity.class));
        } else if (v.getId() == R.id.HeartRateBtn) {
            updateData(new Intent(this, HeartRateActivity.class));
        }
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
        activityLevelDialog.setTitle("Set your stepGoal for today");
        activityLevelDialog.setSingleChoiceItems(goalsValuesArray, -1, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                stepGoal = item;
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
                stepGoal = Integer.parseInt(foo);
                mCurrentStepGoalText.setText(input.getText());
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int negativeButton) {
                //Put actions for CANCEL button here, or leave in blank
                mCurrentStepGoalText.setText(String.format(Locale.getDefault(), "%d", sharedValues.getInt("stepGoal")));
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
        }
    }

    private void setGoal() {
        switch (stepGoal) {
            case 0:
                stepGoal = 5000;
                mCurrentStepGoalText.setText(R.string.fiveTH);
                break;
            case 1:
                stepGoal = 6000;
                mCurrentStepGoalText.setText(R.string.sixTH);
                break;
            case 2:
                stepGoal = 7000;
                mCurrentStepGoalText.setText(R.string.sevenTH);
                break;
            case 3:
                stepGoal = 8000;
                mCurrentStepGoalText.setText(R.string.eigthTH);
                break;
            case 4:
                stepGoal = 9000;
                mCurrentStepGoalText.setText(R.string.itsOverNineTHousand);
                break;
            case 5:
                stepGoal = 10000;
                mCurrentStepGoalText.setText(R.string.tenTH);
                break;
            case 6:
                editGoal();
                break;
            default:
                mCurrentStepGoalText.setText(String.format(Locale.getDefault(), "%d", sharedValues.getInt("stepGoal")));
                break;
        }
    }

    public void updateData(Intent intent) {
        sharedValues.saveInt("stepGoal", stepGoal);
        sharedValues.saveInt("physicalActivityLevel", physicalActivityLevel);
        sessionManager.uploadUserData(this, intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionManager.isLoginValid();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateData(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}


