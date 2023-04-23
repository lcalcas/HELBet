package com.example.helbet;

import static com.example.helbet.PathRefs.USERS_PATHREF;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private AuthManager auth;
    private DBManager db;

    private ImageView back;
    private EditText emailInput;
    private EditText pswdInput;
    private ProgressBar progressBar;
    private Button submit;
    private TextView registerRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = AuthManager.getInstance();
        db = DBManager.getInstance();

        back = findViewById(R.id.back_activity);
        emailInput = findViewById(R.id.input_email);
        pswdInput = findViewById(R.id.input_password);
        progressBar = findViewById(R.id.progressBar);
        submit = findViewById(R.id.button_submit);
        registerRedirect = findViewById(R.id.register_redirect);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (auth.isAuthenticated()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        progressBar.setVisibility(View.INVISIBLE);
        submit.setText("Connexion");
        submit.setEnabled(false);

        AuthFormTextWatcher submit_textWatcher = editable -> {
            if (
                    emailInput.getText().toString().matches(AuthFormTextWatcher.regexEmail)
                            && pswdInput.getText().toString().matches(AuthFormTextWatcher.regexPassword)
            ) {
                submit.setEnabled(true);
            } else {
                submit.setEnabled(false);
            }
        };

        emailInput.addTextChangedListener(submit_textWatcher);
        pswdInput.addTextChangedListener(submit_textWatcher);

        submit.setOnClickListener(view -> {
            String email = emailInput.getText().toString();
            String pswd = pswdInput.getText().toString();

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pswd)) {
                auth.loginUser(email, pswd, authTask -> {
                    progressBar.setVisibility(View.VISIBLE);
                    if (authTask.isSuccessful()) {
                        String userId = authTask.getResult().getUser().getUid();
                        db.fetch(USERS_PATHREF, userId, User.class, new OnFetchCompleteListener<User>() {
                            @Override
                            public <T extends DBModel> void onFetchComplete(ArrayList<T> fetchResult) {
                                progressBar.setVisibility(View.GONE);
                                if (fetchResult.size() == 1) {
                                    User user = (User) fetchResult.get(0);
                                    user.setId(userId);

                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("user", user);

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Erreur !", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(this, "Erreur !", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } else {
                Toast.makeText(this, "Erreur !", Toast.LENGTH_LONG).show();
            }
        });

        back.setOnClickListener(view -> {
            finish();
        });

        registerRedirect.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}