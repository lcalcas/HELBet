package com.example.helbet;

import android.content.Intent;

public abstract class BaseUserLoggedActivity extends BaseActivity {
    @Override
    protected void userUnLogged() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finishAffinity();
    }
}
