package com.example.installopencv;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Home extends AppCompatActivity {
    FloatingActionButton tambah, camera;
    int SELECT_IMAGE = 1;
    private Uri contentURI;
    Bitmap bitmap;
    boolean startCanny = false;
    BaseLoaderCallback baseLoaderCallback;
    ImageView hasil;
    String name = "";
    Scalar low, high;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tambah = findViewById(R.id.tambah);
        camera = findViewById(R.id.camera);
        OpenCVLoader.initDebug();
//        Log.d("appname", getApplicationInfo().loadLabel(getPackageManager()).toString());
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CustomPreview.class);
                intent.putExtra("file", name);
                startActivity(intent);

                startActivity(new Intent(getBaseContext(), CustomPreview.class));

            }
        });
        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
            }
        });
        hasil = findViewById(R.id.hasil);
        low = new Scalar(45,20,10);
        high = new Scalar(75,255,255);
        requestMultiplePermissions();
    }
    public void Canny(View Button){
//        ketika klik button canny dengan nilai default startcanny == false maka akan menggantikan nilai startcanny ke true untuk menghidupkan deteksi tepi pada aplikasi
        if (startCanny == false){
            startCanny = true;
        }

        else{
//            jika klik button kembali dengan nilai startcanny ==  true maka akan mengubah nilainya menjadi false
            startCanny = false;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == SELECT_IMAGE) {
            if (data != null) {
                contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    Log.d("lokasi", contentURI.toString());
//                    hasil.setImageBitmap(bitmap);
                    detectEdges(bitmap, contentURI);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private void SaveImage(Bitmap finalBitmap) {
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String image_name = "Image-"+ n;
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/"+getApplicationInfo().loadLabel(getPackageManager()).toString());
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        myDir.mkdirs();
        String fname = "Image-" + image_name + ".jpg";
        name = fname;
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.d("eror", e.getMessage());
            e.printStackTrace();
        }
    }
    private void detectEdges(Bitmap bmp, Uri contentURI) {
        Mat rgba = new Mat();
        Utils.bitmapToMat(bmp, rgba);

        Mat edges = new Mat(rgba.size(), CvType.CV_8UC1);
        Imgproc.cvtColor(rgba, edges, Imgproc.COLOR_RGB2GRAY, 100);
        Imgproc.Canny(edges, edges, 80, 120);
//        Imgproc.adaptiveThreshold(edges, edges, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 15, 4);

//        Core.inRange(rgba, low, high, edges);
        Core.bitwise_not(edges, edges);
//        Imgproc.GaussianBlur( edges, edges, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT );/
//        Imgproc.drawContours(rgba, contours, maxI, new Scalar(0, 255, 0), 5);
//        Imgproc.Canny(edges, edges, 2, 500, 7, true);
        // Don't do that at home or work it's for visualization purpose.
        Bitmap resultBitmap = Bitmap.createBitmap(edges.cols(), edges.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(edges, resultBitmap);


//        Core.convertScaleAbs( laplace, laplace );
        BitmapHelper.showBitmap(this, resultBitmap, hasil);
        SaveImage(resultBitmap);
    }
    private void requestMultiplePermissions(){

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
//                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
}
