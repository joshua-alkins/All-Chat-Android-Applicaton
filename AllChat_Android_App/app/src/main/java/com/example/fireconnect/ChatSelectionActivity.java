package com.example.fireconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatSelectionActivity extends AppCompatActivity {

    private FirebaseFirestore db = null;
    private Map<String , Object> initM = new HashMap<>();
    private Map<String , Object> initP = new HashMap<>();
    private Map<String , Object> initMR = new HashMap<>();
    private Map<String , Object> details = new HashMap<>();
    private static final String TAG = "MainActivity";
    private Button createChatButton;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public TextView textview;
    private TabItem all_tab, active_tab;
    public PageAdapter pagerAdapter;

    public String str;


    EditText namefield;
    EditText msg;
    EditText subject;
    String logged ;
    String username;
    String First;
    ListView messageListView;

    Toolbar toolbar;

    private ArrayList<String> messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_selectiont);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("AllChat");

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
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

        db = FirebaseFirestore.getInstance();

/*
        Button view = findViewById(R.id.View);
        Button scans = findViewById(R.id.scanner);
        Button out = findViewById(R.id.logout);
        Button trans = findViewById(R.id.tranferb);
        Button thistory = findViewById(R.id.history);
        Button send = findViewById(R.id.Pay);
        Button chat = findViewById(R.id.Chat);

*/

        createChatButton = findViewById(R.id.create_chat_button);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        all_tab = findViewById(R.id.all_conversations_tab);
        active_tab = findViewById(R.id.active_conversations_tab);
        viewPager = findViewById(R.id.viewpager);
        //textview = findViewById(R.id.Name);


        pagerAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        messagesList = new ArrayList<>();

        messageListView = findViewById(R.id.messageListView);
        //msgSetup();



        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition()==0){
                    pagerAdapter.notifyDataSetChanged();
                }else if(tab.getPosition()==1){
                    pagerAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab){

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab){

            }

        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        logged = getIntent().getStringExtra("logged");
        username = getIntent().getStringExtra("Uname");

        createChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChatCreationDialog();
            }
        });


/*
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateChat();
            }
        });//used
        thistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onhistoryClicked();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });//used
        trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transfer();
            }
        });
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loggout();
            }
        });//used
*/
    }
/*
    public void onhistoryClicked(){
        Intent myIntent = new Intent(this, ChatActivity.class);
        myIntent.putExtra("logged",getLogged());
        startActivity(myIntent);
    }
    public void msgSetup(){
        final ArrayAdapter messagesAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1,messagesList);
        messageListView.setAdapter(messagesAdapter);
        messagesAdapter.clear();
        messagesAdapter.notifyDataSetChanged();

        CollectionReference colRef = db.collection("Chats").document(textview.getText().toString()).collection("Messages");

        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                messagesList.add(document.get("Message").toString());
                                messagesAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void transfer(){
        EditText code = findViewById(R.id.Code);
        amount = findViewById(R.id.transfer);
        final String vals = code.getText().toString();

        DocumentReference docRef = db.collection("users").document(vals);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG ,"DocumentSnapshot data: " + document.getData());
                        float test2 = Float.parseFloat(document.getData().get("Balance").toString());
                        float test3 = Float.parseFloat(amount.getText().toString());
                        float newbalance = test2+test3;
                        String round = String.format(Locale.US,"%.2f", newbalance);
                        db.collection("users").document(vals).update("Balance",round);
                        deduct();
                        addhistory();
                        Toast.makeText(getApplicationContext(), "Transfer has been Completed", Toast.LENGTH_LONG).show();


                    } else {
                        balance.setText("No such document");
                    }
                }
            }

        });
    }
    public void deduct(){
        amount = findViewById(R.id.transfer);
        DocumentReference docRef = db.collection("users").document(getLogged());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG ,"DocumentSnapshot data: " + document.getData());
                        float test2 = Float.parseFloat(document.getData().get("Balance").toString());
                        float test3 = Float.parseFloat(amount.getText().toString());
                        float newbalance = test2-test3;
                        String round = String.format(Locale.US,"%.2f", newbalance);
                        db.collection("users").document(getLogged()).update("Balance",round);
                    } else {
                        balance.setText("No such document");
                    }
                }
            }

        });

    }



    public void CreateChat(){

        namefield = findViewById(R.id.cname);
        String name = namefield.getText().toString();
        subject = findViewById(R.id.Csubject);



        details.put("Subject", subject.getText().toString());
        details.put("Logo"," ");

        initM.put("Message",name +" Chat Room Created");

        // Puts the  Create into the chats participants list
        DocumentReference docRef = db.collection("Users").document(getLogged());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String test1 = document.getData().get("Username").toString();
                        initP.put("Participant",test1);
                    }
                }
            }
        });

        initMR.put("Paths","");

        DocumentReference chatsinfo = db.collection("Chats").document(name);


        chatsinfo.set(details);

        chatsinfo.collection("Messages").add(initM)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Successfully added", Toast.LENGTH_LONG).show();
                    }
                });

        chatsinfo.collection("Participants").add(initP)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Successfully added", Toast.LENGTH_LONG).show();
                    }
                });

        chatsinfo.collection("MediaRef").add(initMR)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Successfully added", Toast.LENGTH_LONG).show();
                    }
                });


    }

    public void SendMessage(){
        msg = findViewById(R.id.Message);
        String chatname= textview.getText().toString();
        final DocumentReference messageinfo = db.collection("Chats").document(chatname).collection("Messages").document();
        initM.clear();
        initM.put("Message", msg.getText().toString());
        initM.put("Time",FieldValue.serverTimestamp());

        DocumentReference docRef = db.collection("Users").document(getLogged());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String test1 = document.getData().get("Username").toString();
                        initM.put("Sender",test1);
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
    public void setLogged(String var) {
        logged=var;

    }
    public String getLogged() {
        return logged;
    }

    public void addhistory(){
        EditText code = findViewById(R.id.Code);
        amount = findViewById(R.id.transfer);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
        final String formattedDate = df.format(c);

        final String vals = code.getText().toString();

        DocumentReference docRef = db.collection("users").document(getLogged());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String test2 = document.getData().get("history").toString();
                        String test3 = amount.getText().toString();
                        String his =test2+", "+formattedDate+": "+getLogged()+" Transfers $"+test3+" to "+vals;
                        db.collection("users").document(getLogged()).update("history",his);
                    } else {
                        balance.setText("No such document");
                    }
                }
            }

        });
    }

*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        Log.w(TAG,"Inflated");
        return true;
    }

    public void logout(){
        logged="";
        username="";
        Intent myIntent = new Intent(this, LoginActivity.class);
        startActivity(myIntent);
    }

    public void openChatCreationDialog(){
        ChatCreationDialog dialog = new ChatCreationDialog();
        dialog.show(getSupportFragmentManager(),"Chat Creation Dialog");
    }

}
