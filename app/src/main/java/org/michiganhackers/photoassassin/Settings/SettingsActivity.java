package org.michiganhackers.photoassassin.Settings;

import android.os.Bundle;

import org.michiganhackers.photoassassin.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initializeFragment();
    }

    public void onBackButtonClick(android.view.View view) {
        finish();
    }

    public void initializeFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, new SettingsFragment())
                .commit();
    }
}
