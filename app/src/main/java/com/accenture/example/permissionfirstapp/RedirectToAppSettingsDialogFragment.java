package com.accenture.example.permissionfirstapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import static com.accenture.example.permissionfirstapp.MainActivity.OPEN_APP_SETTINGS_REQUEST_CODE;

public class RedirectToAppSettingsDialogFragment extends DialogFragment {

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
