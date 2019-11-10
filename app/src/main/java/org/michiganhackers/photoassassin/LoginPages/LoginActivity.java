package org.michiganhackers.photoassassin.LoginPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.michiganhackers.photoassassin.Email;
import org.michiganhackers.photoassassin.MainActivity;
import org.michiganhackers.photoassassin.Password;
import org.michiganhackers.photoassassin.R;
import org.michiganhackers.photoassassin.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private TextInputLayout emailTextInputLayout, passwordTextInputLayout;
    private CoordinatorLayout coordinatorLayout;
    private FirebaseAuth auth;
    private final String TAG = getClass().getCanonicalName();
    private LoginHandler loginHandler;
    public static final int RESET_PASSWORD_REQUEST_CODE = 2;
    private ProgressBar progressBar;

    static final String ACCOUNT_NOT_REGISTERED_YET = "account not registered yet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupDefaultNotificationChannel();

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        coordinatorLayout = findViewById(R.id.coordinator_layout);


        loginHandler = new LoginHandler(this, auth, createLoginHandlerCallback());

        emailEditText = findViewById(R.id.text_input_edit_text_email);
        emailTextInputLayout = findViewById(R.id.text_input_layout_email);

        passwordEditText = findViewById(R.id.text_input_edit_text_password);
        passwordTextInputLayout = findViewById(R.id.text_input_layout_password);

        progressBar = findViewById(R.id.progress_bar);
    }

    private LoginHandler.Callback createLoginHandlerCallback() {
        return new LoginHandler.Callback() {
                @Override
                public void onSuccess(@NonNull Task<AuthResult> task) {
                    super.onSuccess(task);

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
                public void onFailure(Exception exception) {
                    Snackbar.make(coordinatorLayout, R.string.login_failed, Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void onCancel() {
                    Log.i(TAG, "login cancelled");
                }
            };
    }

    private void setupDefaultNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH));
        }
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
        Util.setTextInputLayoutErrorReclaim(emailTextInputLayout, errorMsg);
        errorShown = errorMsg != null;

        // Validate password and set error message
        if (passwordEditText.getText() == null) {
            Log.e(TAG, "EditText getText() returned null");
            return;
        }
        Password password = new Password(passwordEditText.getText().toString(), this);
        errorMsg = password.getError();
        Util.setTextInputLayoutErrorReclaim(passwordTextInputLayout, errorMsg);
        if (errorMsg != null || errorShown) {
            return;
        }

        loginHandler.onLoginEmailButtonClick(email.getEmail(), password.getPassword(), progressBar);
    }

    public void onContinueWithGoogleButtonClick(android.view.View view) {
        loginHandler.onLoginGoogleButtonClick();
    }

    public void onContinueWithFacebookButtonClick(android.view.View view) {
        loginHandler.onLoginFacebookButtonClick();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginHandler.onActivityResult(requestCode, resultCode, data);

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
                        loginHandler.signOut();
                        Intent intent = new Intent(LoginActivity.this, SetupProfileActivity.class);
                        intent.putExtra(ACCOUNT_NOT_REGISTERED_YET, true);
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
