package com.example.picklerickgame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity
{
    private static final int REQUEST_CODE = 1;
    private static int highscore;
    private static String difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        highscore = 0;
        difficulty = "easy";
    }

    //---------------Button Callback Functions----------------------//
    public void onStartGame(View v)
    {
        Intent startGameIntent = new Intent(this, Game.class);
        startGameIntent.putExtra("highscore", highscore);
        startGameIntent.putExtra("difficulty", difficulty);
        startActivity(startGameIntent);
    }
    public void onGameRules(View v)
    {
        Intent gameRulesIntent = new Intent(this, GameRules.class);
        startActivity(gameRulesIntent);
    }
    public void onHighScores(View v)
    {
        Intent highScoreIntent = new Intent(this, HighScores.class);
        startActivity(highScoreIntent);
    }
    public void onSettings(View v)
    {
        Intent settingsIntent = new Intent(this, Settings.class);
        settingsIntent.putExtra("difficulty", difficulty);
        startActivityForResult(settingsIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            String newDiff = data.getStringExtra("difficulty");
            if(newDiff != null && newDiff.length() != 0)
            {
                difficulty = newDiff;
            }
        }
    }
}
