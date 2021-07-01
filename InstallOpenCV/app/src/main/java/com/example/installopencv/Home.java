package com.example.installopencv;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import org.opencv.core.CvType;
import org.opencv.core.Mat;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tambah = findViewById(R.id.tambah);
        camera = findViewById(R.id.camera);
        OpenCVLoader.initDebug();
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentURI);
//                startActivity(intent);
                startActivity(new Intent(getBaseContext(), CustomPreview.class));

            }
        });
        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentURI);
//        startActivity(intent);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
            }
        });
        hasil = findViewById(R.id.hasil);
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

//        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(root + "/mySketch");
//        if (!myDir.exists()) {
//            myDir.mkdirs();
//        }
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String image_name = "Image-"+ n;
//        File file = new File (myDir, fname);
//        if (file.exists ())
//            file.delete ();
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
//            out.flush();
//            out.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d("eror", e.getMessage());
//        }
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/mySketch");
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
//        Imgproc.Canny(edges, edges, 2, 500, 7, true);
        // Don't do that at home or work it's for visualization purpose.
//        BitmapHelper.showBitmap(this, bitmap, hasil);
        Bitmap resultBitmap = Bitmap.createBitmap(edges.cols(), edges.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(edges, resultBitmap);

        BitmapHelper.showBitmap(this, resultBitmap, hasil);
        SaveImage(resultBitmap);

//        Bitmap bitmapOriginal = null;
//
//        bitmapOriginal = bmp;

//
//        imageView.setImageBitmap(resizeImage(this, bitmap, 400,400));
//
////        Bitmap bitmap = ((BitmapDrawable)imagemOriginal.getDrawable()).getBitmap();
//
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//
//        Bitmap bitmapResult = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//
//        Mat mat = new Mat();
//        Utils.bitmapToMat(bitmap, mat);
//
//        Imgproc.GaussianBlur( mat, mat, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT );
//
//        Mat gray = new Mat();
//        Imgproc.cvtColor( mat, gray, Imgproc.COLOR_RGB2GRAY );
//
//
//        Mat laplace = new Mat();
//        Imgproc.Laplacian( gray, laplace, CvType.CV_16S, 3, 1, 0, Core.BORDER_DEFAULT );
//
//        Core.convertScaleAbs( laplace, laplace );
//
//        Utils.matToBitmap(laplace, bitmapResult);
//        detectEdgesImageView.setImageBitmap(resizeImage(DetectEdgesActivity.this,bitmapResult,400,400));
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
