package org.michiganhackers.photoassassin.LoginPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private ProgressBar progressBar;

    static final String ACCT_NOT_REGISTERED_YET = "account not registered yet";

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

        ServiceLoginHandler.Callback callback = new ServiceLoginHandler.Callback() {
            @Override
            public void onSuccess(@NonNull Task<AuthResult> task) {

                if (task.getResult() != null && task.getResult().getAdditionalUserInfo().isNewUser()) {
                    Log.i(TAG, "New service sign in");
                    deleteAccountAndGotoSetupProfile();
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(RuntimeException exception) {
                Snackbar.make(coordinatorLayout, R.string.login_failed, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "login cancelled");
            }
        };
        serviceLoginHandler = new ServiceLoginHandler(this, auth, callback);

        emailEditText = findViewById(R.id.text_input_edit_text_email);
        emailTextInputLayout = findViewById(R.id.text_input_layout_email);

        passwordEditText = findViewById(R.id.text_input_edit_text_password);
        passwordTextInputLayout = findViewById(R.id.text_input_layout_password);

        progressBar = findViewById(R.id.progress_bar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    public void onRegisterButtonClick(android.view.View view) {
        Intent intent = new Intent(this, SetupProfileActivity.class);
        startActivity(intent);
    }

    public void onForgotPasswordButtonClick(android.view.View view) {
        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivityForResult(intent, RESET_PASSWORD_REQUEST_CODE);
    }

    public void onLoginButtonClick(android.view.View view) {
        // Validate email and set error message
        boolean errorShown = false;
        if (emailEditText.getText() == null) {
            Log.e(TAG, "EditText getText() returned null");
            return;
        }
        Email email = new Email(emailEditText.getText().toString(), this);
        String errorMsg = email.getError();
        emailTextInputLayout.setError(errorMsg);
        errorShown = errorMsg != null;

        // Validate password and set error message
        if (passwordEditText.getText() == null) {
            Log.e(TAG, "EditText getText() returned null");
            return;
        }
        Password password = new Password(passwordEditText.getText().toString(), this);
        errorMsg = password.getError();
        passwordTextInputLayout.setError(errorMsg);
        if (errorMsg != null || errorShown) {
            return;
        }

        // Log in
        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email.getEmail(), password.getPassword())
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            Log.i(TAG, "Logged in successfully");
                        } else {
                            Exception exception = task.getException();
                            String msg = exception == null ? "" : ": " + exception.getLocalizedMessage();
                            Snackbar.make(coordinatorLayout, R.string.auth_failed_login, Snackbar.LENGTH_LONG).show();
                            Log.d(TAG, getString(R.string.auth_failed_login) + msg);
                        }
                    }
                });
    }

    public void onContinueWithGoogleButtonClick(android.view.View view) {
        serviceLoginHandler.onLoginGoogleButtonClick(view);
    }

    public void onContinueWithFacebookButtonClick(android.view.View view) {
        serviceLoginHandler.onLoginFacebookButtonClick(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        serviceLoginHandler.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESET_PASSWORD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Snackbar.make(coordinatorLayout, R.string.pwd_reset_confirmation, Snackbar.LENGTH_LONG).show();
            } else {
                Log.i(TAG, "RESET_PASSWORD_REQUEST_CODE cancelled");
            }
        }
    }

    private void deleteAccountAndGotoSetupProfile() {
        if (auth.getCurrentUser() == null) {
            Log.e(TAG, "Null user in deleteAccountAndGotoSetupProfile");
            return;
        }
        auth.getCurrentUser().delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        serviceLoginHandler.signOut();
                        Intent intent = new Intent(LoginActivity.this, SetupProfileActivity.class);
                        intent.putExtra(ACCT_NOT_REGISTERED_YET, true);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to delete account in deleteAccountAndGotoSetupProfile", e);
                    }
                });

    }
}
