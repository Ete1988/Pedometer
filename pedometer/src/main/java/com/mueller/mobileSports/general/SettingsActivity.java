package com.mueller.mobileSports.general;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.user.SessionManager;

import java.util.Locale;

/**
 * Created by Ete on 11/10/2016.
 * <p>
 * Activity that offers methods to adjust app relevant data.
 */

public class SettingsActivity extends AppCompatActivity {

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
    private int physicalActivityLevel, stepGoal, restingHeartRate, heartRateMax;
    private TextView mActivityLevelText, mCurrentStepGoalText, mRestingHeartRate, mHeartRateMax;
    private SessionManager sessionManager;
    private SharedValues sharedValues;
    private Button mSaveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");
        init();
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
    }

    private void init() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mSaveBtn = (Button) findViewById(R.id.SE_SaveChangesBtn);
        sharedValues = SharedValues.getInstance(this);
        sessionManager = new SessionManager(this);
        mActivityLevelText = (TextView) findViewById(R.id.SE_TextActivityLevelView);
        mCurrentStepGoalText = (TextView) findViewById(R.id.SE_TextStepGoalView);
        mRestingHeartRate = (TextView) findViewById(R.id.SE_TextRestingHeartRateView);
        mHeartRateMax = (TextView) findViewById(R.id.SE_TextHeartRateMaxView);
        physicalActivityLevel = sharedValues.getInt("physicalActivityLevel");
        mActivityLevelText.setText(String.format(Locale.getDefault(), "%d", physicalActivityLevel));
        stepGoal = sharedValues.getInt("stepGoal");
        mCurrentStepGoalText.setText(String.format(Locale.getDefault(), "%d", stepGoal));

        if (!(sharedValues.getInt("restingHeartRate") == 0)) {
            restingHeartRate = sharedValues.getInt("restingHeartRate");
        } else {
            restingHeartRate = 60;
        }

        if (!(sharedValues.getInt("heartRateMax") == 0)) {
            heartRateMax = sharedValues.getInt("heartRateMax");
        } else if (!(sharedValues.getInt("age") == 0)) {
            heartRateMax = (int) (208 - (0.7 * (sharedValues.getInt("age"))));
        } else {
            heartRateMax = 0;
            mHeartRateMax.setError("Please set your age in the profile tab, or set HRmax manually");
        }
        mRestingHeartRate.setText(String.format(Locale.getDefault(), "%d", restingHeartRate));
        mHeartRateMax.setText(String.format(Locale.getDefault(), "%d", heartRateMax));
    }

    public void onClickSettingsActivity(View v) {
        if (v == null) {
            throw new NullPointerException(
                    "You are referring null object. "
                            + "Please check weather you had called super class method mappingWidgets() or not");
        } else if (v.getId() == R.id.SE_GoalSelector) {
            setGoalDialog();
        } else if (v.getId() == R.id.SE_HeartRateMaxSelector) {
            numberPickerDialog(120, 220, mHeartRateMax, "Set Maximum Heart Rate");
        } else if (v.getId() == R.id.SE_LevelSelector) {
            setActivityLevelDialog();
        } else if (v.getId() == R.id.SE_RestingHeartRateSelector) {
            numberPickerDialog(40, 130, mRestingHeartRate, "Set Resting Heart Rate");
        } else if (v.getId() == R.id.SE_SaveChangesBtn) {
            updateData();
        }
    }

    public void setActivityLevelDialog() {
        AlertDialog.Builder activityLevelDialog = new AlertDialog.Builder(this);
        activityLevelDialog.setTitle("Select your activity mActivityLevelText");
        activityLevelDialog.setSingleChoiceItems(activityLevelTextArray, -1, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                physicalActivityLevel = item;
                mActivityLevelText.setText(String.format(Locale.getDefault(), "%d", physicalActivityLevel));
                dialog.dismiss();
            }
        });
        AlertDialog alert = activityLevelDialog.create();
        alert.show();
    }

    public void setGoalDialog() {
        AlertDialog.Builder activityLevelDialog = new AlertDialog.Builder(this);
        activityLevelDialog.setTitle("Set your stepGoal for today");
        activityLevelDialog.setSingleChoiceItems(goalsValuesArray, -1, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                mapSelectedItemToGoal(item);
                dialog.dismiss();
            }
        });
        AlertDialog alert = activityLevelDialog.create();
        alert.show();

    }

    private void mapSelectedItemToGoal(int item) {
        switch (item) {
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

        }
    }

    private void editGoal() {
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

    public void updateData() {
        sharedValues.saveInt("stepGoal", stepGoal);
        sharedValues.saveInt("physicalActivityLevel", physicalActivityLevel);
        sharedValues.saveInt("restingHeartRate", restingHeartRate);
        sharedValues.saveInt("heartRateMax", heartRateMax);
        sessionManager.uploadUserData(this, true, false);
    }

    @Override
    protected void onResume() {
        sessionManager.checkUserState();
        super.onResume();
    }

    private void numberPickerDialog(int min, int max, final TextView textView, final String title) {
        NumberPicker myNumberPicker = new NumberPicker(this);
        myNumberPicker.setMinValue(min);
        myNumberPicker.setMaxValue(max);
        NumberPicker.OnValueChangeListener myValChangeListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                textView.setText(String.format(Locale.getDefault(), "%d", newVal));
                System.out.println(textView.getText().toString());
            }
        };

        myNumberPicker.setOnValueChangedListener(myValChangeListener);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(myNumberPicker);
        builder.setTitle(title);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (textView.getId() == mRestingHeartRate.getId()) {
                    restingHeartRate = Integer.parseInt(mRestingHeartRate.getText().toString());
                    sharedValues.saveInt("restingHeartRate", restingHeartRate);
                } else if (textView.getId() == mHeartRateMax.getId()) {
                    heartRateMax = Integer.parseInt(mHeartRateMax.getText().toString());
                    sharedValues.saveInt("heartRateMax", heartRateMax);
                }
            }
        });
        builder.show();
    }
}


