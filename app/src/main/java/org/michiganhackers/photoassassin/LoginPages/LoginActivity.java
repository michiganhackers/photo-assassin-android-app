package org.michiganhackers.photoassassin.LoginPages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.michiganhackers.photoassassin.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private TextInputLayout emailTextInputLayout, passwordTextInputLayout;
    private CoordinatorLayout coordinatorLayout;
    private FirebaseAuth auth;
    private final String TAG = getClass().getCanonicalName();
    private ServiceLoginHandler serviceLoginHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        serviceLoginHandler = new ServiceLoginHandler(this, auth, coordinatorLayout);

        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.text_input_edit_text_email);
        emailTextInputLayout = findViewById(R.id.text_input_layout_email);

        passwordEditText = findViewById(R.id.text_input_edit_text_password);
        passwordTextInputLayout = findViewById(R.id.text_input_layout_password);

    }

    public void onRegisterButtonClick(android.view.View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void onLoginButtonClick(android.view.View view) {
        //TODO
    }

    public void onContinueWithGoogleButtonClick(android.view.View view) {
        serviceLoginHandler.onRegisterGoogleButtonClick(view);
    }

    public void onContinueWithFacebookButtonClick(android.view.View view) {
        serviceLoginHandler.onRegisterFacebookButtonClick(view);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        serviceLoginHandler.onActivityResult(requestCode, resultCode, data);
    }
}
