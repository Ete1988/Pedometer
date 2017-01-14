package com.mueller.mobileSports.general;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.mueller.mobileSports.heartRate.GenericActivity;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.user.UserData;
import com.mueller.mobileSports.user.UserSessionManager;

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
    private int activityLevel, stepGoal, restingHeartRate, heartRateMax;
    private TextView mActivityLevelText, mCurrentStepGoalText, mRestingHeartRate, mHeartRateMax;
    private UserSessionManager userSessionManager;
    private Button mSaveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_layout);
        init();
        setUpNavigation();

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View childLayout = inflater.inflate(R.layout.settings_view,
                (ViewGroup) findViewById(R.id.mySettingsView));
        frameLayout.addView(childLayout);
        initializeViews();

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void setUpNavigation() {
        super.setUpNavigation();
    }

    /**
     * Initializes most fields of activity
     */
    private void initializeViews() {
        UserData userData = UserSessionManager.getUserData();
        userSessionManager = new UserSessionManager(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mSaveBtn = (Button) findViewById(R.id.SE_SaveChangesBtn);
        mActivityLevelText = (TextView) findViewById(R.id.SE_TextActivityLevelView);
        mCurrentStepGoalText = (TextView) findViewById(R.id.SE_TextStepGoalView);
        mRestingHeartRate = (TextView) findViewById(R.id.SE_TextRestingHeartRateView);
        mHeartRateMax = (TextView) findViewById(R.id.SE_TextHeartRateMaxView);
        activityLevel = userData.getActivityLevel();
        stepGoal = userData.getStepGoal();

        mActivityLevelText.setText(String.format(Locale.getDefault(), "%d", activityLevel));

        if(stepGoal == 0){
            stepGoal = 5000;
            mCurrentStepGoalText.setText(String.format(Locale.getDefault(), "%d", stepGoal));
            userData.setStepGoal(stepGoal);
        } else {
            mCurrentStepGoalText.setText(String.format(Locale.getDefault(), "%d", stepGoal));
        }

        if (!(userData.getRestingHeartRate() == 0)) {
            restingHeartRate = userData.getRestingHeartRate();
        } else {
            restingHeartRate = 60;
        }

        //If HRMax is set, get it
        if (!(userData.getHeartRateMax() == 0)) {
            heartRateMax = userData.getHeartRateMax();
            //If HRMax is not set, check if Age is set and calculate HRMax based on it
        } else if (!(userData.getAge() == 0)) {
            heartRateMax = (int) (208 - (0.7 * (userData.getAge())));
        } else {
            heartRateMax = 0;
        }


        mRestingHeartRate.setText(String.format(Locale.getDefault(), "%d", restingHeartRate));
        mHeartRateMax.setText(String.format(Locale.getDefault(), "%d", heartRateMax));
    }

    public void onClickSettingsActivity(View v) {
        if (v.getId() == R.id.SE_GoalSelector) {
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

    /**
     * Method to set the activity level
     */
    public void setActivityLevelDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        dialog.setTitle("Select your activity");
        dialog.setSingleChoiceItems(activityLevelTextArray, -1, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                activityLevel = item;
                mActivityLevelText.setText(String.format(Locale.getDefault(), "%d", activityLevel));
                dialog.dismiss();
            }
        });
        AlertDialog alert = dialog.create();
        alert.show();
    }

    /**
     * Method to set the stepGoal
     */
    public void setGoalDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        dialog.setTitle("Set your stepGoal for today");
        dialog.setSingleChoiceItems(goalsValuesArray, -1, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                mapSelectedItemToGoal(item);
                dialog.dismiss();
            }
        });
        AlertDialog alert = dialog.create();
        alert.show();

    }

    //Maps the selected item from SetGoalDialog to the widget and sharedValue
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

    //Edit dialog for stepGoal
    private void editGoal() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        dialog.setTitle("Please enter daily Goal");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        dialog.setView(input);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int positiveButton) {
                String foo = input.getText().toString();
                stepGoal = Integer.parseInt(foo);
                mCurrentStepGoalText.setText(input.getText());
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int negativeButton) {
                //Put actions for CANCEL button here, or leave in blank
                mCurrentStepGoalText.setText(String.format(Locale.getDefault(), "%d", stepGoal));
                dialog.dismiss();
            }
        });
        AlertDialog alert = dialog.create();
        alert.show();
    }

    /**
     * Safe and persist userdata on server
     */
    public void updateData() {
        UserData userData = UserSessionManager.getUserData();

        userData.setStepGoal(stepGoal);
        userData.setActivityLevel(activityLevel);
        userData.setHeartRateMax(heartRateMax);
        userData.setRestingHeartRate(restingHeartRate);
        UserSessionManager.setUserData(userData);
        userSessionManager.uploadUserData(this, true, false, false);
    }

    @Override
    protected void onResume() {
        userSessionManager.checkUserState();
        super.onResume();
    }

    /**
     * Method to generate numberPicker dialog
     *
     * @param min min number to pick
     * @param max max number to pick
     * @param textView textview to display picked number
     * @param title title of to be created numberpicker
     */
    private void numberPickerDialog(int min, int max, final TextView textView, final String title) {
        NumberPicker myNumberPicker = new NumberPicker(this);
        myNumberPicker.setMinValue(min);
        myNumberPicker.setMaxValue(max);

        NumberPicker.OnValueChangeListener myValChangeListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                textView.setText(String.format(Locale.getDefault(), "%d", newVal));
            }
        };

        myNumberPicker.setOnValueChangedListener(myValChangeListener);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.MyDialogTheme).setView(myNumberPicker);
        dialog.setTitle(title);

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (textView.getId() == mRestingHeartRate.getId()) {
                    restingHeartRate = Integer.parseInt(mRestingHeartRate.getText().toString());
                } else if (textView.getId() == mHeartRateMax.getId()) {
                    heartRateMax = Integer.parseInt(mHeartRateMax.getText().toString());
                }
            }
        });
        AlertDialog alert = dialog.create();
        alert.show();

    }
}


