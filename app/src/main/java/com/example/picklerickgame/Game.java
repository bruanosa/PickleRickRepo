package com.example.picklerickgame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;




public class Game extends AppCompatActivity
{
    private DataBaseHelper mDataBaseHelper;
    private SQLiteDatabase myDB;
    private ArrayList<HighScoreInfo> theList;

    public static class Rat
    {
        public ImageView imgView;
        public float x, y;
        public int width, height;
        public String size;

        public Rat()
        {
            imgView = null;
            x = -1;
            y = -1;
            width = -1;
            height = -1;
            size = "unknown";
        }
    }

    private static String difficulty;
    private static final int SLOW_SPEED = 10, MEDIUM_SPEED = 15, FAST_SPEED = 20, GOD_SPEED = 25;
    private static int ratSpeed, pickleRickSpeed;
    private static final int NUM_OF_RATS = 5;

    private static TextView touch2Start;
    private static FrameLayout frame;
    ImageButton pauseButton;

    Rat[] ratArray = new Rat[NUM_OF_RATS];

    //pickle rick attributes
    private static ImageView pickleRick;
    private static float prX, prY;
    private static int prHeight, prWidth;

    //Game Stats attributes
    private static TextView scoreTV;
    private static int score, highscore;

    //boolean variables
    private static  boolean fingerIsDown; // true if users finger is down
    private static boolean gotHit; // true if user got hit by a rat
    private static boolean hasStarted; //true if game has started
    private static boolean paused; //if the game has been paused it will be true

    //things that help image rendering
    private static Handler handy;
    private static Timer t;

    //Screen specs
    private static int frameHeight, frameWidth, screenHeight, screenWidth;

    //(x1,y1) are the coordinates of where the user pressed down on the screen
    //(x2,y2) are the coordinates of where the user lifted his finger up
    //dx and dy are change in x and change in y respectively
    //these variables are used for finger swiping gestures
    private static float x1, x2, y1, y2, dx, dy;

    //this string represents the current movement state of pickle rick
    private static String direction = "still";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        pickleRickSpeed = GOD_SPEED;

        pauseButton = (ImageButton) findViewById(R.id.imageButton);
        pauseButton.setVisibility(View.GONE);

        Intent incomingIntent = getIntent();
        highscore = incomingIntent.getIntExtra("highscore", -1);
        difficulty = incomingIntent.getStringExtra("difficulty");

        if(difficulty == null)
        {
            difficulty = "easy";
        }
        else
        {
            difficulty.toLowerCase();
        }

        switch(difficulty)
        {
            case "easy":
                ratSpeed = SLOW_SPEED;
                break;
            case "medium":
                ratSpeed = MEDIUM_SPEED;
                break;
            case "hard":
                ratSpeed = FAST_SPEED;
                break;
            case"insane":
                ratSpeed = GOD_SPEED;
                break;

            default:
                    ratSpeed = SLOW_SPEED;
                    break;


        }

        touch2Start = (TextView) findViewById(R.id.touch2startTV);

        //counters
        score = 0;

        //boolean flags
        fingerIsDown = false;
        hasStarted = false;
        paused = false;


        //get screen size
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;


        //Populate our rat array
        for(int i = 0; i < 5; i++)
        {
            ratArray[i] = new Rat();
        }


        //this handles the spawn rate
        handy = new Handler();
        t = new Timer();


        //initialize character image views
        pickleRick = (ImageView) findViewById(R.id.imageViewPickleRick);
        pickleRick.setVisibility(View.INVISIBLE);

        ratArray[0].imgView = (ImageView) findViewById(R.id.smallRat1);
        ratArray[0].size = "small";
        ratArray[1].imgView  = (ImageView) findViewById(R.id.smallRat2);
        ratArray[1].size = "small";
        ratArray[2].imgView  = (ImageView) findViewById(R.id.smallRat3);
        ratArray[2].size = "small";
        ratArray[3].imgView  = (ImageView) findViewById(R.id.bigRat1);
        ratArray[3].size = "large";
        ratArray[4].imgView  = (ImageView) findViewById(R.id.bigRat2);
        ratArray[4].size = "large";

