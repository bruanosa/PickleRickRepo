package com.example.picklerickgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScores extends AppCompatActivity {
    private static final String TAG = "HighScores";
    private TextView first_init;
    private TextView first_score;
    private TextView second_init;
    private TextView second_score;
    private TextView third_init;
    private TextView third_score;
    private TextView fourth_init;
    private TextView fourth_score;
    private TextView fifth_init;
    private TextView fifth_score;
    private DataBaseHelper mDataBaseHelper;
    private SQLiteDatabase myDB;
    private ArrayList<HighScoreInfo> theList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        first_init = findViewById(R.id.firstInitials);
        first_score = findViewById(R.id.firstScore);
        second_init = findViewById(R.id.secondInitials);
        second_score = findViewById(R.id.secondScore);
        third_init = findViewById(R.id.thirdInitials);
        third_score = findViewById(R.id.thirdScore);
        fourth_init = findViewById(R.id.fourthInitials);
        fourth_score = findViewById(R.id.fourthScore);
        fifth_init = findViewById(R.id.fifthInitials);
        fifth_score = findViewById(R.id.fifthScore);
        mDataBaseHelper = new DataBaseHelper(this);
        myDB = mDataBaseHelper.getWritableDatabase();
        theList = mDataBaseHelper.fetch(myDB);
        Collections.sort(theList);

        HighScoreInfo initsAndScore = theList.get(4);
        first_init.setText(initsAndScore.getInitials());
        first_score.setText("" + initsAndScore.getScore());

        initsAndScore = theList.get(3);
        second_init.setText(initsAndScore.getInitials());
        second_score.setText("" + initsAndScore.getScore());

        initsAndScore = theList.get(2);
        third_init.setText(initsAndScore.getInitials());
        third_score.setText("" + initsAndScore.getScore());

        initsAndScore = theList.get(1);
        fourth_init.setText(initsAndScore.getInitials());
        fourth_score.setText("" + initsAndScore.getScore());

        initsAndScore = theList.get(0);
        fifth_init.setText(initsAndScore.getInitials());
        fifth_score.setText("" + initsAndScore.getScore());
    }

    @Override
    public void onBackPressed()
        {
        super.onBackPressed();
        startActivity(new Intent(HighScores.this, MainActivity.class));
        finish();
    }
}