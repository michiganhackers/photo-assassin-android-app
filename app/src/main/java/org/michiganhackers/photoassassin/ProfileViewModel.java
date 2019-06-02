package org.michiganhackers.photoassassin;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;
import org.michiganhackers.photoassassin.LoginPages.Email;

import java.util.Map;
import java.util.UUID;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<User> user;
    private DocumentReference userRef;
    private String userId;
    private final String TAG = getClass().getCanonicalName();
    private StorageReference storageReference;

    public ProfileViewModel(final FirebaseUser firebaseUser, final Context context) {
        this.userId = firebaseUser.getUid();
        user = new MutableLiveData<>();
        userRef = FirebaseFirestore.getInstance().collection("users").document(userId);
        storageReference = FirebaseStorage.getInstance().getReference();

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        user.setValue(documentSnapshot.toObject(User.class));
                    } else {
                        initializeDefaultUser(context, firebaseUser);
                    }
                } else {
                    Log.e(TAG, "Failed to get user document", task.getException());
                }
            }
        });
    }

    public LiveData<User> getUser() {
        return user;
    }

    public void updateUser(User newUser, Uri newProfilePicFilepath) {
        if (newUser.getId().equals(userId)) {
            if (newProfilePicFilepath != null) {
                updateProfilePic(newProfilePicFilepath);
            }

            // newUser.profilePicUrl is always null, so newUserMap never has that field
            Map<String, Object> newUserMap = Util.pojoToMap(newUser);

            // merge because we don't want to change the profilePicUrl field
            userRef.set(newUserMap, SetOptions.merge());

        } else {
            Log.e(TAG, "Attempted to add user whose id did not match the ProfileViewModel's userId");
        }
    }


    public void updateProfilePic(Uri newProfilePicFilepath) {
        // delete old profile picture
        deleteProfilePic();

        // upload picture and set user profilePicUrl
        final StorageReference profilePicRef = storageReference.child("images/users/" + UUID.randomUUID().toString());
        profilePicRef.putFile(newProfilePicFilepath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        updateUserProfilePicUrl(profilePicRef);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to add user profile pic to storage", e);
                    }
                });
    }

    private void updateUserProfilePicUrl(StorageReference profilePicRef) {
        profilePicRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        userRef.update("profilePicUrl", uri.toString())
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Failed to update user profilePicUrl", e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to get download url from profile pic ref", e);
                    }
                });
    }

    private void deleteProfilePic() {
        User userLocal = user.getValue();
        if (userLocal != null && userLocal.getProfilePicUrl() != null) {
            StorageReference profilePicRef = FirebaseStorage.getInstance().getReferenceFromUrl(userLocal.getProfilePicUrl());
            profilePicRef.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Failed to delete users's profile pic", e);
                }
            });
        }
    }

    public void updateDisplayName(String displayName) {
        userRef.update("displayName", displayName)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update user displayName", e);
                    }
                });
    }

}
