package org.michiganhackers.photoassassin.LoginPages;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;

import org.michiganhackers.photoassassin.MainActivity;
import org.michiganhackers.photoassassin.R;

// Handles logout logic for google, facebook, and email
// Instance of this class must be created in onCreate
// onActivityResult must be called in activity's onActivityResult

public class LoginHandler extends LogoutHandler {
    private CallbackManager facebookCallbackManager;
    private final String TAG = getClass().getCanonicalName();
    private final static int REQUEST_CODE_GOOGLE_SIGN_IN = 1;

    public static abstract class Callback {
        private final String TAG = getClass().getCanonicalName();
        public void onSuccess(@NonNull Task<AuthResult> task){
            //TODO: should this be succeeding be a prerequisite to calling onSuccess?
            // It would make logging in slower, but it might be better to stop the login process
            // if it fails
            addFirebaseInstanceId();
        }

        public abstract void onFailure(Exception exception);

        public abstract void onCancel();

        private void  addFirebaseInstanceId() {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .continueWithTask(new Continuation<InstanceIdResult, Task<HttpsCallableResult>>() {
                        @Override
                        public Task<HttpsCallableResult> then(@NonNull Task<InstanceIdResult> task) {
                            String token = task.getResult().getToken();
                            Map<String, Object> firebaseInstanceId = new HashMap<>();
                            firebaseInstanceId.put("firebaseInstanceId", token);
                            firebaseInstanceId.put("operation", "add");
                            Log.d(TAG, "adding instance id: " + token);
                            return FirebaseFunctions.getInstance()
                                    .getHttpsCallable("updateFirebaseInstanceIds")
                                    .call(firebaseInstanceId);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Failed to add firebase instance id", e);
                        }
                    });

        }
    }

    private Callback callback;

    LoginHandler(Activity activity, FirebaseAuth auth, Callback callback) {
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
                        LoginHandler.this.callback.onCancel();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d(TAG, "facebook login error", exception);
                        LoginHandler.this.callback.onFailure(exception);
                    }
                });
    }

    void onLoginGoogleButtonClick() {
        Intent googleSignInClientSignInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(googleSignInClientSignInIntent, REQUEST_CODE_GOOGLE_SIGN_IN);
    }

    void onLoginFacebookButtonClick() {
        LoginManager
                .getInstance()
                .logInWithReadPermissions(
                        activity,
                        Collections.singletonList("email")
                );
    }

    // progressBar parameter can be null if not required
    void onLoginEmailButtonClick(String email, String password, final ProgressBar progressBar) {
        if(progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
        }
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(progressBar != null){
                            progressBar.setVisibility(View.GONE);
                        }
                        if(task.isSuccessful()){
                            Log.i(TAG, "Email and password sign in successful");
                            callback.onSuccess(task);
                        }
                        else{
                            Log.e(TAG, "Failed to sign in with email and password", task.getException());
                            callback.onFailure(task.getException());
                        }
                    }
                });
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
