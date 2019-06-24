package org.michiganhackers.photoassassin.Profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.net.Uri;
import android.util.Log;

import org.michiganhackers.photoassassin.User;

public class ProfileViewModel extends ViewModel {
    // Note that user is only set once
    private MutableLiveData<User> user;
    private DocumentReference userRef;
    private String userId;
    private final String TAG = getClass().getCanonicalName();
    private StorageReference storageReference;

    ProfileViewModel(String userId) {
        this.userId = userId;
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
                        Log.e(TAG, "user document doesn't exist");
                    }
                } else {
                    Log.e(TAG, "Failed to get user document", task.getException());
                }
            }
        });
    }

    LiveData<User> getUser() {
        return user;
    }

    public void updateProfilePic(final Uri newProfilePicUri) {
        User.getProfilePicRef(userId).putFile(newProfilePicUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(final Uri uri) {
                                        userRef.update("profilePicUrl", uri.toString())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Note that this will not notify any observers
                                                        user.getValue().setProfilePicUrl(uri.toString());
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e(TAG, "Failed to update user profilePicUrl", e);
                                                    }
                                                });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {

                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Failed to get download url from profile pic ref", e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to add user profile pic to storage", e);
                    }
                });
    }


    public void updateDisplayName(final String displayName) {
        userRef.update("displayName", displayName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Note that this will not notify any observers
                        user.getValue().setDisplayName(displayName);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update user displayName", e);
                    }
                });
    }

}
