package com.dilip.conectivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        View view = LayoutInflater.from(context).inflate(R.layout.follower_item, parent, false);
        return new FollowerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowerViewHolder holder, int position) {
        User follower = followersList.get(position);
        holder.followerName.setText(follower.getUserName()); // Update to use getUserName()
    }

    @Override
    public int getItemCount() {
        return followersList.size();
    }

    static class FollowerViewHolder extends RecyclerView.ViewHolder {
        TextView followerName;

        FollowerViewHolder(@NonNull View itemView) {
            super(itemView);
            followerName = itemView.findViewById(R.id.follower_name); // Ensure this ID exists in your item layout
        }
    }
}
