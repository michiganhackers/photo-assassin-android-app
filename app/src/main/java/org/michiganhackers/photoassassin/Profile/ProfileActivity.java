package org.michiganhackers.photoassassin.Profile;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.michiganhackers.photoassassin.DisplayName;
import org.michiganhackers.photoassassin.FirebaseAuthActivity;
import org.michiganhackers.photoassassin.R;
import org.michiganhackers.photoassassin.RequestImageDialog;
import org.michiganhackers.photoassassin.User;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProfileActivity extends FirebaseAuthActivity implements RequestImageDialog.ImageUriHandler, FriendRecyclerViewAdapter.AddRemoveFriendHandler {

    public static final String USER_CURRENTLY_EDITING_DISPLAY_NAME = "userCurrentlyEditingDisplayName";
    public static final String PROFILE_USER_ID = "Profile User ID";
    private ProfileViewModel profileViewModel;
    private String profileUserId;

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

        if (savedInstanceState != null) {
            userCurrentlyEditingDisplayName = savedInstanceState.getBoolean(USER_CURRENTLY_EDITING_DISPLAY_NAME);
            if (userCurrentlyEditingDisplayName) {
                onEditDisplayNameClick(displayNameEditText);
            }
            profilePicUri = savedInstanceState.getParcelable(PROFILE_PIC_URI);
            if (profilePicUri != null) {
                handleImageUri(profilePicUri);
            }
            profileUserId = savedInstanceState.getString(PROFILE_USER_ID);
        } else {
            profileUserId = getIntent().getStringExtra(PROFILE_USER_ID);
            if (profileUserId == null) {
                Log.e(TAG, "no profile user ID provided to intent");
            }
        }

        RecyclerView friendsRecyclerView = findViewById(R.id.recycler_friends);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final FriendRecyclerViewAdapter friendRecyclerViewAdapter = new FriendRecyclerViewAdapter(this, new ArrayList<User>(), new ArrayList<User>(), auth.getCurrentUser().getUid());
        friendsRecyclerView.setAdapter(friendRecyclerViewAdapter);

        final ProfileViewModelFactory profileViewModelFactory = new ProfileViewModelFactory(profileUserId, auth.getCurrentUser().getUid());
        profileViewModel = ViewModelProviders.of(this, profileViewModelFactory).get(ProfileViewModel.class);

        Observer<User> profileUserObserver = new Observer<User>() {
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
            }
        };
        profileViewModel.getProfileUser().observe(this, profileUserObserver);

        Observer<List<User>> profileUserFriendsObserver = new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                friendRecyclerViewAdapter.updateFriends(users);
            }
        };
        profileViewModel.getProfileUserFriends().observe(this, profileUserFriendsObserver);

        final Button addRemoveFriendButton = findViewById(R.id.button_add_remove_friend);
        Observer<List<User>> loggedInUserFriendsObserver = new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                friendRecyclerViewAdapter.updateLoggedInUserFriends(users);
                Set<String> friendIds = new HashSet<>();
                for (int i = 0; i < users.size(); ++i) {
                    friendIds.add(users.get(i).getId());
                }
                if (friendIds.contains(profileUserId)) {
                    addRemoveFriendButton.setText(R.string.remove_friend);
                } else {
                    addRemoveFriendButton.setText(R.string.add_friend);
                }
            }
        };
        profileViewModel.getLoggedInUserFriends().observe(this, loggedInUserFriendsObserver);


        if (!profileUserId.equals(auth.getCurrentUser().getUid())) {
            FloatingActionButton addImageFAB = findViewById(R.id.fab_add_image);
            addImageFAB.setVisibility(View.INVISIBLE);

            ImageView editDisplayNameImage = findViewById(R.id.image_edit_display_name);
            editDisplayNameImage.setVisibility(View.INVISIBLE);

            LinearLayout addFriendHistoryLinearLayout = findViewById(R.id.linear_layout_add_friend_history);
            addFriendHistoryLinearLayout.setVisibility(View.VISIBLE);
            Button largeGameHistoryButton = findViewById(R.id.button_game_history_large);
            largeGameHistoryButton.setVisibility(View.GONE);
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
        profileViewModel.updateProfilePic(profilePicUri);

    }

    @Override
    public void Add(String userId) {
        profileViewModel.addFriend(userId);
    }

    @Override
    public void Remove(String userId) {
        profileViewModel.removeFriend(userId);
    }

    public void onAddProfilePicButtonClick(android.view.View view) {
        requestImageDialog = new RequestImageDialog();
        requestImageDialog.show(getSupportFragmentManager(), "requestImageDialog");
    }

    public void onBackButtonClick(android.view.View view) {
        finish();
    }

    public void onAddRemoveFriendClick(android.view.View view) {
        List<String> loggedInUserFriendIds = profileViewModel.getLoggedInUser().getValue().getFriendIds();
        if (loggedInUserFriendIds.contains(profileUserId)) {
            profileViewModel.removeFriend(profileUserId);
        } else {
            profileViewModel.addFriend(profileUserId);
        }
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
                        displayNameEditText.setText(profileViewModel.getProfileUser().getValue().getDisplayName());
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
            if (!displayName.getDisplayName().equals(profileViewModel.getProfileUser().getValue().getDisplayName())) {
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
