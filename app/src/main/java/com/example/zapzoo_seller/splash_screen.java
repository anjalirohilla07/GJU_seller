package com.example.zapzoo_seller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class splash_screen extends AppCompatActivity {

    // Variables
    public int SPLASH_TIMER = 1500;
    ImageView image;
    TextView logo_name,tagline,owner;
    Animation topAnim, rsideAnim, lsideAnim,bottomAnim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // to hide status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // hooks
        image=findViewById(R.id.ssImage);
        logo_name=findViewById(R.id.ssText);
        tagline=findViewById(R.id.ssText2);
        owner=findViewById(R.id.ssText3);

        // init animation
        topAnim= AnimationUtils.loadAnimation(splash_screen.this,R.anim.top_anim);
        rsideAnim=AnimationUtils.loadAnimation(splash_screen.this,R.anim.rside_anim);
        lsideAnim=AnimationUtils.loadAnimation(splash_screen.this,R.anim.lside_anim);
        bottomAnim=AnimationUtils.loadAnimation(splash_screen.this,R.anim.bottom_anim);

        // set Animations
        image.setAnimation(topAnim);
        logo_name.setAnimation(rsideAnim);
        tagline.setAnimation(lsideAnim);
        owner.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(getApplicationContext(),login.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIMER);
    }
}