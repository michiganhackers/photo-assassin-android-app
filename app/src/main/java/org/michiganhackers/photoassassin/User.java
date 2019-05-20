package org.michiganhackers.photoassassin;

import com.google.firebase.firestore.DocumentReference;
import com.google.firestore.v1beta1.StructuredQuery;

import org.michiganhackers.photoassassin.LoginPages.Email;

import java.util.ArrayList;
import java.util.List;

public class User {
    private List<DocumentReference> currentGames;
    private int deaths;
    private String displayName;
    private List<DocumentReference> friends;
    private String id;
    private int kills;
    private int longestLifeSeconds;
    private List<DocumentReference> pastGames;
    private String profilePicUrl;

    public User() {
        currentGames = new ArrayList<>();
        deaths = 0;
        this.displayName = null;
        friends = new ArrayList<>();
        this.id = null;
        kills = 0;
        longestLifeSeconds = 0;
        pastGames = new ArrayList<>();
    }

    public User(String displayName, String id) {
        currentGames = new ArrayList<>();
        deaths = 0;
        this.displayName = displayName;
        friends = new ArrayList<>();
        this.id = id;
        kills = 0;
        longestLifeSeconds = 0;
        pastGames = new ArrayList<>();
    }

    public List<DocumentReference> getCurrentGames() {
        return currentGames;
    }

    public void setCurrentGames(List<DocumentReference> currentGames) {
        this.currentGames = currentGames;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<DocumentReference> getFriends() {
        return friends;
    }

    public void setFriends(List<DocumentReference> friends) {
        this.friends = friends;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<DocumentReference> getPastGames() {
        return pastGames;
    }

    public void setPastGames(List<DocumentReference> pastGames) {
        this.pastGames = pastGames;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}
