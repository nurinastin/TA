package com.example.installopencv;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
// implement camerabridggeview base agar tersambung ke kamera opencv kita
public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

//    private static String TAG = "MainActivity";
//    static  {
//        if (OpenCVLoader.initDebug()) {
//            Log.d(TAG, "OpenCV sukses di install");
//        }else {
//            Log.d(TAG, "OpenCV gagal di install");
//        }
//    }
//
//    static {
//        System.loadLibrary( "native-lib");
//    }
//      deklarasi kamerabridge
    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
//    inisialisasi start canny default dengan nilai false agar deteksi tepi dimatikan terlebih dahulu
    boolean startCanny = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        instannsiasi javakamera untuk menggunakan kamera di aplikasi. kamera menggunakan id CameraView di layout
        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.CameraView);

        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
//        mengaktifkan action di javacamera view
        cameraBridgeViewBase.setCvCameraViewListener(this);


        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);

                switch(status){
//                    jika success akan menampilkan kamera
                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }


            }

        };
    }

    public native String stringFromJNI();
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
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        instansiasi frame dengan format rgb
        Mat frame = inputFrame.rgba();

        if (startCanny == true) {
//            proses untuk mengganti kamera berwarna dengan grayscale. sehingga diketahui tepi tepi tiap object
            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGBA2GRAY);
            Imgproc.Canny(frame, frame, 100, 80);

        }



        return frame;
    }


    @Override
    public void onCameraViewStarted(int width, int height) {

    }


    @Override
    public void onCameraViewStopped() {

    }


    @Override
    protected void onResume() {
        super.onResume();
//        mendeteksi kesalahan opencvloader atau error sehingga error dapat ditampilkan
        if (!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"There's a problem, yo!", Toast.LENGTH_SHORT).show();
        }

        else
        {
//          mengkonekkan
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }



    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }
}
