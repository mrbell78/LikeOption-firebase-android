package com.example.voationg.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.voationg.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    Button btn_CreateAccount;
    EditText inputEmail,inputPassword,inputConfirmPassword,inputName;
    FirebaseAuth mAuth;
    ProgressDialog mDialog;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        inputName=findViewById(R.id.inputname);
        inputEmail=findViewById(R.id.inputEmail);
        inputPassword=findViewById(R.id.inputPassword);
        inputConfirmPassword=findViewById(R.id.inputConfirmPassword);
        btn_CreateAccount=findViewById(R.id.btn_registration);
        mAuth=FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("User");
        mDialog=new ProgressDialog(this);
        mDialog.setTitle("Registration");
        mDialog.setMessage("Please wait......");




        btn_CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputEmail.getText().toString();
                String password=inputPassword.getText().toString();
                String confirmpass=inputConfirmPassword.getText().toString();
                final String name = inputName.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmpass)&& !TextUtils.isEmpty(name)){

                    if(password.equals(confirmpass)){
                        mDialog.show();
                        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    Map userdata = new HashMap<>();
                                    userdata.put("name",name);
                                    userdata.put("image","default");

                                    mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(userdata).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if(task.isSuccessful()){
                                                sendTomain();
                                                Toast.makeText(RegistrationActivity.this, "Registration Succesful", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });


                                }else{
                                    String messatge = task.getException().toString();

                                    Toast.makeText(RegistrationActivity.this, "Registration Failed "+messatge, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{

                        Toast.makeText(RegistrationActivity.this, "passwords doesnt match", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                }
            }
        });
    }


    private void sendTologin() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            sendTomain();
        }
    }

    private void sendTomain() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}