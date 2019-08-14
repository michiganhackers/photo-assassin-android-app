package org.michiganhackers.photoassassin.Profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ProfileViewModelFactory implements ViewModelProvider.Factory {
    private String profileUserId;
    private String loggedInUserId;

    public ProfileViewModelFactory(String profileUserId, String loggedInUserId) {
        this.profileUserId = profileUserId;
        this.loggedInUserId = loggedInUserId;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(profileUserId, loggedInUserId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
