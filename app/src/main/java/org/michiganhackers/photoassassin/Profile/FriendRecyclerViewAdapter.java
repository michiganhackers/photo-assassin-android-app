
package org.michiganhackers.photoassassin.Profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.michiganhackers.photoassassin.R;
import org.michiganhackers.photoassassin.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.michiganhackers.photoassassin.Profile.ProfileActivity.PROFILE_USER_ID;

public class FriendRecyclerViewAdapter extends RecyclerView.Adapter<FriendRecyclerViewAdapter.ViewHolder> {

    private List<User> friends;
    private HashSet<String> loggedInUserFriendIds;
    private final String loggedInUserId;
    private final Activity activity;
    private final AddRemoveFriendHandler addRemoveFriendHandler;

    public FriendRecyclerViewAdapter(Activity activity, List<User> friends, List<User> loggedInUserFriends, String loggedInUserId) {
        this.activity = activity;
        this.friends = friends;

        this.loggedInUserFriendIds = new HashSet<String>();
        updateLoggedInUserFriends(loggedInUserFriends);

        this.loggedInUserId = loggedInUserId;

        // Verify that the host activity implements the callback interface
        try {
            addRemoveFriendHandler = (AddRemoveFriendHandler) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AddRemoveFriendHandler");
        }
    }

    public void updateFriends(List<User> friends) {
        if (!this.friends.equals(friends)) {
            this.friends = friends;
            notifyDataSetChanged();
        }
    }

    public void updateLoggedInUserFriends(List<User> loggedInUserFriends) {
        HashSet<String> newLoggedInUserFriendIds = new HashSet<>();
        for (int i = 0; i < loggedInUserFriends.size(); ++i) {
            newLoggedInUserFriendIds.add(loggedInUserFriends.get(i).getId());
        }

        if (!loggedInUserFriendIds.equals(newLoggedInUserFriendIds)) {
            loggedInUserFriendIds = newLoggedInUserFriendIds;
            notifyDataSetChanged();
        }
    }

    public interface AddRemoveFriendHandler {
        void Add(String userId);

        void Remove(String userId);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        View addRemoveFriendButton;
        ImageView addFriendImage, removeFriendImage;
        TextView addRemoveFriendText;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_view_name);
            addRemoveFriendButton = itemView.findViewById(R.id.linear_layout_add_remove_friend_button);
            addFriendImage = itemView.findViewById(R.id.image_add_friend);
            removeFriendImage = itemView.findViewById(R.id.image_remove_friend);
            addRemoveFriendText = itemView.findViewById(R.id.text_add_remove_friend);
        }
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User friend = friends.get(position);
        holder.nameTextView.setText(friend.getDisplayName());
        holder.nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!friend.getId().equals(loggedInUserId)) {
                    Intent intent = new Intent(activity, ProfileActivity.class);
                    intent.putExtra(PROFILE_USER_ID, friend.getId());
                    activity.startActivity(intent);
                }
            }
        });

        if (!friend.getId().equals(loggedInUserId)) {
            holder.addRemoveFriendButton.setVisibility(View.VISIBLE);
            if (loggedInUserFriendIds.contains(friend.getId())) {
                holder.addFriendImage.setVisibility(View.GONE);
                holder.removeFriendImage.setVisibility(View.VISIBLE);
                holder.addRemoveFriendText.setText(R.string.remove);
                holder.addRemoveFriendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addRemoveFriendHandler.Remove(friend.getId());
                    }
                });
            } else {
                holder.addFriendImage.setVisibility(View.VISIBLE);
                holder.removeFriendImage.setVisibility(View.GONE);
                holder.addRemoveFriendText.setText(R.string.add);
                holder.addRemoveFriendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addRemoveFriendHandler.Add(friend.getId());
                    }
                });
            }
        } else {
            holder.addRemoveFriendButton.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}