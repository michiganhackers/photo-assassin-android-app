package org.michiganhackers.photoassassin.Profile;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.michiganhackers.photoassassin.User;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class UserLiveDataWrapper {
    private MutableLiveData<User> user;
    private MutableLiveData<List<User>> friends;
    private DocumentReference userRef;
    private final String userId;
    private final String TAG = getClass().getCanonicalName();

    UserLiveDataWrapper(String userId) {
        if (userId == null) {
            Log.e(TAG, "Null user id");
        }
        this.userId = userId;
        user = new MutableLiveData<>();
        friends = new MutableLiveData<>();
        friends.setValue(new ArrayList<User>());
        userRef = User.getUserRef(userId);
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "userRef listen failed", e);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    user.setValue(documentSnapshot.toObject(User.class));
                    updateFriends();
                } else {
                    Log.e(TAG, "null userRef documentSnapshot");
                }
            }
        });
    }

    LiveData<User> getUser() {
        return user;
    }

    LiveData<List<User>> getFriends() {
        return friends;
    }

    // Note: This only updates friends if a friend is added or removed. If a friend just changes
    // their dispaly name, for example, it won't be updated
    private void updateFriends() {
        List<String> oldFriendIds = new ArrayList<>();
        if (friends.getValue() != null) {
            for (int i = 0; i < friends.getValue().size(); ++i) {
                oldFriendIds.add(friends.getValue().get(i).getId());
            }
        }

        if (user.getValue() != null) {
            final List<String> newFriendIds = user.getValue().getFriends();
            if (!oldFriendIds.equals(newFriendIds)) {
                final List<User> newFriends = new ArrayList<>();
                for (int i = 0; i < newFriendIds.size(); ++i) {
                    User.getUserRef(newFriendIds.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            //Note: callback happens on main thread, so no need to worry about synchronizing
                            // access to newFriends
                            newFriends.add(documentSnapshot.toObject(User.class));
                            if (newFriends.size() == newFriendIds.size()) {
                                friends.setValue(newFriends);
                            }
                        }
                    });
                }
                if (newFriendIds.isEmpty()) {
                    friends.setValue(newFriends);
                }
            }
        }
    }
}
