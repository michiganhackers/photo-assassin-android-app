package org.michiganhackers.photoassassin.LoginPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.michiganhackers.photoassassin.DisplayName;
import org.michiganhackers.photoassassin.RequestImageDialog;
import org.michiganhackers.photoassassin.R;

import java.io.File;

import static org.michiganhackers.photoassassin.LoginPages.LoginActivity.ACCOUNT_NOT_REGISTERED_YET;

public class SetupProfileActivity extends AppCompatActivity implements RequestImageDialog.ImageUriHandler {

    private CoordinatorLayout coordinatorLayout;
    private TextInputEditText displayNameEditText;
    private TextInputLayout displayNameTextInputLayout;
    private ImageView profilePicImageView;
    private Uri profilePicUri;

    private final String TAG = getClass().getCanonicalName();
    private RequestImageDialog requestImageDialog;

    public static final String DISPLAY_NAME = "display name";
    public static final String PROFILE_PIC_URI = "profile pic uri";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        displayNameEditText = findViewById(R.id.text_input_edit_text_display_name);
        displayNameTextInputLayout = findViewById(R.id.text_input_layout_display_name);
        profilePicImageView = findViewById(R.id.image_profile_pic);

        if (getIntent().getBooleanExtra(ACCOUNT_NOT_REGISTERED_YET, false)) {
            Snackbar.make(coordinatorLayout, R.string.acct_not_yet_registered_msg, Snackbar.LENGTH_LONG).show();
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
            Log.e(TAG, "EditText getText() returned null");
            return;
        }

        DisplayName displayName = new DisplayName(displayNameEditText.getText().toString(), this);
        String errorMsg = displayName.getError();
        displayNameTextInputLayout.setError(errorMsg);
        boolean errorShown = errorMsg != null;

        if (profilePicUri == null) {
            Snackbar.make(coordinatorLayout, R.string.profile_pic_required, Snackbar.LENGTH_LONG).show();
            errorShown = true;
        }

        if (errorShown) {
            return;
        }

        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.putExtra(DISPLAY_NAME, displayName.getDisplayName());
        intent.putExtra(PROFILE_PIC_URI, profilePicUri);
        startActivity(intent);
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
