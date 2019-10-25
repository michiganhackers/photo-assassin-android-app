package org.michiganhackers.photoassassin.LoginPages;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import org.michiganhackers.photoassassin.R;
import org.michiganhackers.photoassassin.Util;

import java.util.Collections;

import androidx.annotation.NonNull;

// Handles login logic for google and facebook
// Instance of this class must be created in onCreate
// onActivityResult must be called in activity's onActivityResult

public class ServiceLoginHandler extends ServiceLogoutHandler {
    private CallbackManager facebookCallbackManager;
    private final String TAG = getClass().getCanonicalName();
    private final static int REQUEST_CODE_GOOGLE_SIGN_IN = 1;

    public interface Callback {
        void onSuccess(@NonNull Task<AuthResult> task);

        void onFailure(Exception exception);

        void onCancel();
    }

    private Callback callback;

    ServiceLoginHandler(Activity activity, FirebaseAuth auth, Callback callback) {
        this.activity = activity;
        this.auth = auth;
        this.callback = callback;

        setupServices();

        facebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(facebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.i(TAG, "facebook login cancelled");
                        ServiceLoginHandler.this.callback.onCancel();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d(TAG, "facebook login error", exception);
                        ServiceLoginHandler.this.callback.onFailure(exception);
                    }
                });
    }

    void onLoginGoogleButtonClick(android.view.View view) {
        Intent googleSignInClientSignInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(googleSignInClientSignInIntent, REQUEST_CODE_GOOGLE_SIGN_IN);
    }

    void onLoginFacebookButtonClick(android.view.View view) {
        LoginManager
                .getInstance()
                .logInWithReadPermissions(
                        activity,
                        Collections.singletonList("email")
                );
    }

    private void authenticateWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "Google sign in successful");
                            callback.onSuccess(task);
                        } else {
                            Log.d(TAG, "failed to sign in with google", task.getException());
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "Facebook sign in successful");
                            callback.onSuccess(task);
                        } else {
                            Log.d(TAG, "failed to sign in with facebook", task.getException());
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account == null) {
                        RuntimeException e = new RuntimeException("failed to sign in with google: null GoogleSignInAccount");
                        Log.d(TAG, "null GoogleSignInAccount", e);
                        callback.onFailure(e);
                    } else {
                        authenticateWithGoogle(account);
                    }
                } catch (ApiException e) {
                    Log.d(TAG, "failed to sign in with google", e);
                    callback.onFailure(e);
                }
            } else {
                Log.i(TAG, "REQUEST_CODE_GOOGLE_SIGN_IN cancelled");
                callback.onCancel();
            }

        }
    }
}
