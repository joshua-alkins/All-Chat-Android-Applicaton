package com.example.fireconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Random;


public class RegisterActivity extends AppCompatActivity {

    FirebaseFirestore db;

    Toolbar toolbar;
    EditText username, password, confirmPassword;
    TextView logginCode;
    Button register;

    String logged;
    boolean canRegister = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = FirebaseFirestore.getInstance();



        toolbar = findViewById(R.id.toolbar);
        username = findViewById(R.id.username_editText);
        password = findViewById(R.id.password_editText);
        logginCode = findViewById(R.id.login_code_textView);
        confirmPassword = findViewById(R.id.confirm_password_editText);
        register = findViewById(R.id.register_button);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        generateLogged();

        register.setEnabled(canRegister);

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !confirmPassword.getText().toString().isEmpty()){
                    canRegister = true;
                }else{
                    canRegister = false;
                }
                register.setEnabled(canRegister);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !confirmPassword.getText().toString().isEmpty()){
                    canRegister = true;
                }else{
                    canRegister = false;
                }
                register.setEnabled(canRegister);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !confirmPassword.getText().toString().isEmpty()){
                    canRegister = true;
                }else{
                    canRegister = false;
                }
                register.setEnabled(canRegister);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    public void register(){
        String usernameText = username.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String confirmPasswordText = confirmPassword.getText().toString().trim();

        if(passwordText.equals(confirmPasswordText)){
            HashMap<String, Object> user = new HashMap<>();
            user.put("Password",passwordText);
            user.put("Username",usernameText);

            DocumentReference userDocRef = db.collection("Users").document(logged);
            userDocRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    String usernameText = username.getText().toString().trim();
                    Intent intent = new Intent(getApplicationContext(), ChatSelectionActivity.class);
                    intent.putExtra("logged",logged);
                    intent.putExtra("Uname",usernameText);
                    startActivity(intent);
                }
            });

        }else{
            Toast.makeText(this,"Passwords must match",Toast.LENGTH_SHORT);
        }


    }

    public void generateLogged(){

        logged = generateCode();
        logginCode.setText(logged);

    }

    private String generateCode(){
        Random rand = new Random();
        String logged;
        logged = "";

        for(int i=0; i<6; i++){
            logged += Integer.toString(rand.nextInt(10));
        }

        return logged;
    }

    public void backAction(){
        Intent intent = new Intent(this,LoginActivity.class);

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

