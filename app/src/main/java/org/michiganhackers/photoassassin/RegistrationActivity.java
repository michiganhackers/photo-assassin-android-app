package org.michiganhackers.photoassassin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private TextInputLayout emailTextInputLayout, passwordTextInputLayout;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailEditText = findViewById(R.id.text_input_edit_text_email);
        emailTextInputLayout = findViewById(R.id.text_input_layout_email);

        passwordEditText = findViewById(R.id.text_input_edit_text_password);
        passwordTextInputLayout = findViewById(R.id.text_input_layout_password);

        auth = FirebaseAuth.getInstance();
    }

    public void onRegisterButtonClick(android.view.View view) {
        Toast.makeText(this, "TEST", Toast.LENGTH_LONG).show();
        // Validate email and set error message
        if (emailEditText.getText() == null) {
            return;
        }
        Email email = new Email(emailEditText.getText().toString(), this);
        String errorMsg = email.getError();
        emailTextInputLayout.setError(errorMsg);
        if (errorMsg == null) {
            return;
        }

        // Validate password and set error message
        if (passwordEditText.getText() == null) {
            return;
        }
        Password password = new Password(passwordEditText.getText().toString(), this);
        errorMsg = password.getError();
        passwordTextInputLayout.setError(errorMsg);
        if (errorMsg == null) {
            return;
        }

        // Register user
        auth.createUserWithEmailAndPassword(email.getEmail(), password.getPassword())
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Exception exception = task.getException();
                            String msg = exception == null ? "" : ": " + exception.getLocalizedMessage();
                            Toast.makeText(RegistrationActivity.this, msg, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegistrationActivity.this, "CREATED ACCOUNT SUCCESS", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    private void onLoginButtonClick() {

    }

    private void onRegisterGoogleButtonClick() {

    }

    private void onRegisterFacebookButtonClick() {

    }
}
