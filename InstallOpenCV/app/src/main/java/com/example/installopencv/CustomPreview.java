package com.example.installopencv;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CustomPreview extends AppCompatActivity  {
    static final int MY_PERMISSIONS_REQUEST_CAMERA = 1242;
    TextView open, flip;
    ImageView cover;
    int SELECT_IMAGE = 1;
    private Uri contentURI;
    Bitmap bitmap;
    int flipn = 0;
    Camera camera;
    FrameLayout view;
    ShowCamera show;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_preview);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        cover = findViewById(R.id.cover);
        open = findViewById(R.id.open);
        flip = findViewById(R.id.flip);
        flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flipn == 0){
                    flipn = 1;
                    cover.setScaleX(-1);
                }else if(flipn == 1){
                    flipn = 2;
                    cover.setScaleX(1);
                }else if(flipn == 2){
                    flipn = 3;
                    cover.setScaleY(1);
                }else{
                    flipn = 0;
                    cover.setScaleY(-1);
                }
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                        + "/"+getApplicationInfo().loadLabel(getPackageManager()).toString());
                intent.setDataAndType(uri, "image/*");
                startActivityForResult(Intent.createChooser(intent, "Open folder"), SELECT_IMAGE);
            }
        });
        try {
            Intent data = getIntent();
            if(!data.getStringExtra("file").equals("")){
                loadfile();
            }
        } catch (IOException e) {
            Log.d("pesanE", e.getMessage());
            e.printStackTrace();
        }
        camera = Camera.open();
        view = findViewById(R.id.view);
        show = new ShowCamera(this, camera);
        view.addView(show);

    }
    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
        Intent intent = new Intent(CustomPreview.this, Home.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("pesan", "activityresult");
        if (resultCode == this.RESULT_CANCELED) {

            return;
        }
        if (requestCode == SELECT_IMAGE) {
            if (data != null) {
                contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    Log.d("lokasi", contentURI.toString());
                    cover.setImageBitmap(bitmap);
                    cover.setAlpha(127);
//                    detectEdges(bitmap, contentURI);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
    void loadfile() throws IOException {
        Intent data = getIntent();
        File sdCard = Environment.getExternalStorageDirectory();

        File directory = new File (sdCard.getAbsolutePath() + "/"+getApplicationInfo().loadLabel(getPackageManager()).toString());

        File file = new File(directory, data.getStringExtra("file")); //or any other format supported

        FileInputStream streamIn = new FileInputStream(file);

        Bitmap bitmap = BitmapFactory.decodeStream(streamIn); //This gets the image
        cover.setImageBitmap(bitmap);
        cover.setAlpha(127);
        streamIn.close();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
//                    try {
//                        mCameraManager.openCamera(mCameraIDsList[1], mCameraStateCB, new Handler());
//                    } catch (CameraAccessException e) {
//                        e.printStackTrace();
//                    }
                break;
        }
    }

}