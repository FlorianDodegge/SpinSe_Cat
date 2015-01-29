package com.example.floriandodegge.spinsecat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;


public class MainActivity extends Activity {

    private ImageView img;
    private GestureDetector gd;
    private float xDown, xUp, yDown, yUp, deltaX, deltaY;
    private Animation rotationRight, rotationLeft;
    private int minSwipeDelta;        //min Distanz
    private int randomID;       //Randomzahl für Datenbank ID      //Todo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sauf_di_zua);

        ActionBar actionbar = getActionBar();
        actionbar.hide();

        img = (ImageView) findViewById(R.id.imageView);

        rotationRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right);
        rotationLeft = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.left);

        gd = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener());

        ViewConfiguration config = ViewConfiguration.get(this);         //für Standardkonfigurationen (Timeouts, Größen, Distanzen)
        minSwipeDelta = config.getScaledPagingTouchSlop();              //Distanz in Pixel
    }

    public boolean onTouchEvent(MotionEvent event) {

        gd.onTouchEvent(event);

        boolean result = false;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                xDown = event.getX();
                yDown = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                xUp = event.getX();
                yUp = event.getY();

                deltaX = xUp - xDown;
                deltaY = yUp - yDown;


                if (Math.abs(deltaX) > Math.abs(deltaY)) {    //horizontaler Swipe
                    if (Math.abs(deltaX) > minSwipeDelta) {   //Gültigkeit bzgl Standardeinstellungen prüfen
                        if (deltaX < 0) {
                            Log.i("SWIPE", "right");
                            img.startAnimation(rotationRight);

                            randomID();

                        } else {
                            Log.i("SWIPE", "left");
                            img.startAnimation(rotationLeft);

                            randomID();
                        }
                    }
                    result = true;
                } else if (Math.abs(deltaY) > minSwipeDelta) {   //vertikaler Swipe
                    if (deltaY < 0) {
                        Log.i("SWIPE", "left");
                        img.startAnimation(rotationLeft);

                        randomID();
                    } else {
                        Log.i("SWIPE", "right");
                        img.startAnimation(rotationRight);

                        randomID();
                    }
                }

                break;
        }
        return true;
    }

    public int randomID(){
        randomID = (int) ((Math.random() * 10))+1;

        Log.i("RANDOM", "randomID = " + randomID);

        return randomID;
    }
}