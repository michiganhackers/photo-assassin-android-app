package org.michiganhackers.photoassassin.Settings;

import android.os.Bundle;

import org.michiganhackers.photoassassin.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NotificationsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
    }

    public void onBackButtonClick(android.view.View view) {
        finish();
    }
}
