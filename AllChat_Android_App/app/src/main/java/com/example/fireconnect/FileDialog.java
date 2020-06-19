package com.example.fireconnect;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import static android.app.Activity.RESULT_OK;


public class FileDialog extends AppCompatDialogFragment {
    private TextView Filename;
    private Button FileUpload;
    private Button FileSelect;
    private RadioGroup radioGroup;
    private RadioButton choice;
    private View view;
    private Uri MediaUri;
    private String Chatname;
    private String Sender;
    private String  url;
    private String info;
    private  FileDialogListener listener;
    private StorageReference fileLocation;


    private ProgressBar progressBar;
    private LinearLayout ll;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private Map<String , Object> initMR = new HashMap<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle arguments = getArguments();
         Chatname = arguments.getString("Name");
         Sender = arguments.getString("Sender");


        LayoutInflater inflater =getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.layout_dialog,null);
        Filename = view.findViewById(R.id.Filename);
        FileUpload = view.findViewById(R.id.Fupload);
        radioGroup = view.findViewById(R.id.RadioGroup1);
        FileSelect = view.findViewById(R.id.Select);
        ll = (LinearLayout) view.findViewById(R.id.DialogLayout);
        Filename.setText(Chatname);



        builder.setView(view)
                .setTitle("File Upload")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (url == null){}else{listener.applyTexts(url);}
                    }
                });
        FileSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermissions();
                findFile();
            }
        });

        FileUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MediaUri != null){
                    uploadFile(MediaUri);
                }else{
                    Toast.makeText(getActivity(),"Please Select a file",Toast.LENGTH_SHORT).show();
                }

            }
        });

        return builder.create();
    }

    public void findFile(){
        choice = view.findViewById(radioGroup.getCheckedRadioButtonId());

        Intent intent = new Intent();

        switch (choice.getText().toString().trim()){
            case ".pdf":
                intent.setType("application/pdf");


                break;
            case ".docx":
                intent.setType("docx/*");
                break;
            case ".png / .jpg":
                intent.setType("image/*");
                break;
            case ".mp3 / .mp4":
                intent.setType("audio/*");
                break;
            case ".mp4":
                intent.setType("video/*");
                break;
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,86);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 86 && resultCode == RESULT_OK && data!= null){
            MediaUri = data.getData();
            Filename.setText(MediaUri.getPath());
        }else{
            Toast.makeText(getActivity(),"Please Select a file",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            findFile();
        }else{
            Toast.makeText(getActivity(),"Please provide permissions",Toast.LENGTH_SHORT).show();
        }
    }

    public void CheckPermissions(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            findFile();
        }
        else{
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);}

    }
    public void uploadFile(Uri fileInfo){
        String storagename = System.currentTimeMillis()+"" ;
        final StorageReference storageReference = storage.getReference();


        progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setProgress(0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Width in pixels
                LinearLayout.LayoutParams.WRAP_CONTENT // Height of progress bar
        );
        progressBar.getProgressDrawable().setColorFilter(Color.rgb(216,27,96), PorterDuff.Mode.SRC_IN);
        progressBar.setLayoutParams(lp);
        ll.addView(progressBar);



        String name[]= MediaUri.getPath().split("/");
        fileLocation = storageReference.child(Chatname).child(storagename).child(name[name.length-1]);
        fileLocation.putFile(MediaUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

               // url = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                fileLocation.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        url = uri.toString();
                    }
                });

                final DocumentReference messageinfo = db.collection("Chats").document(Chatname).collection("MediaRef").document();
                initMR.clear();
                initMR.put("Paths", url);
                initMR.put("Time", FieldValue.serverTimestamp());

                DocumentReference docRef = db.collection("Users").document(Sender);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String test1 = document.getData().get("Username").toString();
                                initMR.put("Sender",test1);
                                messageinfo.set(initMR).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(),"Added to Chat",Toast.LENGTH_SHORT).show();
                                        ll.removeView(progressBar);
                                    }
                                });
                            }else{
                                Toast.makeText(getActivity(),"Error: Not Added",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Error: Not Uploaded",Toast.LENGTH_SHORT).show();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                int Current_Progress =(int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressBar.setProgress(Current_Progress);

            }
        });

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (FileDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement FileDialogListener");
        }
    }

    public interface FileDialogListener {
        void applyTexts(String Path);
    }
}
