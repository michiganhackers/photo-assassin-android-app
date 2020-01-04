package org.michiganhackers.photoassassin;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.internal.Logger;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String id;
    private String displayName;
    private String username;
    private String profilePicUrl;

    private CollectionReference currentGames;
    private CollectionReference friends;
    private CollectionReference pastGames;

    private int deaths;
    private int kills;
    private int longestLifeSeconds;

    private static final String TAG = "User";

    public User() {
        id = null;
        displayName = null;
        username = null;
        profilePicUrl = null;

        deaths = 0;
        kills = 0;
        longestLifeSeconds = 0;
    }

    public User(String id) {
        this.id = id;
        displayName = null;
        username = null;
        profilePicUrl = null;

        deaths = 0;
        kills = 0;
        longestLifeSeconds = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }


    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getLongestLifeSeconds() {
        return longestLifeSeconds;
    }

    public void setLongestLifeSeconds(int longestLifeSeconds) {
        this.longestLifeSeconds = longestLifeSeconds;
    }

    public CollectionReference getCurrentGames() {
        return currentGames;
    }

    public void setCurrentGames(CollectionReference currentGames) {
        this.currentGames = currentGames;
    }

    public CollectionReference getFriends() {
        return friends;
    }

    public void setFriends(CollectionReference friends) {
        this.friends = friends;
    }

    public CollectionReference getPastGames() {
        return pastGames;
    }

    public void setPastGames(CollectionReference pastGames) {
        this.pastGames = pastGames;
    }

    public static StorageReference getProfilePicRef(String userId) {
        return FirebaseStorage.getInstance().getReference().child("images/profile_pictures/" + userId);
    }

    public static DocumentReference getUserRef(String userId) {
        return User.getUsersRef().document(userId);
    }

    public static CollectionReference getUsersRef() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static CollectionReference getFriendsRef(String userId) {
        return User.getUserRef(userId).collection("friends");
    }

    public static CollectionReference getCurrentGamesRef(String userId) {
        return User.getUserRef(userId).collection("currentGames");
    }

    public static CollectionReference getPastGamesRef(String userId) {
        return User.getUserRef(userId).collection("pastGames");
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof User)) {
            return false;
        }

        return this.getId().equals(((User) obj).getId());
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }
}
