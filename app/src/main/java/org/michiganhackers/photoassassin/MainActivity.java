package org.michiganhackers.photoassassin;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import org.michiganhackers.photoassassin.Profile.ProfileActivity;

import static org.michiganhackers.photoassassin.Profile.ProfileActivity.PROFILE_USER_ID;

public class MainActivity extends FirebaseAuthActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSignoutButtonClick(android.view.View view) {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "null user", Toast.LENGTH_LONG).show();

        }
        signOut();
    }

    public void onCurrentProfileClick(android.view.View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(PROFILE_USER_ID, auth.getCurrentUser().getUid());
        startActivity(intent);
    }

    public void onOtherProfileClick(android.view.View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(PROFILE_USER_ID, "PBkztTsSyZbpGmznxoRkbltFR203");
        startActivity(intent);
    }


}
