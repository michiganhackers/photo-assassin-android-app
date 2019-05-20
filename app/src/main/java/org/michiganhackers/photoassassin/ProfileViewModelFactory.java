package org.michiganhackers.photoassassin;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseUser;

public class ProfileViewModelFactory implements ViewModelProvider.Factory {
    private FirebaseUser firebaseUser;
    private Context context;

    public ProfileViewModelFactory(FirebaseUser firebaseUser, Context context) {
        this.firebaseUser = firebaseUser;
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(firebaseUser, context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
