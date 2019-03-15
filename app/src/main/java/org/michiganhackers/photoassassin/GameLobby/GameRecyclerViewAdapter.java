package org.michiganhackers.photoassassin.GameLobby;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.michiganhackers.photoassassin.Game;
import org.michiganhackers.photoassassin.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GameRecyclerViewAdapter extends RecyclerView.Adapter<GameRecyclerViewAdapter.ViewHolder> {
    private List<Game> games;

    public GameRecyclerViewAdapter(List<Game> games) {
        this.games = games;
    }

    @NonNull
    @Override
    public GameRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.game_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull GameRecyclerViewAdapter.ViewHolder holder, int position) {
        Game game = games.get(position);
        holder.gameName.setText(game.getGameName());
        holder.playerCount.setText(game.getCurrentGameCapacity());
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView gameName;
        public TextView playerCount;

        public ViewHolder(View itemView) {
            super(itemView);

            gameName = (TextView) itemView.findViewById(R.id.textViewGameName);
            playerCount = (TextView) itemView.findViewById(R.id.textViewNumParticipants);
        }
    }
}
