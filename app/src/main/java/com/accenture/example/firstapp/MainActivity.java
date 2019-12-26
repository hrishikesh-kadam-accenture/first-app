package com.accenture.example.firstapp;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

interface OnOkClickListener {
    void onOkClick();
}

public class MainActivity extends AppCompatActivity implements OnOkClickListener {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int READ_PHONE_STATE_REQUEST_CODE = 101;
    private static final int OPEN_APP_SETTINGS_REQUEST_CODE = 102;

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
                    Log.i(LOG_TAG, "-> onRequestPermissionsResult -> READ_PHONE_STATE permission not granted");
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


    }

    @Override
    public void onOkClick() {
        Log.v(LOG_TAG, "-> onOkClick");
        checkReadPhoneStatePermission();
    }

    public static class ExplainReadPhoneStateDialogFragment extends DialogFragment {

        public static final String LOG_TAG = ExplainReadPhoneStateDialogFragment.class.getSimpleName();

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Log.v(LOG_TAG, "-> onCreateDialog");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Please allow Phone permission to work the app")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.v(LOG_TAG, "-> onCreateDialog -> OK");
                            if (getActivity() instanceof OnOkClickListener) {
                                ((OnOkClickListener) getActivity()).onOkClick();
                            }
                        }
                    });
            return builder.create();
        }
    }

    public static class RedirectToAppSettingsDialogFragment extends DialogFragment {

        public static final String LOG_TAG = RedirectToAppSettingsDialogFragment.class.getSimpleName();

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Log.v(LOG_TAG, "-> onCreateDialog");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Please allow Phone permission to work the app\n" +
                    "Click OK -> Permissions -> Allow Phone permissions")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.v(LOG_TAG, "-> onCreateDialog -> OK");

                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                            intent.setData(uri);
                            getActivity().startActivityForResult(intent, OPEN_APP_SETTINGS_REQUEST_CODE);
                        }
                    });

            return builder.create();
        }
    }
}