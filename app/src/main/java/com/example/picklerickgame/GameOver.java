package com.example.picklerickgame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

public class GameOver extends AppCompatActivity
{

    private int score;
    private static int highscore;
    private DataBaseHelper mDataBaseHelper;
    private SQLiteDatabase myDB;
    private ArrayList<HighScoreInfo> theList;
    private TextView userName;
    private EditText userNameInput;
    private HighScoreInfo info;
    private String name = "";
    private String difficulty;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Intent incomingIntent = getIntent();
        score = incomingIntent.getIntExtra("score", -1);
        highscore = incomingIntent.getIntExtra("highscore", -1);
        difficulty = incomingIntent.getStringExtra("difficulty");

        TextView scoreTV = (TextView) findViewById(R.id.yourScoreValueGO);
        TextView highscoreTV = (TextView)findViewById(R.id.highScoreValueGO);
        scoreTV.setText(Integer.toString(score));
        highscoreTV.setText(Integer.toString(highscore));

        mDataBaseHelper = new DataBaseHelper(this);
        myDB = mDataBaseHelper.getWritableDatabase();
        theList = mDataBaseHelper.fetch(myDB);

        info = new HighScoreInfo();
        userName = findViewById(R.id.yourName);
        userNameInput = findViewById(R.id.yourNameInput);
        Collections.sort(theList);
        for(HighScoreInfo temp: theList) {
            if (score >= temp.getScore()) {
                info = temp;
                userName.setVisibility(View.VISIBLE);
                userNameInput.setVisibility(View.VISIBLE);
                break;
            }
        }
    }


    public void onTryAgain(View v)
    {   name = userNameInput.getText().toString();
        Intent playAgainIntent = new Intent(this, Game.class);
        playAgainIntent.putExtra("highscore", highscore);
        playAgainIntent.putExtra("difficulty", difficulty);
        startActivityForResult(playAgainIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 1)
        {
            /** DEBUG
            int dataHighScore = data.getIntExtra("highscore", -1);
            if(dataHighScore > highscore)
            {
                highscore = dataHighScore;
            }*/

            mDataBaseHelper = new DataBaseHelper(this);
            myDB = mDataBaseHelper.getWritableDatabase();
            theList = mDataBaseHelper.fetch(myDB);

            info = new HighScoreInfo();
            userName = findViewById(R.id.yourName);
            userNameInput = findViewById(R.id.yourNameInput);
            Collections.sort(theList);
            for(HighScoreInfo temp: theList) {
                if (score >= temp.getScore()) {
                    info = temp;
                    userName.setVisibility(View.VISIBLE);
                    userNameInput.setVisibility(View.VISIBLE);
                    break;
                }
            }

            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        name = userNameInput.getText().toString();
        String oldName = "";
        int oldScore = 0;
        int id = 0;
        Collections.sort(theList);
        for(HighScoreInfo temp: theList) {
            if (info.compareTo(temp) == 0) {
                oldName = temp.getInitials();
                oldScore = temp.getScore();
                id = temp.getId();
                temp.setInitials(name);
                temp.setScore(score); // Note: highscore used to be score
                Collections.sort(theList);
                mDataBaseHelper.updateInitials(name, id, oldName, myDB);
                mDataBaseHelper.updateHighScore(score, id, oldScore, myDB); // Note: highscore used to be score
                myDB = mDataBaseHelper.getWritableDatabase();
                break;
            }
        }

        Intent outgoingIntent = new Intent();
        outgoingIntent.putExtra("highscore", highscore);
        setResult(RESULT_OK, outgoingIntent);
        super.onBackPressed();
        finish();
    }
}
