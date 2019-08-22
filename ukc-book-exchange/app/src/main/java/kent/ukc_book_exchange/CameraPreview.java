package kent.ukc_book_exchange;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * Class for camera handler
 *
 */

public class CameraPreview extends AppCompatActivity{

    //SurfaceView for camera preview
    SurfaceView cameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);

        cameraPreview = findViewById(R.id.cameraPreview);

        createCameraSource();
    }

    /**
     * Connects to phone camera and set barcoder.
     * Once barcode read, start sellBook activity
     */
    private void createCameraSource()
    {
        // Barcode Reader
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();

        //Set camera properties
        final CameraSource cameraSource = new CameraSource.Builder(this, barcodeDetector).setAutoFocusEnabled(true).setRequestedPreviewSize(2000,1034).build();

        //Start camera process
         cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            //Check phone camera permission of the app. If permission not granted, camera does not start
            if (ActivityCompat.checkSelfPermission(CameraPreview.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }

            //If permission granted, start camera
            try {cameraSource.start(cameraPreview.getHolder());
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            cameraSource.stop();
        }
    });

        //Start barcode detector
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
        @Override
        public void release() {

        }

        @Override
        public void receiveDetections(Detector.Detections<Barcode> detections) {

            //Get camera detection
            final SparseArray<Barcode> barcodes = detections.getDetectedItems();

            //Open sell_Book and pass it the barcode result if detection is successful
            if(barcodes.size() > 0){

                Intent intent = new Intent();
                intent.putExtra("barcode", barcodes.valueAt(0));

                setResult(CommonStatusCodes.SUCCESS,intent);
                finish();
            }
        }
    });

    }

}
