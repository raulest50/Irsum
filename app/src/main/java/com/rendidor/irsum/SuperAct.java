package com.rendidor.irsum;


import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class SuperAct extends AppCompatActivity {
    public void ShowT(String te) {
        Toast to = Toast.makeText(getApplicationContext(), te, 1);
        to.setDuration(Toast.LENGTH_SHORT);
        to.setGravity(17, 0, 0);
        to.show();
    }
}
