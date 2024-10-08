package com.dilip.conectivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.FollowerViewHolder> {

    private List<User> followersList;
    private Context context;

    public FollowersAdapter(List<User> followersList, Context context) {
        this.followersList = followersList;
        this.context = context;
    }

    @NonNull
    @Override
    public FollowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follower, parent, false);
        return new FollowerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowerViewHolder holder, int position) {
        User follower = followersList.get(position);
        holder.usernameTextView.setText(follower.getUserName());

        // Load profile image using Picasso
        Picasso.get()
                .load(follower.getUserProfileImage())
                .placeholder(R.drawable.ic_profile_placeholder) // Placeholder image
                .into(holder.profileImageView);

        // Set onClickListener to redirect to user's profile
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("profileUserId", follower.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return followersList.size();
    }

    static class FollowerViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageView;
        TextView usernameTextView;

        public FollowerViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
        }
    }
}
