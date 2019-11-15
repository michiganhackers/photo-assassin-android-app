package org.michiganhackers.photoassassin;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

public class NewGame extends AppCompatActivity {
    private Button backButton;
    private Spinner playerLimitSpinner;
    private Button invitePlayersButton;
    private Button createGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        // Attach views to member variables
        backButton = (Button) findViewById(R.id.back_button);
        playerLimitSpinner = (Spinner) findViewById(R.id.player_limit);
        invitePlayersButton = (Button) findViewById(R.id.invite_players);
        createGameButton = (Button) findViewById(R.id.create_game);

        // Note: back button just calls finish() which pops it off the stack
        // TODO: create listeners for buttons
        // TODO: create and set adapter for spinner
    }

}
