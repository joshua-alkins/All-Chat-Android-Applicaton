package com.example.fireconnect;

import android.app.Activity;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class active_tab extends Fragment {

    private ListView activeChatsListView;
    private ArrayList<String> activeChats;

    String logged;

    FirebaseFirestore db;

    public active_tab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View active = inflater.inflate(R.layout.fragment_active_tab, container, false);

        db = FirebaseFirestore.getInstance();

        activeChatsListView = active.findViewById(R.id.active_chat_list_view);
        /*
        activeChats = new ArrayList<String>();
        ArrayAdapter activeChatsAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_expandable_list_item_1,activeChats);

        activeChatsListView.setAdapter(activeChatsAdapter);
*/

        list(active);


        return active;
    }

    public void list(View all){
        activeChatsListView = all.findViewById(R.id.active_chat_list_view);
        activeChats = new ArrayList<String>();
        populate();

        activeChatsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = activeChats.get(position);
                ChatSelectionActivity m = (ChatSelectionActivity) getActivity();
                HashMap<String,Object> activeChat = new HashMap<>();

                activeChat.put("ChatName",str);
                activeChat.put("TimeLastJoined", FieldValue.serverTimestamp());

                //m.str = str;
                //m.textview.setText(str);
                //msgPopulate(m);

                DocumentReference activeChatDocRef = db.collection("Users").document(m.getIntent().getStringExtra("logged")).collection("ActiveChats").document(str);

                activeChatDocRef.set(activeChat);

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

    public void populate(){

        ChatSelectionActivity m = (ChatSelectionActivity) getActivity();

        CollectionReference colRef = db.collection("Users").document(m.getIntent().getStringExtra("logged")).collection("ActiveChats");

        activeChats.clear();

        colRef.orderBy("TimeLastJoined", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayAdapter T = new ArrayAdapter(getContext(),android.R.layout.simple_expandable_list_item_1,activeChats);
                    activeChatsListView.setAdapter(T);
                    for(QueryDocumentSnapshot document : task.getResult()){
                        try{
                            activeChats.add(document.getId());
                            T.notifyDataSetChanged();
                        }catch(Exception exception){

                        }

                    }

                }else{

                }
            }
        });
/*
        colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                Log.d(TAG, "onEvent: got Activity");
                ArrayAdapter T = new ArrayAdapter(getContext(),android.R.layout.simple_expandable_list_item_1,activeChats);
                activeChatsListView.setAdapter(T);
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if(queryDocumentSnapshots != null){
                    List<DocumentChange> documentChangeList = queryDocumentSnapshots.getDocumentChanges();
                    for(DocumentChange documentChange: documentChangeList){
                        activeChats.add(documentChange.getDocument().getId());
                        T.notifyDataSetChanged();
                    }
                }

            }
        });
*/

    }

}

