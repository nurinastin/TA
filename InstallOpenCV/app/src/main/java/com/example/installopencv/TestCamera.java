package com.example.installopencv;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Camera;
import android.os.Bundle;
import android.widget.FrameLayout;

public class TestCamera extends AppCompatActivity {
    Camera camera;
    FrameLayout frameLayout;
    ShowCamera show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_camera);
        camera = Camera.open();
        frameLayout = findViewById(R.id.frameLayout);
        show = new ShowCamera(this, camera);
        frameLayout.addView(show);
    }
}