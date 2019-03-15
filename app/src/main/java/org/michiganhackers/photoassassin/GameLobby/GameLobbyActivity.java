package org.michiganhackers.photoassassin.GameLobby;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import org.michiganhackers.photoassassin.Game;
import org.michiganhackers.photoassassin.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameLobbyActivity extends AppCompatActivity {
    private List<Game> gamesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_lobby);

        // TODO: THIS IS JUST FOR TESTING
        List<Integer> participantIds = new ArrayList<Integer>(Arrays.asList(1, 2, 3));
        Game game = new Game(participantIds, 24, 1, "Awesome Game", 12, 7);
        gamesList = new ArrayList<>();
        gamesList.add(game);

        RecyclerView gamesRecyclerView = findViewById(R.id.recyclerViewGames);
        GameRecyclerViewAdapter gameRecyclerViewAdapter = new GameRecyclerViewAdapter(gamesList);
        gamesRecyclerView.setAdapter(gameRecyclerViewAdapter);
        gamesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}
