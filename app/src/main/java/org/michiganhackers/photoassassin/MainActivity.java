package org.michiganhackers.photoassassin;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.michiganhackers.photoassassin.Profile.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

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

    public void onMakeEveryoneFriendsClick(android.view.View view) {
        User.getUsersRef().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    List<String> ids = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        ids.add(doc.toObject(User.class).getId());
                    }
                    for (int i = 0; i < ids.size(); ++i) {
                        String tempId = ids.get(i);
                        ids.remove(i);
                        User.getUserRef(tempId).update("friends", ids);
                        ids.add(i, tempId);
                    }
                } else {
                    Log.e("MainActivity", "null users queryDocumentSnapshots");
                }
            }
        });
    }

    public void onRemoveEveryoneFriendsClick(android.view.View view) {
        User.getUsersRef().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        doc.getReference().update("friends", new ArrayList<>());
                    }
                } else {
                    Log.e("MainActivity", "null users queryDocumentSnapshots");
                }
            }
        });
    }
}
