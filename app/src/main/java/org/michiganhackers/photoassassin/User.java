package org.michiganhackers.photoassassin;

import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.internal.Logger;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String displayName;
    private String profilePicUrl;

    private List<String> currentGames;
    private List<String> friends;
    private List<String> pastGames;

    private int deaths;
    private int kills;
    private int longestLifeSeconds;

    public User() {
        id = null;
        displayName = null;
        profilePicUrl = null;

        currentGames = new ArrayList<>();
        friends = new ArrayList<>();
        pastGames = new ArrayList<>();

        deaths = 0;
        kills = 0;
        longestLifeSeconds = 0;
    }

    public User(String id, String displayName, String profilePicUrl) {
        this.id = id;
        this.displayName = displayName;
        this.profilePicUrl = profilePicUrl;

        currentGames = new ArrayList<>();
        friends = new ArrayList<>();
        pastGames = new ArrayList<>();

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

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public List<String> getCurrentGames() {
        return currentGames;
    }

    public void setCurrentGames(List<String> currentGames) {
        this.currentGames = currentGames;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<String> getPastGames() {
        return pastGames;
    }

    public void setPastGames(List<String> pastGames) {
        this.pastGames = pastGames;
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

    public static StorageReference getProfilePicRef(String userId) {
        return FirebaseStorage.getInstance().getReference().child("images/profile_pictures/" + userId);
    }

    public static DocumentReference getUserRef(String userId) {
        return User.getUsersRef().document(userId);
    }

    public static CollectionReference getUsersRef() {
        return FirebaseFirestore.getInstance().collection("users");
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
