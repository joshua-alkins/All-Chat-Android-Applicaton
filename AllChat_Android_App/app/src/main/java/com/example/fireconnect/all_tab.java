package com.example.fireconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class all_tab extends Fragment {

    private ListView allChatsListview;
    private ArrayList<String> allChats;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> messagesList;
    public String logged ;


    public all_tab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View all = inflater.inflate(R.layout.fragment_all_tab, container, false);
        list(all);

        return all;
    }

    public void list(View all){
        allChatsListview = all.findViewById(R.id.all_chat_list_view);
        allChats = new ArrayList<String>();
        messagesList = new ArrayList<>();
        populate();

        allChatsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = allChats.get(position);
                ChatSelectionActivity m = (ChatSelectionActivity) getActivity();
                HashMap<String,Object> activeChat = new HashMap<>();

                activeChat.put("ChatName",str);
                activeChat.put("TimeLastJoined", FieldValue.serverTimestamp());

                DocumentReference activeChatDocRef = db.collection("Users").document(m.getIntent().getStringExtra("logged")).collection("ActiveChats").document(str);

                activeChatDocRef.set(activeChat).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: ");
                    }
                });

                Intent myIntent = new Intent(m, ChatActivity.class);
                myIntent.putExtra("logged",m.getIntent().getStringExtra("logged"));
                myIntent.putExtra("username",m.getIntent().getStringExtra("Uname"));
                myIntent.putExtra("chat",str);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(myIntent);
                getActivity().finish();


                Toast.makeText(getActivity(),str,Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void msgPopulate(final ChatSelectionActivity m){
        CollectionReference colRef = db.collection("Chats").document(m.textview.getText().toString()).collection("Messages");

        final ArrayAdapter messagesAdapter = new ArrayAdapter((ChatSelectionActivity)getActivity(), android.R.layout.simple_expandable_list_item_1,messagesList);


        colRef.orderBy("Time", Query.Direction.DESCENDING).addSnapshotListener((ChatSelectionActivity)getActivity(),new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                m.messageListView.setAdapter(messagesAdapter);
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if(!queryDocumentSnapshots.getMetadata().hasPendingWrites() ){
                    List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                    for(DocumentChange documentChange: documentChangeList){
                        messagesList.add(documentChange.getDocument().get("Message").toString());
                        messagesAdapter.notifyDataSetChanged();
                    }

                }


            }
        });




    }


    public void populate(){


        CollectionReference colRef = db.collection("Chats");

        colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                ArrayAdapter T = new ArrayAdapter(getActivity(),android.R.layout.simple_expandable_list_item_1,allChats);
                allChatsListview.setAdapter(T);
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if(queryDocumentSnapshots != null){
                    List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                    for(DocumentChange documentChange: documentChangeList){
                        allChats.add(documentChange.getDocument().getId());
                        T.notifyDataSetChanged();
                    }
                }

            }
        });


    }

}


