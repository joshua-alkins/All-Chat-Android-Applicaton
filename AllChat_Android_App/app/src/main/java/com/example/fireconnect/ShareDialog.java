package com.example.fireconnect;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ShareDialog extends AppCompatDialogFragment {

    View view;

    Button file;
    Button map;

    String chatName;
    String logged;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle arguments = getArguments();

        chatName = arguments.getString("Name");
        logged = arguments.getString("Sender");

        LayoutInflater inflater =getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.share_layout,null);

        file = view.findViewById(R.id.file_button);
        map = view.findViewById(R.id.map_button);

        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapsActivity();
            }
        });

        builder.setView(view)
                .setTitle("File Upload")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }

    public void openMapsActivity() {
        Intent intent = new Intent(getContext(), MapsActivity.class);
        startActivity(intent);
    }

    public void openDialog(){
        FileDialog dialog = new FileDialog();
        Bundle bundle = new Bundle();
        bundle.putString("Name",chatName);
        bundle.putString("Sender",logged);
        dialog.setArguments(bundle);
        dialog.show(getChildFragmentManager(),"file dialog");
    }

}
