package com.dilip.conectivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.UserViewHolder> {

    private List<User> userList;

    public FollowerAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follower, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userNameTextView.setText(user.getUserName());

        // Load profile image using Glide


        Picasso.get()
                .load(user.getUserProfileImage())
                .placeholder(R.drawable.ic_profile_placeholder)  // Show placeholder while loading
                .error(R.drawable.ic_profile_placeholder)  // Fallback image in case of error
                .into(holder.userProfileImageView);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        public TextView userNameTextView;
        public ImageView userProfileImageView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            userProfileImageView = itemView.findViewById(R.id.userProfileImageView);
        }
    }
}