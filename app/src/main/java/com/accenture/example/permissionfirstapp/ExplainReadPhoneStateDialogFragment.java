package com.accenture.example.permissionfirstapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ExplainReadPhoneStateDialogFragment extends DialogFragment {

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

    interface OnOkClickListener {
        void onOkClick();
    }
}
