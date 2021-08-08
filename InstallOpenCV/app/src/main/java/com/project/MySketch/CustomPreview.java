package com.project.MySketch;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CustomPreview extends AppCompatActivity  {
    static final int MY_PERMISSIONS_REQUEST_CAMERA = 1242;
    TextView open, flip, scale;
    ImageView cover;
    int SELECT_IMAGE = 1;
    private Uri contentURI;
    Bitmap bitmap;
    int flipn = 0, scaleValue = 0;
    Camera camera;
    FrameLayout view;
    ShowCamera show;
    LinearLayout scale_object;
    int height = 600, width = 600;
    EditText input_height, input_width;
    Button simpan;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_preview);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        cover = findViewById(R.id.cover);
        open = findViewById(R.id.open);
        flip = findViewById(R.id.flip);
        scale = findViewById(R.id.scale);
        simpan = findViewById(R.id.simpan);
        input_height = findViewById(R.id.input_height);
        input_width = findViewById(R.id.input_width);
        scale_object = findViewById(R.id.scale_object);
        input_height.setText(String.valueOf(height));
        input_width.setText(String.valueOf(width));
        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("klik", "simpan");
                changeSize(Integer.parseInt(input_height.getText().toString()), Integer.parseInt(input_width.getText().toString()));
            }
        });
        scale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("height", String.valueOf(cover.getLayoutParams().height));
                check();
            }
        });
        check();
        changeSize(height, width);
        scale_object = findViewById(R.id.scale_object);
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cover.setScaleX(600);
                cover.setScaleY(600);
            }
        });
        cover.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float xDown = 0, yDown = 0;
                switch(event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN:
                        xDown = event.getX();
                        yDown = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float movedX, movedY;
                        movedX = event.getX();
                        movedY = event.getY();
                        float distanceX = movedX - xDown - (cover.getLayoutParams().height - 200);
                        float distanceY = movedY - yDown - (cover.getLayoutParams().width - 200);
                        cover.setX(cover.getX() + distanceX);
                        cover.setY(cover.getY() + distanceY);
                        break;
                }
                return true;

            }
        });
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
        showcase();
    }
    public void showcase(){
        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(flip,"Flip","Berfungsi untuk memutar gambar")
                                .outerCircleColor(R.color.colorWhite)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.colorPrimary)
                                .titleTextSize(20)
                                .titleTextColor(R.color.colorBlack)
                                .descriptionTextSize(10)
                                .descriptionTextColor(R.color.colorBlack)
                                .textColor(R.color.colorBlack)
                                .textTypeface(Typeface.SANS_SERIF)
                                .dimColor(R.color.colorBlack)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(true)
                                .targetRadius(60),
                        TapTarget.forView(open,"Open","Berfungsi untuk membuka gambar dari gallery")
                                .outerCircleColor(R.color.colorWhite)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.colorPrimary)
                                .titleTextSize(20)
                                .titleTextColor(R.color.colorBlack)
                                .descriptionTextSize(10)
                                .descriptionTextColor(R.color.colorBlack)
                                .textColor(R.color.colorBlack)
                                .textTypeface(Typeface.SANS_SERIF)
                                .dimColor(R.color.colorBlack)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(true)
                                .targetRadius(60),
                        TapTarget.forView(scale,"Scale","Berfungsi untuk mengubah ukuran gambar")
                                .outerCircleColor(R.color.colorWhite)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.colorPrimary)
                                .titleTextSize(20)
                                .titleTextColor(R.color.colorBlack)
                                .descriptionTextSize(10)
                                .descriptionTextColor(R.color.colorBlack)
                                .textColor(R.color.colorBlack)
                                .textTypeface(Typeface.SANS_SERIF)
                                .dimColor(R.color.colorBlack)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(true)
                                .targetRadius(60)).start();
    }
//    fungsi untuk mengubah ukuran gambar
    void changeSize(int vheight, int vwidth){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(vwidth, vheight);
        cover.setLayoutParams(layoutParams);
    }

    void check(){
        if(scaleValue == 1){
            scaleValue = 0;
            scale_object.setVisibility(View.VISIBLE);
        }else{
            scaleValue = 1;
            scale_object.setVisibility(View.GONE);
        }
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