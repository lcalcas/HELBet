package com.example.helbet;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {
    private static AuthManager singleton;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    private AuthManager() {
        this.mAuth = FirebaseAuth.getInstance();
    }


    public synchronized static AuthManager getInstance() {
        if (singleton == null) {
            singleton = new AuthManager();
        }

        return singleton;
    }

    public FirebaseUser getUser() {
        this.mUser = mAuth.getCurrentUser();
        return mUser;
    }

    public boolean isAuthenticated() {
        return getUser() != null;
    }

    public void createUser(String email, String pswd, OnCompleteListener<AuthResult> listener) {
        mAuth.createUserWithEmailAndPassword(email, pswd).addOnCompleteListener(listener);
    }

    public void loginUser(String email, String pswd, OnCompleteListener<AuthResult> listener) {
        mAuth.signInWithEmailAndPassword(email, pswd).addOnCompleteListener(listener);
    }
}

interface AuthFormTextWatcher extends TextWatcher {

    String regexEmail = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    @Override
    default void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {};

    @Override
    default void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {};

    @Override
    void afterTextChanged(Editable edit);
}
