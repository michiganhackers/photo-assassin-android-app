package org.michiganhackers.photoassassin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

public class ProfileActivity extends FirebaseAuthActivity {

    private boolean userCurrentlyEditingDisplayName = false;
    EditText displayNameEditText;
    private final String TAG = getClass().getCanonicalName();
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        displayNameEditText = findViewById(R.id.edit_text_display_name);
        if (savedInstanceState != null) {
            userCurrentlyEditingDisplayName = savedInstanceState.getBoolean("userCurrentlyEditingDisplayName");
        }

        ProfileViewModelFactory profileViewModelFactory = new ProfileViewModelFactory(auth.getCurrentUser(), this);
        profileViewModel = ViewModelProviders.of(this, profileViewModelFactory).get(ProfileViewModel.class);

        Observer<User> userObserver = new Observer<User>() {
            @Override
            public void onChanged(User user) {
                displayNameEditText.setText(user.getDisplayName());
            }
        };
        profileViewModel.getUser().observe(this, userObserver);


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
                    focusedView.setFocusable(false);
                    focusedView.setEnabled(false);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public void onEditDisplayNameClick(View view) {
        displayNameEditText.setEnabled(true);
        displayNameEditText.setFocusableInTouchMode(true);
        if (displayNameEditText.requestFocus()) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(displayNameEditText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            Log.e(TAG, "was not able to request focus to display name edit text");
        }
    }
}
