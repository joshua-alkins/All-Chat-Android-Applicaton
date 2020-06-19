package com.example.fireconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;


public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db = null;
    private Map<String, Object> user = null;
    private static final String TAG = "MainActivity";
    String logged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button login = findViewById(R.id.log);
        Switch toggle = findViewById(R.id.Toggle);

        db = FirebaseFirestore.getInstance();
        checkTheme(toggle);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }


            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();

            }
        });


    }

    public void login() {
        EditText lcode = findViewById(R.id.lCode);
        final String nval = lcode.getText().toString();
        final EditText pass = findViewById(R.id.password);
        final Intent myIntent = new Intent(this, ChatSelectionActivity.class);

        DocumentReference docRef = db.collection("Users").document(nval);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String test2 = document.getData().get("Password").toString();
                        String test3 = pass.getText().toString();
                        Log.d(TAG, "DocumentSnapshot data: test3" + test3);
                        if (test3.equals(test2)) {
                            setLogged(nval);
                            createNotification("Welcome to All Chat enjoy your stay :)");
                            Toast.makeText(getApplicationContext(), "Logged IN", Toast.LENGTH_LONG).show();
                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(),R.raw.ding);
                            mp.start();
                            myIntent.putExtra("logged", getLogged());
                            startActivity(myIntent);

                        } else {
                            Toast.makeText(getApplicationContext(), "ERROR: Incorrect Password", Toast.LENGTH_LONG).show();


                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "ERROR: Incorrect Username", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void createNotification(String msg){

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(R.color.colorAccent);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_settings_black_24dp)
                .setContentTitle("AllChat")
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        notificationManager.notify(1, notificationBuilder.build());

    }

    public void setLogged(String var) {
        logged = var;
    }

    public String getLogged() {
        return logged;
    }

    public void checkTheme(Switch toggle){
        int nightModeFlags = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                toggle.setChecked(true);
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                toggle.setChecked(false);
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                toggle.setChecked(false);
                break;
        }
    }



    public void createUser() {
        user = new HashMap<>();
        user.put("Code", 59081);
        user.put("Name", "joey");
        db.collection("User")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Successfully added", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR: Not Successfully added", Toast.LENGTH_LONG).show();

                    }
                });

    }
}
