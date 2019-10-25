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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.michiganhackers.photoassassin.DisplayName;
import org.michiganhackers.photoassassin.FirebaseAuthActivity;
import org.michiganhackers.photoassassin.R;
import org.michiganhackers.photoassassin.RequestImageDialog;
import org.michiganhackers.photoassassin.User;
import org.michiganhackers.photoassassin.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends FirebaseAuthActivity implements RequestImageDialog.ImageUriHandler, FriendRecyclerViewAdapter.AddRemoveFriendHandler {

    public static final String USER_CURRENTLY_EDITING_DISPLAY_NAME = "userCurrentlyEditingDisplayName";
    public static final String PROFILE_USER_ID = "Profile User ID";
    public static final String PROFILE_PIC_URI = "profile pic uri";
    private final String TAG = getClass().getCanonicalName();
    private ProfileViewModel profileViewModel;
    private String profileUserId;
    private boolean userCurrentlyEditingDisplayName = false;
    private EditText displayNameEditText;
    private ImageView profilePicImageView;
    private Uri profilePicUri;
    private RequestImageDialog requestImageDialog;
    private CoordinatorLayout coordinatorLayout;
    private TextInputLayout searchTextInputLayout;
    private TextInputEditText searchTextInputEditText;
    private FriendRecyclerViewAdapter friendRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupEditDisplayNameImeAction();
        setupSearchImeAction();

        if (savedInstanceState != null) {
            recoverInstanceState(savedInstanceState);
        } else {
            profileUserId = getIntent().getStringExtra(PROFILE_USER_ID);
            if (profileUserId == null) {
                Log.e(TAG, "no profile user ID provided to intent");
            }
        }

        instantiateProfileViewModel();
        setupFriendRecyclerView();
        setupProfileUserObserver();
        setupProfileUserFriendsObserver();
        setupLoggedInUserFriendIdsObserver();

        if (!profileUserId.equals(auth.getCurrentUser().getUid())) {
            findViewById(R.id.button_game_history_large).setVisibility(View.GONE);
            findViewById(R.id.search_bar).setVisibility(View.GONE);
            findViewById(R.id.fab_add_image).setVisibility(View.INVISIBLE);
            findViewById(R.id.image_edit_display_name).setVisibility(View.INVISIBLE);
            findViewById(R.id.linear_layout_add_friend_history).setVisibility(View.VISIBLE);
        }
        else
        {
            setupUserSearchObserver();
        }

    }

    private void initViews (){
        displayNameEditText = findViewById(R.id.edit_text_display_name);
        profilePicImageView = findViewById(R.id.image_profile_pic);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        searchTextInputLayout = findViewById(R.id.text_input_layout_search);
        searchTextInputEditText = findViewById(R.id.text_input_edit_text_search);
    }

    private void setupEditDisplayNameImeAction() {
        displayNameEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (updateDisplayName()) {
                        disableDisplayNameEditText(displayNameEditText);
                    }
                    return true;
                }

                return false;
            }
        });
    }

    private void setupSearchImeAction() {
        searchTextInputEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onSearchButtonClick(searchTextInputEditText);
                    return true;
                }
                return false;
            }
        });
    }

    private void instantiateProfileViewModel() {
        final ProfileViewModelFactory profileViewModelFactory = new ProfileViewModelFactory(profileUserId, auth.getCurrentUser().getUid());
        profileViewModel = ViewModelProviders.of(this, profileViewModelFactory).get(ProfileViewModel.class);
    }

    private void setupProfileUserFriendsObserver() {
        Observer<List<User>> profileUserFriendsObserver = new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                friendRecyclerViewAdapter.updateFriends(users);
            }
        };
        profileViewModel.getProfileUserFriends().observe(this, profileUserFriendsObserver);
    }

    private void setupProfileUserObserver() {
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
    }

    private void setupUserSearchObserver() {
        Observer<String> searchedUserIdObserver = new Observer<String>() {
            @Override
            public void onChanged(String searchedUserId) {
                if(searchedUserId.isEmpty()){
                    Util.setTextInputLayoutErrorReclaim(searchTextInputLayout, getString(R.string.display_name_doesnt_exist));
                }
                else if(searchedUserId.equals(profileUserId)){
                    Util.setTextInputLayoutErrorReclaim(searchTextInputLayout, getString(R.string.already_viewing_this_profile));
                }
                else
                {
                    Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                    intent.putExtra(PROFILE_USER_ID, searchedUserId);
                    startActivity(intent);
                    searchTextInputEditText.setText("");
                }
            }
        };
        profileViewModel.getSearchedUserId().observe(this, searchedUserIdObserver);
    }

    private void setupLoggedInUserFriendIdsObserver() {
        final Button addRemoveFriendButton = findViewById(R.id.button_add_remove_friend);
        Observer<List<String>> loggedInUserFriendIdsObserver = new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> friendIds) {
                friendRecyclerViewAdapter.updateLoggedInUserFriendIds(friendIds);
                if (friendIds.contains(profileUserId)) {
                    addRemoveFriendButton.setText(R.string.remove_friend);
                } else {
                    addRemoveFriendButton.setText(R.string.add_friend);
                }
            }
        };
        profileViewModel.getLoggedInUserFriendIds().observe(this, loggedInUserFriendIdsObserver);
    }

    private void setupFriendRecyclerView() {
        RecyclerView friendsRecyclerView = findViewById(R.id.recycler_friends);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendRecyclerViewAdapter = new FriendRecyclerViewAdapter(this, new ArrayList<User>(), new ArrayList<String>(), auth.getCurrentUser().getUid());
        friendsRecyclerView.setAdapter(friendRecyclerViewAdapter);
    }

    private void recoverInstanceState(Bundle savedInstanceState) {
        userCurrentlyEditingDisplayName = savedInstanceState.getBoolean(USER_CURRENTLY_EDITING_DISPLAY_NAME);
        if (userCurrentlyEditingDisplayName) {
            onEditDisplayNameClick(displayNameEditText);
        }
        profilePicUri = savedInstanceState.getParcelable(PROFILE_PIC_URI);
        if (profilePicUri != null) {
            handleImageUri(profilePicUri);
        }
        profileUserId = savedInstanceState.getString(PROFILE_USER_ID);
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

    public void onSearchButtonClick(android.view.View view) {
        DisplayName displayName = new DisplayName(searchTextInputEditText.getText().toString(), this);
        String errorMsg = displayName.getError();
        Util.setTextInputLayoutErrorReclaim(searchTextInputLayout, errorMsg);
        if(errorMsg != null){
            return;
        }
        profileViewModel.searchUserId(displayName.getDisplayName());
    }

    public void onAddRemoveFriendClick(android.view.View view) {
        List<String> loggedInUserFriendIds = profileViewModel.getLoggedInUserFriendIds().getValue();
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
                    focusedView.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                    if(focusedView == displayNameEditText){
                        disableDisplayNameEditText(focusedView);
                        if (!updateDisplayName()) {
                            displayNameEditText.setText(profileViewModel.getProfileUser().getValue().getDisplayName());
                        }
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void disableDisplayNameEditText(View focusedView) {
        focusedView.setFocusable(false);
        focusedView.setEnabled(false);
        userCurrentlyEditingDisplayName = false;
    }

    // returns true if display name is updated
    // Note: can still error during profileViewModel.updateDisplayName()
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
        outState.putString(PROFILE_USER_ID, profileUserId);
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
