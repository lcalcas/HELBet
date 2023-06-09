package com.example.helbet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity {

    private EditText emailInput;
    private EditText pswdInput;
    private ProgressBar progressBar;
    private Button submit;
    private TextView loginRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        emailInput = findViewById(R.id.input_email);
        pswdInput = findViewById(R.id.input_password);
        progressBar = findViewById(R.id.progressBar); // chargement enregistrement
        submit = findViewById(R.id.button_submit);
        loginRedirect = findViewById(R.id.login_redirect);
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public String getCurrentTitle() {
        return "Enregistrement";
    }

    @Override
    public int getBottomNavSelectItemId() {
        return R.id.menu_profile;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void userLogged() {
        super.userLogged();

        goToMain();
    }

    @Override
    protected void userUnLogged() {
        super.userUnLogged();

        progressBar.setVisibility(View.INVISIBLE);
        submit.setText("Enregistrement");
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
            progressBar.setVisibility(View.VISIBLE);

            String email = emailInput.getText().toString();
            String pswd = pswdInput.getText().toString();

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pswd)) {
                auth.createUser(email, pswd, authTask -> {
                    if (authTask.isSuccessful()) {
                        User user = new User(email);
                        user.setId(authTask.getResult().getUser().getUid());
                        db.storeObject(user, Constants.DBPathRefs.USERS, dbTask -> {
                            progressBar.setVisibility(View.GONE);
                            if (dbTask.isSuccessful()) {
                                Toast.makeText(this, "Compte créé avec succès !", Toast.LENGTH_LONG);
                                Intent intent = new Intent(this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                authTask.getResult().getUser().delete();
                                Toast.makeText(this, "Erreur enregistrement bdd", Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        Toast.makeText(this, "Erreur enregistrement utilisateur", Toast.LENGTH_LONG);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } else {
                Toast.makeText(this, "Erreur champs vides", Toast.LENGTH_LONG);
            }
        });

        loginRedirect.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}