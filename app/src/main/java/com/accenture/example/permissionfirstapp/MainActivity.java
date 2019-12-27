package com.accenture.example.permissionfirstapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity
        implements ExplainReadPhoneStateDialogFragment.OnOkClickListener {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final int OPEN_APP_SETTINGS_REQUEST_CODE = 102;
    private static final int READ_PHONE_STATE_REQUEST_CODE = 101;

    private boolean preReadPhoneStateRationale, postReadPhoneStateRationale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "-> onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkReadPhoneStatePermission();
    }

    private void checkReadPhoneStatePermission() {
        Log.v(LOG_TAG, "-> checkReadPhoneStatePermission");

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(LOG_TAG, "-> checkReadPhoneStatePermission -> READ_PHONE_STATE permission not granted");
            preReadPhoneStateRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    READ_PHONE_STATE_REQUEST_CODE);
        } else {
            Log.i(LOG_TAG, "-> checkReadPhoneStatePermission -> READ_PHONE_STATE permission granted");
            afterReadPhoneStatePermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v(LOG_TAG, "-> onRequestPermissionsResult");

        switch (requestCode) {
            case READ_PHONE_STATE_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(LOG_TAG, "-> onRequestPermissionsResult -> READ_PHONE_STATE permission granted");
                    afterReadPhoneStatePermissionGranted();
                } else {
                    Log.i(LOG_TAG, "-> onRequestPermissionsResult -> READ_PHONE_STATE permission denied");
                    postReadPhoneStateRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_PHONE_STATE);
                    Log.d(LOG_TAG, "-> onRequestPermissionsResult -> preReadPhoneStateRationale = " + preReadPhoneStateRationale +
                            ", postReadPhoneStateRationale = " + postReadPhoneStateRationale);
                    if ((preReadPhoneStateRationale && !postReadPhoneStateRationale) ||
                            (!preReadPhoneStateRationale && !postReadPhoneStateRationale)) {
                        Log.i(LOG_TAG, "-> Redirect to App Settings");
                        new RedirectToAppSettingsDialogFragment()
                                .show(getSupportFragmentManager(), RedirectToAppSettingsDialogFragment.LOG_TAG);
                    } else {
                        new ExplainReadPhoneStateDialogFragment()
                                .show(getSupportFragmentManager(), ExplainReadPhoneStateDialogFragment.LOG_TAG);
                    }
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(LOG_TAG, "-> onActivityResult");

        switch (requestCode) {
            case OPEN_APP_SETTINGS_REQUEST_CODE:
                checkReadPhoneStatePermission();
                return;
        }
    }

    private void afterReadPhoneStatePermissionGranted() {
        Log.v(LOG_TAG, "-> afterReadPhoneStatePermissionGranted");

        try {
            TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();
            TextView textView = findViewById(R.id.textViewMobileNumber);
            textView.setText(mPhoneNumber);
        } catch (SecurityException e) {
            Log.e(LOG_TAG, "-> afterReadPhoneStatePermissionGranted -> ", e);
        }
    }

    @Override
    public void onOkClick() {
        Log.v(LOG_TAG, "-> onOkClick");
        checkReadPhoneStatePermission();
    }
}