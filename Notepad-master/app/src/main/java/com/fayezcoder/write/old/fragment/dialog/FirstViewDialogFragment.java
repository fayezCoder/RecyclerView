
package com.fayezcoder.write.old.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;

import com.fayezcoder.write.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class FirstViewDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.first_view)
        .setTitle(R.string.app_name)
        .setPositiveButton(R.string.action_close, null);

        // Create the AlertDialog object and return it
        return builder.create();
    }
}