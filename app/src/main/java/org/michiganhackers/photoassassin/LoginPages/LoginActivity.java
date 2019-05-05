package org.michiganhackers.photoassassin.LoginPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.michiganhackers.photoassassin.MainActivity;
import org.michiganhackers.photoassassin.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private TextInputLayout emailTextInputLayout, passwordTextInputLayout;
    private CoordinatorLayout coordinatorLayout;
    private FirebaseAuth auth;
    private final String TAG = getClass().getCanonicalName();
    private ServiceLoginHandler serviceLoginHandler;
    public static final int RESET_PASSWORD_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        serviceLoginHandler = new ServiceLoginHandler(this, auth, coordinatorLayout);


        emailEditText = findViewById(R.id.text_input_edit_text_email);
        emailTextInputLayout = findViewById(R.id.text_input_layout_email);

        passwordEditText = findViewById(R.id.text_input_edit_text_password);
        passwordTextInputLayout = findViewById(R.id.text_input_layout_password);

    }

    public void onRegisterButtonClick(android.view.View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void onForgotPasswordButtonClick(android.view.View view) {
        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivityForResult(intent, RESET_PASSWORD_REQUEST_CODE);
    }

    public void onLoginButtonClick(android.view.View view) {
        // Validate email and set error message
        if (emailEditText.getText() == null) {
            return;
        }
        Email email = new Email(emailEditText.getText().toString(), this);
        String errorMsg = email.getError();
        emailTextInputLayout.setError(errorMsg);
        if (errorMsg != null) {
            return;
        }

        // Validate password and set error message
        if (passwordEditText.getText() == null) {
            return;
        }
        Password password = new Password(passwordEditText.getText().toString(), this);
        errorMsg = password.getError();
        passwordTextInputLayout.setError(errorMsg);
        if (errorMsg != null) {
            return;
        }

        // Log in
        auth.signInWithEmailAndPassword(email.getEmail(), password.getPassword())
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            Log.d(TAG, "Logged in successfully");
                        } else {
                            Exception exception = task.getException();
                            String msg = exception == null ? "" : ": " + exception.getLocalizedMessage();
                            Snackbar.make(coordinatorLayout, getString(R.string.auth_failed_login) + msg, Snackbar.LENGTH_LONG).show();
                            Log.d(TAG, getString(R.string.auth_failed_login) + msg);
                        }
                    }
                });
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
        if (requestCode == RESET_PASSWORD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Snackbar.make(coordinatorLayout, getString(R.string.pwd_reset_confirmation), Snackbar.LENGTH_LONG).show();
            } else {
                Log.w(TAG, "RESET_PASSWORD_REQUEST_CODE cancelled");
            }
        }
    }
}
