package org.michiganhackers.photoassassin.Profile;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.snackbar.Snackbar;

import org.michiganhackers.photoassassin.DisplayName;
import org.michiganhackers.photoassassin.FirebaseAuthActivity;
import org.michiganhackers.photoassassin.R;
import org.michiganhackers.photoassassin.RequestImageDialog;
import org.michiganhackers.photoassassin.User;

import java.io.File;

public class ProfileActivity extends FirebaseAuthActivity implements RequestImageDialog.ImageUriHandler {

    public static final String USER_CURRENTLY_EDITING_DISPLAY_NAME = "userCurrentlyEditingDisplayName";
    private ProfileViewModel profileViewModel;

    private boolean userCurrentlyEditingDisplayName = false;
    private EditText displayNameEditText;

    private ImageView profilePicImageView;
    private Uri profilePicUri;
    private RequestImageDialog requestImageDialog;

    private CoordinatorLayout coordinatorLayout;

    private final String TAG = getClass().getCanonicalName();
    public static final String PROFILE_PIC_URI = "profile pic uri";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        displayNameEditText = findViewById(R.id.edit_text_display_name);

        displayNameEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (updateDisplayName()) {
                        removeFocusFromDisplayNameEditText(displayNameEditText);
                    }
                    return true;
                }

                return false;
            }
        });


        profilePicImageView = findViewById(R.id.image_profile_pic);
        coordinatorLayout = findViewById(R.id.coordinator_layout);

        final ProfileViewModelFactory profileViewModelFactory = new ProfileViewModelFactory(auth.getCurrentUser().getUid());
        profileViewModel = ViewModelProviders.of(this, profileViewModelFactory).get(ProfileViewModel.class);

        if (savedInstanceState != null) {
            userCurrentlyEditingDisplayName = savedInstanceState.getBoolean(USER_CURRENTLY_EDITING_DISPLAY_NAME);
            if (userCurrentlyEditingDisplayName) {
                onEditDisplayNameClick(displayNameEditText);
            }
            profilePicUri = savedInstanceState.getParcelable(PROFILE_PIC_URI);
            if (profilePicUri != null) {
                handleImageUri(profilePicUri);
            }
        }

        Observer<User> userObserver = new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (!userCurrentlyEditingDisplayName) {
                    displayNameEditText.setText(user.getDisplayName());
                }
                if (profilePicUri == null) {
                    Glide.with(ProfileActivity.this)
                            .load(user.getProfilePicUrl())
                            .placeholder(R.drawable.ic_profile)
                            .centerCrop()
                            .into(profilePicImageView);
                }
                profileViewModel.getUser().removeObserver(this);
            }
        };
        profileViewModel.getUser().observe(this, userObserver);


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
        profileViewModel.updateProfilePic(profilePicUri);

    }

    public void onAddProfilePicButtonClick(android.view.View view) {
        requestImageDialog = new RequestImageDialog();
        requestImageDialog.show(getSupportFragmentManager(), "requestImageDialog");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View focusedView = getCurrentFocus();
            if (focusedView instanceof EditText) {
                Rect focusedViewRect = new Rect();
                focusedView.getGlobalVisibleRect(focusedViewRect);
                if (!focusedViewRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    removeFocusFromDisplayNameEditText(focusedView);
                    if (!updateDisplayName()) {
                        displayNameEditText.setText(profileViewModel.getUser().getValue().getDisplayName());
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void removeFocusFromDisplayNameEditText(View focusedView) {
        focusedView.clearFocus();
        focusedView.setFocusable(false);
        focusedView.setEnabled(false);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        userCurrentlyEditingDisplayName = false;
    }

    // returns true if display name is valid
    private boolean updateDisplayName() {
        DisplayName displayName = new DisplayName(displayNameEditText.getText().toString(), this);
        String errorMsg = displayName.getError();
        if (errorMsg != null) {
            Snackbar.make(coordinatorLayout, errorMsg, Snackbar.LENGTH_LONG).show();
            return false;
        } else {
            // Only update display name if it has changed
            if (!displayName.getDisplayName().equals(profileViewModel.getUser().getValue().getDisplayName())) {
                profileViewModel.updateDisplayName(displayName.getDisplayName());
            }
        }
        return true;
    }

    public void onEditDisplayNameClick(View view) {
        displayNameEditText.setEnabled(true);
        displayNameEditText.setFocusableInTouchMode(true);
        if (displayNameEditText.requestFocus()) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(displayNameEditText, InputMethodManager.SHOW_IMPLICIT);
            displayNameEditText.setSelection(displayNameEditText.getText().toString().length());
            userCurrentlyEditingDisplayName = true;
        } else {
            Log.e(TAG, "was not able to request focus to display name edit text");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (profilePicUri != null) {
            outState.putParcelable(PROFILE_PIC_URI, profilePicUri);
        }
        outState.putBoolean(USER_CURRENTLY_EDITING_DISPLAY_NAME, userCurrentlyEditingDisplayName);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestImageDialog != null) {
            requestImageDialog.onActivityResult(requestCode, resultCode, data);
        }
    }
}
