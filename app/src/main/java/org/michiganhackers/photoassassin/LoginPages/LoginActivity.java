package org.michiganhackers.photoassassin.LoginPages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import org.michiganhackers.photoassassin.R;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    //TODO: startActivityForResult instead and return to this activity from RegistrationActivity instead of going directly to MainActivity
    public void onRegisterButtonClick(android.view.View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }
}
