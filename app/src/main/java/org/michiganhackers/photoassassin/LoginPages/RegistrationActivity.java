package org.michiganhackers.photoassassin.LoginPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.UploadTask;

import org.michiganhackers.photoassassin.Constants;
import org.michiganhackers.photoassassin.Email;
import org.michiganhackers.photoassassin.MainActivity;
import org.michiganhackers.photoassassin.Password;
import org.michiganhackers.photoassassin.R;
import org.michiganhackers.photoassassin.User;
import org.michiganhackers.photoassassin.Util;

import java.util.HashMap;
import java.util.Map;

import static org.michiganhackers.photoassassin.LoginPages.SetupProfileActivity.DISPLAY_NAME;
import static org.michiganhackers.photoassassin.LoginPages.SetupProfileActivity.DUPLICATE_USERNAME;
import static org.michiganhackers.photoassassin.LoginPages.SetupProfileActivity.PROFILE_PIC_URI;
import static org.michiganhackers.photoassassin.LoginPages.SetupProfileActivity.USERNAME;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private TextInputLayout emailTextInputLayout, passwordTextInputLayout;
    private CoordinatorLayout coordinatorLayout;
    private FirebaseAuth auth;
    private final String TAG = getClass().getCanonicalName();
    private LoginHandler loginHandler;
    private ProgressBar progressBar;

    public static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();
        coordinatorLayout = findViewById(R.id.coordinator_layout);

        loginHandler = new LoginHandler(this, auth, createLoginHandlerCallback());

        emailEditText = findViewById(R.id.text_input_edit_text_email);
        emailTextInputLayout = findViewById(R.id.text_input_layout_email);
        String email = getIntent().getStringExtra(EMAIL);
        emailEditText.setText(email == null ? "" : email);

        passwordEditText = findViewById(R.id.text_input_edit_text_password);
        passwordTextInputLayout = findViewById(R.id.text_input_layout_password);

        progressBar = findViewById(R.id.progress_bar);
    }

    private LoginHandler.Callback createLoginHandlerCallback() {
        return new LoginHandler.Callback() {
            @Override
            public void onSuccess(@NonNull Task<AuthResult> task) {
                super.onSuccess(task);
                if (auth.getCurrentUser() == null) {
                    Log.e(TAG, "Null user in successful registration");
                    return;
                }
                if (task.getResult() != null && task.getResult().getAdditionalUserInfo().isNewUser()) {
                    initializeUser(auth.getCurrentUser().getUid());
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

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    public void onRegisterButtonClick(android.view.View view) {
        // Validate email and set error message
        if (emailEditText.getText() == null) {
            Log.e(TAG, "EditText getText() returned null");
            return;
        }
        Email email = new Email(emailEditText.getText().toString(), this);
        String errorMsg = email.getError();
        Util.setTextInputLayoutErrorReclaim(emailTextInputLayout, errorMsg);
        boolean errorShown = errorMsg != null;

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

        // Register user
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email.getEmail(), password.getPassword())
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            initializeUser(auth.getCurrentUser().getUid());
                        } else {
                            Exception exception = task.getException();
                            String msg = exception == null ? "" : ": " + exception.getLocalizedMessage();
                            Snackbar.make(coordinatorLayout, R.string.auth_failed_registration, Snackbar.LENGTH_LONG).show();
                            Log.d(TAG, getString(R.string.auth_failed_registration) + msg);
                        }
                    }
                });

    }

    public void onRegisterGoogleButtonClick(android.view.View view) {
        loginHandler.onLoginGoogleButtonClick();
    }

    public void onRegisterFacebookButtonClick(android.view.View view) {
        loginHandler.onLoginFacebookButtonClick();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginHandler.onActivityResult(requestCode, resultCode, data);

    }

    private void initializeUser(final String userId) {
        if (userId == null) {
            Log.e(TAG, "null userId in initializeUser");
            return;
        }


        final Uri profilePicUri = getIntent().getParcelableExtra(PROFILE_PIC_URI);
        final String displayName = getIntent().getStringExtra(DISPLAY_NAME);
        final String username = getIntent().getStringExtra(USERNAME);

        Map<String, Object> displayNameMap = new HashMap<>();
        displayNameMap.put("displayName", displayName);
        displayNameMap.put("username", username);
        FirebaseFunctions.getInstance()
                .getHttpsCallable("addUser")
                .call(displayNameMap)
                .addOnCompleteListener(this, new OnCompleteListener<HttpsCallableResult>() {
                    @Override
                    public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() == null || task.getResult().getData() == null) {
                                Log.e(TAG, "addUser cloud function didn't return result");
                                return;
                            }
                            Map<String, Object> data = (Map<String, Object>) task.getResult().getData();
                            String errorCode = (String) data.get("errorCode");
                            if (errorCode == null) {
                                Log.e(TAG, "addUser result data doesn't contain 'errorCode' field");
                            } else if (errorCode.equals(Constants.ErrorCode.OK)) {
                                Log.d(TAG, "addUser returned OK");
                                User.getProfilePicRef(userId).putFile(profilePicUri)
                                        .addOnFailureListener(RegistrationActivity.this, new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "Failed to add user profile pic to storage", e);

                                            }
                                        });
                                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else if (errorCode.equals(Constants.ErrorCode.DUPLICATE_USERNAME)) {
                                Log.w(TAG, "addUser returned DUPLICATE_USERNAME");
                               handleDuplicateUsername();
                            } else {
                                Log.e(TAG, "addUser returned unknown error code");
                            }
                        } else {
                            Log.e(TAG, "Failed to addUser", task.getException());
                        }
                    }
                });
    }

    private void handleDuplicateUsername() {
        if (auth.getCurrentUser() == null) {
            Log.e(TAG, "Null user in handleDuplicateUsername");
            return;
        }

        final Uri profilePicUri = getIntent().getParcelableExtra(PROFILE_PIC_URI);
        final String displayName = getIntent().getStringExtra(DISPLAY_NAME);
        final String username = getIntent().getStringExtra(USERNAME);
        final String email = emailEditText.getText().toString();

        auth.getCurrentUser().delete()
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loginHandler.signOut();
                        Intent intent = new Intent(RegistrationActivity.this, SetupProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(DUPLICATE_USERNAME, true);
                        intent.putExtra(DISPLAY_NAME, displayName);
                        intent.putExtra(USERNAME, username);
                        intent.putExtra(PROFILE_PIC_URI, profilePicUri);
                        intent.putExtra(EMAIL, email);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to delete account in handleDuplicateUsername", e);
                    }
                });

    }
}
