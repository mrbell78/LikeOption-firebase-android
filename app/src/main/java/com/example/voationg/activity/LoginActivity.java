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
import android.widget.TextView;
import android.widget.Toast;

import com.example.voationg.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    Button btn_Login;
    EditText loginEmail,loginPassword;
    FirebaseAuth mAuth;
    ProgressDialog mDialog;
    DatabaseReference mDatabase;
    TextView tv_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_Login=findViewById(R.id.btnLogin);
        mAuth=FirebaseAuth.getInstance();
        tv_register=findViewById(R.id.gotoRegister);


        loginEmail=findViewById(R.id.inputEmail);
        loginPassword=findViewById(R.id.inputPassword);
        mDialog= new ProgressDialog(this);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("User");
        mDialog.setTitle("Login");
        mDialog.setMessage("Please wait...............");

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=loginEmail.getText().toString();
                String pasword=loginPassword.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pasword)){

                   mDialog.show();

                    mAuth.signInWithEmailAndPassword(email,pasword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){



                                Toast.makeText(LoginActivity.this, "Congrats! u signed in", Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                                sendTomain();

                            }
                            else {
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Login Failed "+ message, Toast.LENGTH_SHORT).show();
                               mDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });


        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser =mAuth.getCurrentUser();
        if(currentUser!=null){
            sendTomain();
        }
    }

    private void sendTomain() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}