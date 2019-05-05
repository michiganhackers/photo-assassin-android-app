package org.michiganhackers.photoassassin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.michiganhackers.photoassassin.LoginPages.LoginActivity;
import org.michiganhackers.photoassassin.LoginPages.ServiceLoginHandler;
import org.michiganhackers.photoassassin.LoginPages.ServiceLogoutHandler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// This activity will redirect the user to the login page if they are not logged in
// Any activity that a user must be signed in to see should extend this class
public abstract class FirebaseAuthActivity extends AppCompatActivity {
    protected FirebaseAuth auth;
    protected FirebaseAuth.AuthStateListener authListener;
    private final String TAG = getClass().getCanonicalName();
    private ServiceLogoutHandler serviceLogoutHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        serviceLogoutHandler = new ServiceLogoutHandler(this, auth);
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                requireUserSignedIn(firebaseAuth);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authListener);
    }

    protected void requireUserSignedIn(FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    public void signOut() {
        serviceLogoutHandler.signOut();
        auth.signOut();
    }
}
