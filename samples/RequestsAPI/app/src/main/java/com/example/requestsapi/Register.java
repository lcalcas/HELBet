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

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private DBManager dbManager = DBManager.getInstance();

    private EditText etEmail;
    private EditText etPswd;
    private TextWatcher textWatcher;
    private Button bnSubmit;
    private ProgressBar progressBar;
    private TextView redirectLogin;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(Register.this, TestActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.input_email);
        etPswd = findViewById(R.id.input_pswd);
        bnSubmit = findViewById(R.id.button_submit);
        progressBar = findViewById(R.id.progressBar);
        redirectLogin = findViewById(R.id.redirect_register);

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

        bnSubmit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = etEmail.getText().toString();
                        String pswd = etPswd.getText().toString();

                        progressBar.setVisibility(View.VISIBLE);

                        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pswd)) {
                            mAuth.createUserWithEmailAndPassword(email, pswd).addOnCompleteListener(
                                    new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            progressBar.setVisibility(View.GONE);
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_LONG);

                                                String userId = task.getResult().getUser().getUid();
                                                User user = new User(email);
                                                user.setId(userId);
                                                dbManager.storeUser(user);

                                                Intent intent = new Intent(Register.this, Login.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_LONG);
                                            }
                                        }
                                    }
                            );
                        } else {
                            Toast.makeText(Register.this, "Some information is missing", Toast.LENGTH_LONG);
                            return;
                        }
                    }
                }
        );

        redirectLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Register.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );
    }
}