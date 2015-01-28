package com.example.floriandodegge.spinsecat;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class MainActivity extends Activity {

    private ImageView img;
    private GestureDetector gd;
    private float xDown, xUp, yDown, yUp, deltaX, deltaY, rotation;
    private Animation rotationRight, rotationLeft;
    private int minSwipeDelta;        //min Distanz

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_activity);

        img = (ImageView) findViewById(R.id.image);

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

                        } else {
                            Log.i("SWIPE", "left");
                            img.startAnimation(rotationLeft);
                        }
                    }
                    result = true;
                } else if (Math.abs(deltaY) > minSwipeDelta) {   //vertikaler Swipe
                    if (deltaY < 0) {
                        Log.i("SWIPE", "left");
                        img.startAnimation(rotationLeft);
                    } else {
                        Log.i("SWIPE", "right");
                        img.startAnimation(rotationRight);
                    }
                }

                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_activitiy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

//Test