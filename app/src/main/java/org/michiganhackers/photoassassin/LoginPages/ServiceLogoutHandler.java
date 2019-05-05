package org.michiganhackers.photoassassin.LoginPages;

import android.app.Activity;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.michiganhackers.photoassassin.R;

// Handles logout logic google and facebook
// Instance of this class must be created in onCreate

public class ServiceLogoutHandler {
    GoogleSignInClient googleSignInClient;
    Activity activity;
    FirebaseAuth auth;

    ServiceLogoutHandler() {
    }

    public ServiceLogoutHandler(Activity activity, FirebaseAuth auth) {
        this.activity = activity;
        this.auth = auth;
        setupServices();
    }

    void setupServices() {
        FacebookSdk.sdkInitialize(activity.getApplicationContext());

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(activity, googleSignInOptions);
    }

    // This only signs out of google and facebook.
    // auth.signOut might still need to be called
    public void signOut() {
        if (GoogleSignIn.getLastSignedInAccount(activity) != null) {
            googleSignInClient.signOut();
        }
        LoginManager.getInstance().logOut();
    }

}
