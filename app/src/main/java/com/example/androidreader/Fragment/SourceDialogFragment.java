package com.example.androidreader.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.androidreader.R;
import com.example.androidreader.SourceID;

public class SourceDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public AlertDialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        String [] source = getActivity().getResources().getStringArray(R.array.source);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Chọn nguồn");
        builder.setItems(source, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SourceID.ID = i;
                SourceID.source = source[i];
                Toast.makeText(getActivity(), SourceID.ID + " " + SourceID.source, Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }
}
