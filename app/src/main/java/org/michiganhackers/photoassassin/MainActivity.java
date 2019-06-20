package org.michiganhackers.photoassassin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends FirebaseAuthActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        print("Hello World!");
        print("Testing");

    }

    private void print(String s) {

        print(s);
    }

    public void onSignoutButtonClick(android.view.View view) {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "null user", Toast.LENGTH_LONG).show();

        }
        signOut();
    }


}
