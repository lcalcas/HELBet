package com.example.requestsapi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.Duration;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private EditText etEmail;
    private EditText etPswd;
    private TextWatcher textWatcher;
    private Button bnSubmit;
    private Button bnGuest;
    private ProgressBar progressBar;
    private TextView redirectRegister;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(Login.this, LeaguesActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.input_email);
        etPswd = findViewById(R.id.input_pswd);
        bnSubmit = findViewById(R.id.button_submit);
        bnGuest = findViewById(R.id.button_enterGuest);
        progressBar = findViewById(R.id.progressBar);
        redirectRegister = findViewById(R.id.redirect_register);

        progressBar.setVisibility(View.INVISIBLE);
        bnSubmit.setEnabled(false);

        TextWatcher textWatcher_bnSubmit = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (
                        !etEmail.getText().toString().matches("")
                                && !etPswd.getText().toString().matches("")
                ) {
                    bnSubmit.setEnabled(true);
                } else {
                    bnSubmit.setEnabled(false);
                }
            }
        };

        etEmail.addTextChangedListener(textWatcher_bnSubmit);
        etPswd.addTextChangedListener(textWatcher_bnSubmit);

        OnCompleteListener<AuthResult> loginListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, TestActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Login.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        };

        bnSubmit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = etEmail.getText().toString();
                        String pswd = etPswd.getText().toString();

                        progressBar.setVisibility(View.VISIBLE);

                        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pswd)) {
                            mAuth.signInWithEmailAndPassword(email, pswd).addOnCompleteListener(loginListener);
                        } else {
                            Toast.makeText(Login.this, "Some information is missing", Toast.LENGTH_LONG);
                            return;
                        }
                    }
                }
        );

        bnGuest.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressBar.setVisibility(View.VISIBLE);

                        mAuth.signInAnonymously().addOnCompleteListener(loginListener);
                    }
                }
        );

        redirectRegister.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Login.this, Register.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );
    }
}