package com.example.fireconnect;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ChatCreationDialog extends AppCompatDialogFragment {

    View view;

    TextView chatName;
    Button createButton;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater =getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.chat_creation_dialog,null);

        chatName = view.findViewById(R.id.chat_name_edit_text);
        createButton = view.findViewById(R.id.create_button);

        createButton.setEnabled(false);

        chatName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = chatName.getText().toString().trim();
                createButton.setEnabled(!name.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChat(chatName.getText().toString().trim());
            }
        });

        builder.setView(view)
                .setTitle("Create Chat")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }

    public void createChat(String name){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference chatReference = db.collection("Chats").document(name);

        HashMap<String, Object> chatDetails = new HashMap<>();
        HashMap<String, Object> initialMessage = new HashMap<>();
        HashMap<String, Object> initialPath = new HashMap<>();

        chatDetails.put("Subject", name);
        chatDetails.put("Logo","");

        initialMessage.put("Message", name + " Chat Room Created Welcome");
        initialMessage.put("Sender", name );
        initialMessage.put("Time", FieldValue.serverTimestamp());
        initialMessage.put("isDownloadLink", "false");

        chatReference.set(chatDetails);
        chatReference.collection("Messages").add(initialMessage)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                });

        //current user is not added to participants

        chatReference.collection("MediaRef").add(initialPath)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                });

        ChatSelectionActivity m = (ChatSelectionActivity) getActivity();
        Intent intent = new Intent(m, ChatActivity.class);

        intent.putExtra("logged",m.getIntent().getStringExtra("logged"));
        intent.putExtra("username", m.getIntent().getStringExtra("Uname"));
        intent.putExtra("chat",name);
        startActivity(intent);

    }
}
