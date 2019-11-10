package org.michiganhackers.photoassassin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.michiganhackers.photoassassin.LoginPages.LoginActivity;
import org.michiganhackers.photoassassin.LoginPages.LogoutHandler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

// This activity will redirect the user to the login page if they are not logged in
// Any activity that a user must be signed in to see should extend this class
public abstract class FirebaseAuthActivity extends AppCompatActivity {
    protected FirebaseAuth auth;
    protected FirebaseAuth.AuthStateListener authListener;
    private final String TAG = getClass().getCanonicalName();
    private LogoutHandler logoutHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        logoutHandler = new LogoutHandler(this, auth);
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

    protected void signOut() {
        logoutHandler.signOut();
    }
}
