package com.example.lenovo.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class Scanner extends AppCompatActivity {

    String value;
    private SurfaceView cameraPreview;
    CameraSource cameraSource;
    private Button validate,scanAgain;
    private BarcodeDetector barcodeDetector;
    final int RequestCameraPermissionID = 1001;
    private TextView roll,date,mealvalue,status;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        setVar();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(300, 480)
                .setAutoFocusEnabled(true)
                .build();

        validate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
                String parts[]=value.split(":");
                databaseReference.child(parts[0]).child(parts[1]).child(parts[2]).removeValue();
                roll.setText("- - - - - -");
                mealvalue.setText("- - - - - -");
                date.setText("- - - - - - -");
                status.setText("Scanning...");
                validate.setEnabled(false);
                if (ActivityCompat.checkSelfPermission(Scanner.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        scanAgain.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                roll.setText("- - - - - -");
                mealvalue.setText("- - - - - -");
                date.setText("- - - - - - -");
                status.setText("Scanning...");
                if (ActivityCompat.checkSelfPermission(Scanner.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Request permission
                    ActivityCompat.requestPermissions(Scanner.this,
                            new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
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

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if(qrcodes.size()!= 0){
                    status.post(new Runnable() {
                        @Override
                        public void run() {
                            final Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            assert vibrator != null;
                            vibrator.vibrate(500);
                            value = qrcodes.valueAt(0).displayValue;
                            if(value.contains("Breakfast")||value.contains("Lunch")||value.contains("Dinner")||value.contains("HighTea"))
                            {
                                final String parts[]=value.split(":");
                                roll.setText(parts[0]);
                                date.setText(parts[1]);
                                mealvalue.setText(parts[2]);

                                final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.child(parts[0]).child(parts[1]).child(parts[2]).exists())
                                        {
                                            status.setText("Valid QR");
                                            cameraSource.stop();
                                            vibrator.vibrate(250);
                                            validate.setEnabled(true);
                                        }
                                        else{
                                            status.setText("Invalid Or Used QR");
                                            vibrator.vibrate(250);
                                            cameraSource.stop();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                                });
                            }

                            else{
                                status.setText("Invalid QR");
                                vibrator.vibrate(250);
                                cameraSource.stop();
                            }
                        }
                    });
                }
            }
        });
    }

    private void setVar(){
        cameraPreview = findViewById(R.id.sv_camera);
        validate = findViewById(R.id.btn_Validate);
        scanAgain = findViewById(R.id.btn_scan_again);
        roll = findViewById(R.id.tv_u_roll);
        date = findViewById(R.id.tv_u_date);
        mealvalue = findViewById(R.id.tv_u_meal);
        status = findViewById(R.id.tv_status);
    }
}
