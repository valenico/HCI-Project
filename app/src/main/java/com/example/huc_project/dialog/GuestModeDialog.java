package com.example.huc_project.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.example.huc_project.R;
import com.example.huc_project.homepage.Homepage;

public class GuestModeDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_gues_mode)
                .setPositiveButton(R.string.guest_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Context context = getContext();
                        Intent intent = new Intent(context, Homepage.class);
                        intent.putExtra("guest", "true");
                        startActivity(intent);

                    }
                })
                .setNegativeButton(R.string.guest_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}