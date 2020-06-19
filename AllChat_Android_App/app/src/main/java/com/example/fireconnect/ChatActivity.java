package com.example.fireconnect;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements FileDialog.FileDialogListener, RecyclerViewAdapter.OnMessageSelectedListener {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Map<String , Object> user = null;
    private static final String TAG = "MainActivity";
    private Button button;

    RecyclerView messageRecyclerView;
    String logged ;
    String username;
    EditText msg;
    Button upload;
    //Button Download;
    ClipData myClip;
    //EditText Dlink;

    Toolbar toolbar;

    private Map<String , Object> initM = new HashMap<>();

    private  ArrayList<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getChat());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //button = (Button) findViewById(R.id.button);

        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapsActivity();
            }
        });*/

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "onMenuItemClick: "+item.getItemId());
                switch(item.getItemId()){
                    case R.id.settings_option:
                        Toast.makeText(getApplicationContext(),"Settings Selected",Toast.LENGTH_SHORT);
                        Log.w(TAG,"Settings Selected");
                        break;
                    case R.id.logout_option:
                        Toast.makeText(getApplicationContext(),"Logout",Toast.LENGTH_SHORT);
                        Log.w(TAG,"Log Out Selected");
                        logout();
                        break;
                }
                return false;
            }
        });



        messageRecyclerView = findViewById(R.id.chat_recycler_view);

        msg = findViewById(R.id.Message);
        upload = findViewById(R.id.Upload);
        //Download = findViewById(R.id.Download);
        //Dlink = findViewById(R.id.Dlink);

        logged = getIntent().getStringExtra("logged");
        username = getIntent().getStringExtra("username");
        Log.w("Logged test","Logged: "+ logged);
        messageList = new ArrayList<>();
        final Button send = findViewById(R.id.Send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage(msg);
                msg.setText("");
            }
        });


        //Download.setEnabled(false);
/*
        Dlink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String Msg = Dlink.getText().toString().trim();
                Download.setEnabled(!Msg.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        send.setEnabled(false);

        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String Msg = msg.getText().toString().trim();
                send.setEnabled(!Msg.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openDialog();
                openShareDialog();
            }
        });
/*
        Download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Dlink.getText().toString()));
                startActivity(browserIntent);
            }
        });*/


        /*
        messageListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                myClip = ClipData.newPlainText("text", messagesList.get(position));
                clipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(), "Copied", Toast.LENGTH_SHORT).show();
                return true;
            }
        });*/

    }

    public void openMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        populate();

    }


    public void back(){
        /*
        Intent myIntent = new Intent(this, ChatSelectionActivity.class);
        myIntent.putExtra("logged",getIntent().getStringExtra("logged"));
        startActivity(myIntent);*/
    }
/*
    public void displayChatname(String name){
        title = findViewById(R.id.Title);
        title.setText(name);
    }*/

    public String getLogged(){
        Log.d(TAG, "getLogged: reached");
        logged = getIntent().getStringExtra("logged");
        Log.d(TAG, "getLogged: ended");
        return logged;
    }


    public String getChat(){
        Log.d(TAG, "getChat: reached");
        String chat = getIntent().getStringExtra("chat");
        Log.d(TAG, "getChat: ended");
        return chat;
    }


    public void populate(){
        CollectionReference colRef = db.collection("Chats").document(getChat()).collection("Messages");

        DocumentReference docRef = db.collection("Users").document(getLogged());

        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, this, messageList, username);

        colRef.orderBy("Time", Query.Direction.ASCENDING).addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                messageRecyclerView.setAdapter(adapter);

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setStackFromEnd(true);
                messageRecyclerView.setLayoutManager(linearLayoutManager);

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if(!queryDocumentSnapshots.getMetadata().hasPendingWrites() ){
                    List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                    for(DocumentChange documentChange: documentChangeList){
                        String mes = "";

                        String author = documentChange.getDocument().get("Sender").toString();
                        String text = documentChange.getDocument().get("Message").toString();
                        boolean isLink;

                        try{
                            String isDownloadLink = documentChange.getDocument().get("isDownloadLink").toString();

                            if(isDownloadLink.equals("true")){
                                isLink = true;
                            }else{
                                isLink = false;
                            }
                        }catch(Exception exception){
                            isLink = false;
                        }

                        messageList.add(new Message(author,text,isLink));

                        adapter.notifyDataSetChanged();
                    }
                }

            }
        });
    }

    public void SendMessage(EditText msg){

        String chatname= getChat();
        final DocumentReference messageinfo = db.collection("Chats").document(chatname).collection("Messages").document();
        initM.clear();
        initM.put("Message", msg.getText().toString());
        initM.put("Time", FieldValue.serverTimestamp());
        initM.put("userID",logged);
        initM.put("isDownloadLink", "false");

        DocumentReference docRef = db.collection("Users").document(getLogged());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String username= document.getData().get("Username").toString();
                        initM.put("Sender",username);
                        messageinfo.set(initM).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Successfully added", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });

    }

    public void openDialog(){
        FileDialog dialog = new FileDialog();
        Bundle bundle = new Bundle();
        bundle.putString("Name","test11");
        bundle.putString("Sender",getLogged());
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(),"file dialog");

    }

    public void openShareDialog(){
        ShareDialog dialog = new ShareDialog();
        Bundle bundle = new Bundle();
        bundle.putString("Name",getChat());
        bundle.putString("Sender",getLogged());
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "share dialog");
    }

    @Override
    public void applyTexts(String Path) {
        //takes path from dialog and puts in list view
        if (Path != null) {
            String chatname = getChat();
            final DocumentReference messageinfo = db.collection("Chats").document(chatname).collection("Messages").document();
            initM.clear();
            initM.put("Message", Path);
            initM.put("Time", FieldValue.serverTimestamp());
            initM.put("isDownloadLink", "true");

            DocumentReference docRef = db.collection("Users").document(getLogged());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String test1 = document.getData().get("Username").toString();
                            initM.put("Sender", test1);
                            messageinfo.set(initM).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "Successfully added", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        Log.w(TAG,"Inflated");
        return true;
    }

    @Override
    public void onItemClick(int position) {
        //on item selected functionality
        //position is position of item that is selected

        Message message = messageList.get(position);

        if (message.isDownloadLink()){

            Log.d(TAG, "onItemClick: isDownloadLink: reached");
            
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getText().toString()));
            startActivity(browserIntent);

        }else{
            Log.d(TAG, "onItemClick: message long clicked");

            final ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);

            myClip = ClipData.newPlainText("text", messageList.get(position).getText());
            clipboard.setPrimaryClip(myClip);
            Toast.makeText(getApplicationContext(), "Copied", Toast.LENGTH_SHORT).show();
        }

    }

    public void logout(){
        logged="";
        username="";
        Intent myIntent = new Intent(this, LoginActivity.class);
        startActivity(myIntent);
    }

    public void backAction(){
        Intent intent = new Intent(this,ChatSelectionActivity.class);
        intent.putExtra("logged",logged);
        intent.putExtra("Uname",username);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            backAction();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            backAction();
        }

        return super.onOptionsItemSelected(item);
    }
}
