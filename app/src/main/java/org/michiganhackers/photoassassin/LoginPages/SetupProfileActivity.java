package org.michiganhackers.photoassassin.LoginPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.michiganhackers.photoassassin.DisplayName;
import org.michiganhackers.photoassassin.RequestImageDialog;
import org.michiganhackers.photoassassin.R;
import org.michiganhackers.photoassassin.Username;
import org.michiganhackers.photoassassin.Util;

import java.io.File;

import static org.michiganhackers.photoassassin.LoginPages.LoginActivity.ACCOUNT_NOT_REGISTERED_YET;
import static org.michiganhackers.photoassassin.LoginPages.RegistrationActivity.EMAIL;

public class SetupProfileActivity extends AppCompatActivity implements RequestImageDialog.ImageUriHandler {

    private CoordinatorLayout coordinatorLayout;
    private TextInputEditText displayNameEditText;
    private TextInputLayout displayNameTextInputLayout;
    private TextInputEditText usernameEditText;
    private TextInputLayout usernameTextInputLayout;
    private ImageView profilePicImageView;
    private Uri profilePicUri;

    private final String TAG = getClass().getCanonicalName();
    private RequestImageDialog requestImageDialog;

    public static final String DISPLAY_NAME = "display name";
    public static final String USERNAME = "username";
    public static final String PROFILE_PIC_URI = "profile pic uri";

    public static final String DUPLICATE_USERNAME = "duplicate username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        displayNameEditText = findViewById(R.id.text_input_edit_text_display_name);
        displayNameTextInputLayout = findViewById(R.id.text_input_layout_display_name);
        usernameEditText = findViewById(R.id.text_input_edit_text_username);
        usernameTextInputLayout = findViewById(R.id.text_input_layout_username);
        profilePicImageView = findViewById(R.id.image_profile_pic);

        if (getIntent().getBooleanExtra(ACCOUNT_NOT_REGISTERED_YET, false)) {
            Snackbar.make(coordinatorLayout, R.string.acct_not_yet_registered_msg, Snackbar.LENGTH_LONG).show();
        }
        if (getIntent().getBooleanExtra(DUPLICATE_USERNAME, false)) {
            Snackbar.make(coordinatorLayout, R.string.username_taken, Snackbar.LENGTH_LONG).show();

            Uri profilePicUri = getIntent().getParcelableExtra(PROFILE_PIC_URI);
            handleImageUri(profilePicUri);
            String displayName = getIntent().getStringExtra(DISPLAY_NAME);
            displayNameEditText.setText(displayName);
            String username = getIntent().getStringExtra(USERNAME);
            usernameEditText.setText(username);
        }

        if (savedInstanceState != null) {
            profilePicUri = savedInstanceState.getParcelable(PROFILE_PIC_URI);
            if (profilePicUri != null) {
                handleImageUri(profilePicUri);
            }
        }
    }

    @Override
    public void handleImageUri(Uri uri) {
        profilePicUri = uri;
        // Add signature to cached image because RequestImageDialog always uses same filename to
        // store image
        Glide.with(this)
                .load(profilePicUri)
                .placeholder(R.drawable.ic_profile)
                .centerCrop()
                .signature(new ObjectKey(new File(profilePicUri.getPath()).lastModified()))
                .into(profilePicImageView);
    }

    public void onAddProfilePicButtonClick(android.view.View view) {
        requestImageDialog = new RequestImageDialog();
        requestImageDialog.show(getSupportFragmentManager(), "requestImageDialog");
    }

    public void onLoginButtonClick(android.view.View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onContinueButtonClick(android.view.View view) {
        if (displayNameEditText.getText() == null) {
            Log.e(TAG, "displayNameEditText getText() returned null");
            return;
        }
        if (usernameEditText.getText() == null) {
            Log.e(TAG, "usernameEditText getText() returned null");
            return;
        }

        final DisplayName displayName = new DisplayName(displayNameEditText.getText().toString(), this);
        String errorMsg = displayName.getError();
        Util.setTextInputLayoutErrorReclaim(displayNameTextInputLayout, errorMsg);
        boolean errorShown = errorMsg != null;

        final Username username = new Username(usernameEditText.getText().toString(), this);
        errorMsg = username.getError();
        Util.setTextInputLayoutErrorReclaim(usernameTextInputLayout, errorMsg);
        errorShown = errorShown || errorMsg != null;

        if (profilePicUri == null) {
            Snackbar.make(coordinatorLayout, R.string.profile_pic_required, Snackbar.LENGTH_LONG).show();
            errorShown = true;
        }

        if (errorShown) {
            return;
        }

        DocumentReference usernameRef = FirebaseFirestore.getInstance().collection("usernames").document(username.getUsername());
        usernameRef.get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult() != null && task.getResult().exists()){
                        Snackbar.make(coordinatorLayout, R.string.username_taken, Snackbar.LENGTH_LONG).show();
                    }
                    else{
                        Intent intent = new Intent(SetupProfileActivity.this, RegistrationActivity.class);
                        intent.putExtra(DISPLAY_NAME, displayName.getDisplayName());
                        intent.putExtra(USERNAME, username.getUsername());
                        intent.putExtra(PROFILE_PIC_URI, profilePicUri);
                        String email = getIntent().getStringExtra(EMAIL);
                        intent.putExtra(EMAIL, email);
                        startActivity(intent);
                    }
                } else {
                    Log.e(TAG, "Failed to get username document", task.getException());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestImageDialog != null) {
            requestImageDialog.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (profilePicUri != null) {
            outState.putParcelable(PROFILE_PIC_URI, profilePicUri);
        }
        super.onSaveInstanceState(outState);
    }
}
