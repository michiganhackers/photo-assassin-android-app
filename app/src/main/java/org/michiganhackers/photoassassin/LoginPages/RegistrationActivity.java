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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.michiganhackers.photoassassin.MainActivity;
import org.michiganhackers.photoassassin.R;
import org.michiganhackers.photoassassin.User;
import org.michiganhackers.photoassassin.Util;

import java.util.Map;

import static org.michiganhackers.photoassassin.LoginPages.SetupProfileActivity.DISPLAY_NAME;
import static org.michiganhackers.photoassassin.LoginPages.SetupProfileActivity.PROFILE_PIC_URI;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private TextInputLayout emailTextInputLayout, passwordTextInputLayout;
    private CoordinatorLayout coordinatorLayout;
    private FirebaseAuth auth;
    private final String TAG = getClass().getCanonicalName();
    private ServiceLoginHandler serviceLoginHandler;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();
        coordinatorLayout = findViewById(R.id.coordinator_layout);

        ServiceLoginHandler.Callback callback = new ServiceLoginHandler.Callback() {
            @Override
            public void onSuccess() {
                if (auth.getCurrentUser() == null) {
                    Log.e(TAG, "Null user in successful registration");
                    return;
                }
                initializeUser(auth.getCurrentUser().getUid());
                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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

        // Register user
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email.getEmail(), password.getPassword())
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            initializeUser(auth.getCurrentUser().getUid());
                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Log.i(TAG, "Account successfully created");
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
        serviceLoginHandler.onLoginGoogleButtonClick(view);
    }

    public void onRegisterFacebookButtonClick(android.view.View view) {
        serviceLoginHandler.onLoginFacebookButtonClick(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        serviceLoginHandler.onActivityResult(requestCode, resultCode, data);
    }

    private void initializeUser(final String userId) {

        if (userId == null) {
            Log.e(TAG, "null userId in initializeUser");
            return;
        }

        if (getIntent() == null) {
            Log.e(TAG, "getIntent() == null");
            return;
        }


        final Uri profilePicUri = getIntent().getParcelableExtra(PROFILE_PIC_URI);
        final String displayName = getIntent().getStringExtra(DISPLAY_NAME);

        User.getProfilePicRef(userId).putFile(profilePicUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        User newUser = new User(userId, displayName, uri.getPath());
                                        Map<String, Object> newUserMap = Util.pojoToMap(newUser);
                                        User.getUserRef(userId).set(newUserMap);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Failed to get download url from profile pic ref", e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to add user profile pic to storage", e);
                    }
                });


    }
}
