package com.example.picklerickgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    private static String difficulty;
    private static RadioGroup radGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent incomingIntent = getIntent();
        difficulty = incomingIntent.getStringExtra("difficulty");
        if(difficulty != null)
        {
            difficulty = difficulty.toLowerCase();
        }



        radGroup = (RadioGroup) findViewById(R.id.radGroup);
        RadioButton easy = (RadioButton) findViewById(R.id.radioButtonEasy);
        RadioButton medium = (RadioButton) findViewById(R.id.radioButtonMED);
        RadioButton hard = (RadioButton) findViewById(R.id.radioButtonHard);
        RadioButton insane = (RadioButton) findViewById(R.id.radioButtonINSANE);

        switch(difficulty)
        {
            case "easy":
                easy.setChecked(true);
                break;
            case "medium":
                medium.setChecked(true);
                break;
            case "hard":
                hard.setChecked(true);
                break;
            case "insane":
                insane.setChecked(true);
                break;
            default :
                easy.setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {

        Intent outgoingIntent = new Intent();
        int rbID = radGroup.getCheckedRadioButtonId();

        switch(rbID)
        {
            case R.id.radioButtonEasy:
                difficulty = "easy";
                break;
            case R.id.radioButtonMED:
                difficulty = "medium";
                break;
            case R.id.radioButtonHard:
                difficulty = "hard";
                break;
            case R.id.radioButtonINSANE:
                difficulty = "insane";
                break;
        }

        outgoingIntent.putExtra("difficulty", difficulty);

        setResult(RESULT_OK, outgoingIntent);
        super.onBackPressed();
    }
}
