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
    static final String ACCT_NOT_REGISTERED_YET = "account not registered yet";

    public interface Callback {
        void onSuccess();

        void onFailure(RuntimeException exception);

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
                        RuntimeException e = Util.prependToException("facebook login error", exception);
                        Log.d(TAG, "", e);
                        ServiceLoginHandler.this.callback.onFailure(e);
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
                            if (task.getResult() != null && task.getResult().getAdditionalUserInfo().isNewUser() && activity instanceof LoginActivity) {
                                Log.i(TAG, "New google sign in in LoginActivity");
                                callback.onCancel();
                                deleteAccountAndGotoSetupProfile();
                            } else {
                                callback.onSuccess();
                            }
                        } else {
                            RuntimeException e = Util.prependToException(activity.getString(R.string.failed_google_sign_in_message), task.getException());
                            Log.d(TAG, "", e);
                            callback.onFailure(e);
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
                            if (task.getResult() != null && task.getResult().getAdditionalUserInfo().isNewUser() && activity instanceof LoginActivity) {
                                Log.i(TAG, "New facebook sign in in LoginActivity");
                                callback.onCancel();
                                deleteAccountAndGotoSetupProfile();
                            } else {
                                callback.onSuccess();
                            }
                        } else {
                            RuntimeException e = Util.prependToException(activity.getString(R.string.failed_google_sign_in_facebook), task.getException());
                            Log.d(TAG, "", e);
                            callback.onFailure(e);
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
                        RuntimeException e = Util.prependToException(activity.getString(R.string.failed_google_sign_in_message), new RuntimeException("null GoogleSignInAccount"));
                        Log.d(TAG, "", e);
                        callback.onFailure(e);
                    } else {
                        authenticateWithGoogle(account);
                    }
                } catch (ApiException e) {
                    RuntimeException exc = Util.prependToException(activity.getString(R.string.failed_google_sign_in_message), e);
                    Log.d(TAG, "", exc);
                    callback.onFailure(exc);
                }
            } else {
                Log.i(TAG, "REQUEST_CODE_GOOGLE_SIGN_IN cancelled");
                callback.onCancel();
            }

        }
    }

    private void deleteAccountAndGotoSetupProfile() {
        if (auth.getCurrentUser() == null) {
            Log.e(TAG, "Null user in deleteAccountAndGotoSetupProfile");
            return;
        }
        auth.getCurrentUser().delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        signOut();
                        Intent intent = new Intent(activity, SetupProfileActivity.class);
                        intent.putExtra(ACCT_NOT_REGISTERED_YET, true);
                        activity.startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to delete account in deleteAccountAndGotoSetupProfile", e);
                    }
                });

    }

}
