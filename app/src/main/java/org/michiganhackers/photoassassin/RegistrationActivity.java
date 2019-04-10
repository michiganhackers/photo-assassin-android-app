package org.michiganhackers.photoassassin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private TextInputLayout emailTextInputLayout, passwordTextInputLayout;
    private CoordinatorLayout coordinatorLayout;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private final static int REQUEST_CODE_GOOGLE_SIGN_IN = 1;
    private final String TAG = getClass().getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailEditText = findViewById(R.id.text_input_edit_text_email);
        emailTextInputLayout = findViewById(R.id.text_input_layout_email);

        passwordEditText = findViewById(R.id.text_input_edit_text_password);
        passwordTextInputLayout = findViewById(R.id.text_input_layout_password);

        coordinatorLayout = findViewById(R.id.coordinator_layout);

        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO: Not sure if this check should be here or just in login activity. Would also need to check firebase auth and fb login.
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            Log.w(TAG, "User already signed in with google");
            googleSignInClient.signOut(); // TODO: this is just for debugging
        }
    }

    public void onRegisterButtonClick(android.view.View view) {
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

        // Register user
        auth.createUserWithEmailAndPassword(email.getEmail(), password.getPassword())
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Account successfully created", Toast.LENGTH_LONG).show(); //TODO: remove
                            Log.d(TAG, "Account successfully created");
                        } else {
                            Exception exception = task.getException();
                            String msg = exception == null ? "" : ": " + exception.getLocalizedMessage();
                            Snackbar.make(coordinatorLayout, getString(R.string.auth_failed_registration) + msg, Snackbar.LENGTH_LONG).show();
                            Log.d(TAG, getString(R.string.auth_failed_registration) + msg);
                        }
                    }
                });


    }

    private void onLoginButtonClick() {
        finish();
    }

    // TODO: link google, fb, and email accounts to same user: https://firebase.google.com/docs/auth/android/account-linking?authuser=0
    // TODO: move google sign in stuff to another file so login page can use it
    public void onRegisterGoogleButtonClick(android.view.View view) {
        Intent googleSignInClientSignInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(googleSignInClientSignInIntent, REQUEST_CODE_GOOGLE_SIGN_IN);
    }

    private void onRegisterFacebookButtonClick() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account == null) {
                    Snackbar.make(coordinatorLayout, getString(R.string.failed_google_sign_in_message), Snackbar.LENGTH_LONG).show();
                    Log.d(TAG, "null GoogleSignInAccount");
                } else {
                    authenticateWithGoogle(account);
                }
            } catch (ApiException e) {
                Snackbar.make(coordinatorLayout, getString(R.string.failed_google_sign_in_message), Snackbar.LENGTH_LONG).show();
                Log.d(TAG, e.getMessage());
            }
        }
    }

    private void authenticateWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Google sign in successful", Toast.LENGTH_LONG).show(); //TODO: remove
                            Log.d(TAG, "Google sign in successful");
                        } else {
                            Exception exception = task.getException();
                            String msg = exception == null ? "" : ": " + exception.getLocalizedMessage();
                            Snackbar.make(coordinatorLayout, getString(R.string.failed_google_sign_in_message) + msg, Snackbar.LENGTH_LONG).show();
                            Log.d(TAG, getString(R.string.failed_google_sign_in_message) + msg);
                        }
                    }
                });
    }
}