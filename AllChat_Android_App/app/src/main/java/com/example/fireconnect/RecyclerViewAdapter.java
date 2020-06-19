package com.example.fireconnect;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<Message> messages;

    private OnMessageSelectedListener mOnMessageSelectedListener;

    private String logged;

    public RecyclerViewAdapter(Context context, OnMessageSelectedListener onMessageSelectedListener, ArrayList<Message> messages, String logged){
        this.mOnMessageSelectedListener = onMessageSelectedListener;
        this.messages = messages;
        this.logged = logged;
        Log.w(TAG,"Logged: "+logged);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.w(TAG, "onCreateViewHolder: called");

        if(viewType==0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_other, parent, false);
            ViewHolder holder = new ViewHolder(view, mOnMessageSelectedListener);
             return holder;
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_user, parent, false);
            ViewHolder holder = new ViewHolder(view, mOnMessageSelectedListener);
             return holder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder called");
        holder.message.setText(messages.get(position).getText());
        holder.author.setText(messages.get(position).getAuthor());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        TextView author, message;
        OnMessageSelectedListener onMessageSelectedListener;

        public ViewHolder(@NonNull View itemView, OnMessageSelectedListener onMessageSelectedListener) {
            super(itemView);
            author = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
            this.onMessageSelectedListener = onMessageSelectedListener;

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            onMessageSelectedListener.onItemClick(getAdapterPosition());
            return true;
        }
    }


    @Override
    public int getItemViewType(int position) {
        Log.w(TAG,"Logged value: "+logged);
        Log.w(TAG,"Message Author value: "+messages.get(position).getAuthor());

        if(logged.trim().equals(messages.get(position).getAuthor().trim())){
            Log.w(TAG,"Users are equal: True");
            return 1;
        }else{
            Log.w(TAG,"Users are equal: Failed");
            return 0;
        }
    }


    public interface OnMessageSelectedListener {
        void onItemClick(int position);
    }

}
