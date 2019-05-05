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
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import org.michiganhackers.photoassassin.MainActivity;
import org.michiganhackers.photoassassin.R;

import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

// Handles login logic for google and facebook
// Instance of this class must be created in onCreate
// onActivityResult must be called in activity's onActivityResult

public class ServiceLoginHandler extends ServiceLogoutHandler {
    private CallbackManager facebookCallbackManager;
    private final String TAG = getClass().getCanonicalName();
    private final static int REQUEST_CODE_GOOGLE_SIGN_IN = 1;
    private CoordinatorLayout coordinatorLayout;


    ServiceLoginHandler(Activity activity, FirebaseAuth auth, CoordinatorLayout coordinatorLayout) {
        this.activity = activity;
        this.auth = auth;
        this.coordinatorLayout = coordinatorLayout;

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
                        Log.d(TAG, "facebook login cancelled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d(TAG, "facebook login error");
                    }
                });
    }


    // TODO: link google, fb, and email accounts to same user: https://firebase.google.com/docs/auth/android/account-linking?authuser=0
    void onRegisterGoogleButtonClick(android.view.View view) {
        Intent googleSignInClientSignInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(googleSignInClientSignInIntent, REQUEST_CODE_GOOGLE_SIGN_IN);
    }

    void onRegisterFacebookButtonClick(android.view.View view) {
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
                            Intent intent = new Intent(activity, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            activity.startActivity(intent);
                            Log.d(TAG, "Google sign in successful");
                        } else {
                            Exception exception = task.getException();
                            String msg = exception == null ? "" : ": " + exception.getLocalizedMessage();
                            Snackbar.make(coordinatorLayout, activity.getString(R.string.failed_google_sign_in_message) + msg, Snackbar.LENGTH_LONG).show();
                            Log.d(TAG, activity.getString(R.string.failed_google_sign_in_message) + msg);
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
                            Intent intent = new Intent(activity, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            activity.startActivity(intent);
                            Log.d(TAG, "Facebook sign in successful");
                        } else {
                            Exception exception = task.getException();
                            String msg = exception == null ? "" : ": " + exception.getLocalizedMessage();
                            Snackbar.make(coordinatorLayout, activity.getString(R.string.failed_google_sign_in_message) + msg, Snackbar.LENGTH_LONG).show();
                            Log.d(TAG, activity.getString(R.string.failed_google_sign_in_facebook) + msg);
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
                        Snackbar.make(coordinatorLayout, activity.getString(R.string.failed_google_sign_in_message), Snackbar.LENGTH_LONG).show();
                        Log.d(TAG, "null GoogleSignInAccount");
                    } else {
                        authenticateWithGoogle(account);
                    }
                } catch (ApiException e) {
                    Snackbar.make(coordinatorLayout, activity.getString(R.string.failed_google_sign_in_message), Snackbar.LENGTH_LONG).show();
                    Log.d(TAG, e.getMessage());
                }
            } else {
                Log.w(TAG, "REQUEST_CODE_GOOGLE_SIGN_IN cancelled");

            }

        }
    }

}
