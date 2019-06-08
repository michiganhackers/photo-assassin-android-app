package org.michiganhackers.photoassassin;

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

    private List<DocumentReference> currentGames;
    private List<DocumentReference> friends;
    private List<DocumentReference> pastGames;

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

    public List<DocumentReference> getCurrentGames() {
        return currentGames;
    }

    public void setCurrentGames(List<DocumentReference> currentGames) {
        this.currentGames = currentGames;
    }

    public List<DocumentReference> getFriends() {
        return friends;
    }

    public void setFriends(List<DocumentReference> friends) {
        this.friends = friends;
    }

    public List<DocumentReference> getPastGames() {
        return pastGames;
    }

    public void setPastGames(List<DocumentReference> pastGames) {
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
        return FirebaseFirestore.getInstance().collection("users").document(userId);
    }
}
