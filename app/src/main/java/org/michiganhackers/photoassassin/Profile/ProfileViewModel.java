package org.michiganhackers.photoassassin.Profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.storage.UploadTask;

import org.michiganhackers.photoassassin.User;

import java.util.List;

public class ProfileViewModel extends ViewModel {
    private final String profileUserId, loggedInUserId;
    private final UserLiveDataWrapper profileUserLiveDataWrapper, loggedInUserLiveDataWrapper;
    private final String TAG = getClass().getCanonicalName();

    ProfileViewModel(String profileUserId, String loggedInUserId) {
        this.profileUserId = profileUserId;
        this.loggedInUserId = loggedInUserId;
        profileUserLiveDataWrapper = new UserLiveDataWrapper(profileUserId);
        loggedInUserLiveDataWrapper = new UserLiveDataWrapper(loggedInUserId);
    }

    LiveData<User> getProfileUser() {
        return profileUserLiveDataWrapper.getUser();
    }

    LiveData<List<User>> getProfileUserFriends() {
        return profileUserLiveDataWrapper.getFriends();
    }

    LiveData<User> getLoggedInUser() {
        return loggedInUserLiveDataWrapper.getUser();
    }

    LiveData<List<User>> getLoggedInUserFriends() {
        return loggedInUserLiveDataWrapper.getFriends();
    }

    public void updateProfilePic(final Uri newProfilePicUri) {
        if (!profileUserId.equals(loggedInUserId)) {
            Log.e(TAG, "Logged in user attempted to update profile pic of another user");
            return;
        }
        User.getProfilePicRef(profileUserId).putFile(newProfilePicUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(final Uri uri) {
                                        User.getUserRef(profileUserId).update("profilePicUrl", uri.toString())
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
        if (!profileUserId.equals(loggedInUserId)) {
            Log.e(TAG, "Logged in user attempted to update display name of another user");
            return;
        }
        User.getUserRef(profileUserId).update("displayName", displayName)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update user displayName", e);
                    }
                });
    }

    public void addFriend(String friendId) {
        if (friendId.equals(loggedInUserId)) {
            Log.e(TAG, "Logged in user attempted to add themself as a friend");
            return;
        }
        User.getUserRef(friendId).update("friends", FieldValue.arrayUnion(loggedInUserId));
        User.getUserRef(loggedInUserId).update("friends", FieldValue.arrayUnion(friendId));
    }

    public void removeFriend(String friendId) {
        if (friendId.equals(loggedInUserId)) {
            Log.e(TAG, "Logged in user attempted to removed themself as a friend");
            return;
        }
        User.getUserRef(friendId).update("friends", FieldValue.arrayRemove(loggedInUserId));
        User.getUserRef(loggedInUserId).update("friends", FieldValue.arrayRemove(friendId));
    }
}
