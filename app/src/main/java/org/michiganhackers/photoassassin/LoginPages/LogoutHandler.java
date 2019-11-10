package org.michiganhackers.photoassassin.LoginPages;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.michiganhackers.photoassassin.R;

import java.util.HashMap;
import java.util.Map;

// Handles logout logic for google, facebook, and email
// Instance of this class must be created in onCreate

public class LogoutHandler {
    GoogleSignInClient googleSignInClient;
    Activity activity;
    FirebaseAuth auth;
    private final String TAG = getClass().getCanonicalName();

    LogoutHandler() {
    }

    public LogoutHandler(Activity activity, FirebaseAuth auth) {
        this.activity = activity;
        this.auth = auth;
        setupServices();
    }

    void setupServices() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(activity, googleSignInOptions);
    }

    public void signOut() {
        removeFirebaseInstanceId()
                .addOnSuccessListener(activity, new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        if (GoogleSignIn.getLastSignedInAccount(activity) != null) {
                            googleSignInClient.signOut();
                        }
                        LoginManager.getInstance().logOut();
                        if(auth.getCurrentUser() != null){
                            auth.signOut();
                        }
                    }
                });

    }

    private Task<HttpsCallableResult> removeFirebaseInstanceId() {
        return FirebaseInstanceId.getInstance().getInstanceId()
                .continueWithTask(new Continuation<InstanceIdResult, Task<HttpsCallableResult>>() {
                    @Override
                    public Task<HttpsCallableResult> then(@NonNull Task<InstanceIdResult> task) throws Exception {
                        String token = task.getResult().getToken();
                        Map<String, Object> firebaseInstanceId = new HashMap<>();
                        firebaseInstanceId.put("firebaseInstanceId", token);
                        firebaseInstanceId.put("operation", "remove");
                        Log.d(TAG, "removing instance id: " + token);
                        return FirebaseFunctions.getInstance()
                                .getHttpsCallable("updateFirebaseInstanceIds")
                                .call(firebaseInstanceId);
                    }
                })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "failed to remove firebase instance id", e);
                    }
                });
    }
}
