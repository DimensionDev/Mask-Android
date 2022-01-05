package com.dimension.maskbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private MaterialToolbar mMaterialToolbar;
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(R.layout.activity_scanner);

        mMaterialToolbar = findViewById(R.id.scannerBar);
        setSupportActionBar(mMaterialToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FrameLayout container = (FrameLayout)findViewById(R.id.container);
        container.addView(mScannerView);

        Log.d("Qr Scanner", "onCreate"); // Prints scan results
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera(0);          // Start camera on resume
        Log.d("Qr Scanner", "onResume"); // Prints scan results
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
        Log.d("Qr Scanner", "onPause"); // Prints scan results
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.d("Qr Scanner", rawResult.getText()); // Prints scan results
        Log.d("Qr Scanner", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        // If you would like to resume scanning, call this method below:
        // mScannerView.resumeCameraPreview(this);

        Intent intent = new Intent();
        intent.putExtra("scanResult", rawResult.getText());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}