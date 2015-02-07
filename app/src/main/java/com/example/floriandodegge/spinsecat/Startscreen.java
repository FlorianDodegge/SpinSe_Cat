package com.example.floriandodegge.spinsecat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by FlorianDodegge on 07.02.15.
 */
public class Startscreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startscreen);

        ActionBar actionBar = getActionBar();
        actionBar.hide();

    }

    public void loslegen(View Buttonclick) {

        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);


    }

}
