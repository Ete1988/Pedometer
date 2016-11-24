package com.mueller.mobileSports.general;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.sharedValues;

public class SettingsActivity extends AppCompatActivity {

    private int physicalActivityLevel;
    private int goal;
    private TextView level;
    private TextView stepGoal;
    private sharedValues values;

    private String[] goal_arr = {"5000", "6000", "7000", "8000", "9000", "10000", "Other? Please set here!"};

    private String[] actLevel_arr = {"0: Avoid walking or exertion, for example, always use elevator, drive " +
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
        level = (TextView) findViewById(R.id.level);
        stepGoal = (TextView) findViewById(R.id.stepGoalView);
        values = sharedValues.getInstance(this);
        physicalActivityLevel = values.getInt("physicalActivityLevel");
        goal = values.getInt("stepGoal");
        setLevel();
        //setGoal();

        setSupportActionBar(myToolbar);
        setTitle("Settings");
    }

    public void onClick(View v) {
        if (v.getId() == R.id.editProfileButton) {
            Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
    }

    public void setActivityLevelDialog(View v) {

        AlertDialog.Builder activityLevelDialog = new AlertDialog.Builder(this);
        activityLevelDialog.setTitle("Select your activity level");
        activityLevelDialog.setSingleChoiceItems(actLevel_arr, -1, new DialogInterface
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
        activityLevelDialog.setTitle("Set your goal for today");
        activityLevelDialog.setSingleChoiceItems(goal_arr, -1, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                goal = item;
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
                goal = Integer.parseInt(foo);
                stepGoal.setText(input.getText());
                values.saveInt("stepGoal", goal);
                dialog.dismiss();

            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int negativeButton) {
                //Put actions for CANCEL button here, or leave in blank

                stepGoal.setText(Integer.toString(values.getInt("stepGoal")));
                dialog.dismiss();

            }
        });
        alert.show();

    }

    private void setLevel() {

        switch (physicalActivityLevel) {
            case 0:
                level.setText("0");
                break;
            case 1:
                level.setText("1");
                break;
            case 2:
                level.setText("2");
                break;
            case 3:
                level.setText("3");
                break;
            case 4:
                level.setText("4");
                break;
            case 5:
                level.setText("5");
                break;
            case 6:
                level.setText("6");
                break;
            case 7:
                level.setText("7");
                break;
            default:
                level.setText(" ");
                break;
        }

        values.saveInt("physicalActivityLevel", physicalActivityLevel);
    }

    private void setGoal() {
        switch (goal) {

            case 0:
                stepGoal.setText("5000");
                goal = 5000;
                break;
            case 1:
                stepGoal.setText("6000");
                goal = 6000;
                break;
            case 2:
                stepGoal.setText("7000");
                goal = 7000;
                break;
            case 3:
                stepGoal.setText("8000");
                goal = 8000;
                break;
            case 4:
                stepGoal.setText("9000");
                goal = 9000;
                break;
            case 5:
                stepGoal.setText("10000");
                goal = 10000;
                break;
            case 6:
                editGoal();
                break;
            default:
                stepGoal.setText(Integer.toString(values.getInt("stepGoal")));
                break;
        }
        values.saveInt("stepGoal", goal);
    }
}