        //initialize game stat views
        scoreTV = (TextView) findViewById(R.id.scoreValueTextView);
        scoreTV.setText(Integer.toString(score));

        //coordinates (-80,-80) are off the visual screen
        int startingPosXY = -80;

        for(Rat r : ratArray)
        {
            //initially make rats invisible
            r.imgView.setVisibility(View.INVISIBLE);

            //set rat image coordinates
            r.imgView.setX(startingPosXY);
            r.imgView.setY(startingPosXY);

            //store coordinate values
            r.x = startingPosXY;
            r.y = startingPosXY;
        }




        //get pickle ricks screen coordinates
        prX = pickleRick.getX();
        prY = pickleRick.getY();
    }

    //updates position of rats and pickle rick
    public void changePos()
    {

        for(Rat rat : ratArray)
        {
            gotHit = hitCheck(rat);
            rat.x = rat.x - ratSpeed;

            //if rat has gone off screen or has been hit respawn
            if(rat.x < 0 - rat.width || gotHit)
            {

                if(rat.size.equalsIgnoreCase("small"))
                {
                    // generate random number from 20 - 100
                    int randomX = (int)(Math.random()*8) + 2;
                    randomX *= 10;

                    //set rats new coordinate position
                    rat.x = screenWidth + randomX;
                    //generate random y value that fits on the visible screen
                    rat.y = (int) Math.floor(Math.random()*(frameHeight - rat.height));
                }
                //want to spawn bigger rats further away so that they don't come as frequently
                else
                {
                    //if got hit by a big rat game is over
                    if(gotHit)
                    {
                        gameOver();
                    }
                    //generate random number from 1000 - 5000
                    int randomX = (int)(Math.random()*5) + 1;
                    randomX *= 1000;

                    //set rats new coordinates
                    rat.x = screenWidth + randomX;
                    //generate random y value that fits on the visible screen
                    rat.y = (int) Math.floor(Math.random()*(frameHeight-rat.height));
                }

            }
            //render the image with the new coordinates
            rat.imgView.setX(rat.x);
            rat.imgView.setY(rat.y);
            //reset flag
            gotHit = false;
        }




        //---------------------------------------PICKLE RICK RENDERING------------------------------------------------------------------------------

        //this is what makes pickle rick move
        switch(direction)
        {
            case "down":
                prY -= pickleRickSpeed;
                break;
            case "up":
                prY += pickleRickSpeed;
                break;
            case "left":
                prX -= pickleRickSpeed;
                break;
            case "right":
                prX += pickleRickSpeed;
                break;
            case "still":
                break;
        }

        //code to make it a toroidal world
        //if(prY < 0 - prHeight) prY = frameHeight;
        //if(prY > frameHeight) prY = 0 - prHeight;
        //if(prX < 0 - prWidth) prX = frameWidth;
        //if(prX > frameWidth) prX = 0 - prWidth;

        //bounded to the screen
        if(prY <= 0) prY = 0; //keeps pickle rick bounded to not pass the top of the screen
        if(prY >= frameHeight - prHeight) prY = frameHeight - prHeight; //keeps pickle rick bounded to not pass the bottom of the screen
        if(prX <= 0) prX = 0; //keeps pickle rick bounded to not pass the left of the screen
        if(prX > frameWidth - prWidth) prX = frameWidth - prWidth; // keeps pickle rick bounded to not pass the right of the screen

        //renders pickle ricks new location
        pickleRick.setY(prY);
        pickleRick.setX(prX);
    }

    private boolean hitCheck(Rat rat)
    {
        int ratCenterX = (int) rat.x + (int)(rat.width / 2);
        int ratCenterY = (int) rat.y + (int)(rat.height/ 2);

        if(rat.size.equalsIgnoreCase("large"))
        {
            //case when you've hit a large rat aka your dead
            if(0 <= ratCenterX && ratCenterX <= prWidth && prY <= ratCenterY && ratCenterY <= prY + prHeight)
            {
                return true;
            }
        }
        else if(rat.size.equalsIgnoreCase("small"))
        {
            //case when you have hit a small rat
            if(0 <= ratCenterX && ratCenterX <= prWidth && prY <= ratCenterY && ratCenterY <= prY + prHeight)
            {
                score++;
                scoreTV.setText(Integer.toString(score));
                return true;
            }
        }
        else
        {
            print(this, "rat size was never initialized");
        }
        return false;
    }

    private void gameOver()
    {
        t.cancel();
        t = null;
        Intent gameOverIntent = new Intent(this, GameOver.class);
        if(score > highscore)
        {
            highscore = score;
        }
        gameOverIntent.putExtra("score", score);
        gameOverIntent.putExtra("highscore", highscore);
        gameOverIntent.putExtra("difficulty", difficulty);
        startActivityForResult(gameOverIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == 1)
        {
            int dataHighScore = data.getIntExtra("highscore", -1);
            if(dataHighScore > highscore)
            {
                highscore = dataHighScore;
            }
            onBackPressed();
        }
    }

    public static void print(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        pickleRick.setVisibility(View.VISIBLE);
        pauseButton.setVisibility(View.VISIBLE);

        //make all the rats visible
        for(Rat r : ratArray)
        {
            r.imgView.setVisibility(View.VISIBLE);
        }


        touch2Start.setVisibility(View.GONE);

        if(hasStarted == false)
        {
            hasStarted = true;

            //get the game frame dimensions
            frame = (FrameLayout) findViewById(R.id.frameLayout);
            frameHeight = frame.getHeight();
            frameWidth = frame.getWidth();

            prX = (int) pickleRick.getX();
            prY = (int) pickleRick.getY();

            //get pickle ricks height width
            prHeight = pickleRick.getHeight();
            prWidth = pickleRick.getWidth();

            for(Rat rat : ratArray)
            {
                //set rat width and height values
                rat.height = rat.imgView.getHeight();
                rat.width = rat.imgView.getWidth();
            }

            //this is what controls the spawn rate
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    handy.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }, 0, 10);

        }
        else
        {
            //want to save the coordinates when a finger touches the screen
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                fingerIsDown = true;
                x1 = event.getX();
                y1 = event.getY();

            }
            //want to save the coordinates when a finger is lifted from the screen
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                fingerIsDown = false;
                x2 = event.getX();
                y2 = event.getY();
                dx = x2-x1;
                dy = y2-y1;

                //if the finger position didn't change than keep pickle rick from moving up or down
                if(Math.abs(dy) < 10 && Math.abs(dy) >= 0)
                {
                    direction = "still";
                }
                //case when user swiped up or down
                else
                {
                    //use this if you want to allow left and right movements too
                    /*
                    if(Math.abs(dx) > Math.abs(dy))
                    {
                        if(dx>0)
                            direction = "right";
                        else
                            direction = "left";
                    } else {
                        if(dy>0)
                            direction = "up";
                        else
                            direction = "down";
                    }
                     */

                    if(dy>0)
                    {
                        direction = "up";
                    }
                    else
                    {
                        direction = "down";
                    }


                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_OK);
        super.onBackPressed();
        if( t != null)
        {
            t.cancel();
            t = null;
        }
        finish();
    }

    public void onPauseButtonClicked(View v)
    {
        if(paused)
        {
            paused = false;
            pauseButton.setImageResource(R.drawable.pause_icon);
            //this is what controls the spawn rate
            t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    handy.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }, 0, 10);
        }
        else
        {
            paused = true;
            pauseButton.setImageResource(R.drawable.play_icon);
            if(t != null)
            {
                t.cancel();
                t = null;
            }
            if(handy != null)
            {
                handy.removeCallbacksAndMessages(null);
            }
        }
    }
}
