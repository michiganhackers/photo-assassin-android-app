package org.michiganhackers.photoassassin.Profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.UploadTask;

import org.michiganhackers.photoassassin.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.Tainted;

public class ProfileViewModel extends ViewModel {
    private final String profileUserId, loggedInUserId;
    private MutableLiveData<User> profileUser;
    private MutableLiveData<List<User>> profileUserFriends;
    private MutableLiveData<List<String>> loggedInUserFriendIds;
    private MutableLiveData<String> searchedUserId;
    private final String TAG = getClass().getCanonicalName();

    ProfileViewModel(String profileUserId, String loggedInUserId) {
        this.profileUserId = profileUserId;
        this.loggedInUserId = loggedInUserId;
        profileUser = new MutableLiveData<>();
        profileUserFriends = new MutableLiveData<>();
        profileUserFriends.setValue(new ArrayList<User>());
        loggedInUserFriendIds = new MutableLiveData<>();
        loggedInUserFriendIds.setValue(new ArrayList<String>());
        searchedUserId = new MutableLiveData<>(); //TODO: does this need to be initialized?

        setupProfileUserListener();
        setupProfileFriendsListener();
        setupLoggedInUserFriendIdsListener();
    }

    public LiveData<User> getProfileUser() {
        return profileUser;
    }

    public LiveData<List<User>> getProfileUserFriends() {
        return profileUserFriends;
    }

    public LiveData<List<String>> getLoggedInUserFriendIds() {
        return loggedInUserFriendIds;
    }

    public LiveData<String> getSearchedUserId() {
        return searchedUserId;
    }

    private void setupProfileUserListener() {
        User.getUserRef(this.profileUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "userRef listen error", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    profileUser.setValue(documentSnapshot.toObject(User.class));
                } else {
                    Log.e(TAG, "invalid profile user document");
                }
            }
        });
    }

    private void setupProfileFriendsListener() {
        User.getFriendsRef(this.profileUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "friends collection listen error", e);
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.i("Friend doc", dc.getDocument().toString());
                            DocumentReference docRef = User.getUserRef(dc.getDocument().getId());
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    List<User> newFriends = profileUserFriends.getValue();
                                    newFriends.add(documentSnapshot.toObject(User.class));
                                    profileUserFriends.setValue(newFriends);
                                }
                            });
                            break;
                        case MODIFIED:
                            Log.e(TAG, "Friend document reference modified");
                            break;
                        case REMOVED:
                            List<User> newFriends = profileUserFriends.getValue();
                            newFriends.remove(new User(dc.getDocument().getId()));
                            profileUserFriends.setValue(newFriends);
                            break;
                    }
                }
            }
        });
    }

    private void setupLoggedInUserFriendIdsListener() {
        User.getFriendsRef(this.loggedInUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "friends collection listen error", e);
                    return;
                }
                List<String> newFriendIds = loggedInUserFriendIds.getValue();
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            newFriendIds.add(dc.getDocument().getId());
                            break;
                        case MODIFIED:
                            Log.e(TAG, "Friend document reference modified");
                            break;
                        case REMOVED:
                            newFriendIds.remove(dc.getDocument().getId());
                            break;
                    }
                }
                loggedInUserFriendIds.setValue(newFriendIds);
            }
        });
    }

    public void updateProfilePic(final Uri newProfilePicUri) {
        if (!profileUserId.equals(loggedInUserId)) {
            Log.e(TAG, "Logged in user attempted to update profile pic of another user");
            return;
        }
        User.getProfilePicRef(profileUserId).putFile(newProfilePicUri)
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
        Map<String, Object> displayNameMap = new HashMap<>();
        displayNameMap.put("displayName", displayName);
        FirebaseFunctions.getInstance()
                .getHttpsCallable("updateDisplayName")
                .call(displayNameMap)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "failed to update user displayName", e);
                    }
                });
    }

    public void addFriend(String friendId) {
        if (friendId.equals(loggedInUserId)) {
            Log.e(TAG, "Logged in user attempted to add themself as a friend");
            return;
        }
        Map<String, Object> friendMap = new HashMap<>();
        friendMap.put("friendToAddId", friendId);
        FirebaseFunctions.getInstance()
                .getHttpsCallable("addFriend")
                .call(friendMap)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "failed to add friend", e);
                    }
                });

    }

    public void removeFriend(String friendId) {
        if (friendId.equals(loggedInUserId)) {
            Log.e(TAG, "Logged in user attempted to removed themself as a friend");
            return;
        }
        Map<String, Object> friendMap = new HashMap<>();
        friendMap.put("friendToRemoveId", friendId);
        FirebaseFunctions.getInstance()
                .getHttpsCallable("removeFriend")
                .call(friendMap)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "failed to remove friend", e);
                    }
                });

    }

    // sets searchedUserId to the id of the user with the given displayName
    // if the displayName doesn't exist, sets the id to the empty string
    public void searchUserId(String displayName) {
        User.getUsersRef()
                .whereEqualTo("displayName", displayName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 1) {
                            Log.e(TAG, "multiple users have same display name");
                        } else if (queryDocumentSnapshots.size() == 0) {
                            searchedUserId.setValue("");
                        } else {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                searchedUserId.setValue(documentSnapshot.toObject(User.class).getId());
                            } else {
                                Log.e(TAG, "invalid user document");
                            }
                        }

                    }
                });
    }
}
