package com.dilip.conectivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log; // Import Log for debugging
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dilip.conectivity.R;
import com.dilip.conectivity.UserProfileActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {
    private List<Post> postList;
    private Context context;

    public PostsAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.captionTextView.setText(post.getCaption());
        Picasso.get().load(post.getPostImageUrl()).into(holder.postImageView);

        loadUserDetails(holder, post.getUserId());

        holder.likeCountTextView.setText(post.getLikeCount() + " Likes");
        holder.likeButton.setImageResource(post.isLiked() ? R.drawable.ic_like : R.drawable.ic_like);

        holder.likeButton.setOnClickListener(v -> {
            boolean isLiked = post.isLiked();
            int newLikeCount = isLiked ? post.getLikeCount() - 1 : post.getLikeCount() + 1;
            post.setLiked(!isLiked);
            post.setLikeCount(newLikeCount);

            // Update Firebase
            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(post.getPostId());
            postRef.child("likeCount").setValue(newLikeCount);
            postRef.child("isLiked").setValue(!isLiked);

            holder.likeCountTextView.setText(newLikeCount + " Likes");
            holder.likeButton.setImageResource(!isLiked ? R.drawable.ic_like : R.drawable.ic_like);
        });

        // Open user profile on username/profile image click
        View.OnClickListener profileClickListener = v -> {
            String profileUserId = post.getUserId();
            Log.d("PostsAdapter", "Clicked user ID: " + profileUserId); // Log the user ID

            // Check if the profileUserId is not null and context is valid
            if (profileUserId != null && context != null) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("userIdToFollow", profileUserId); // Ensure the key matches what you expect in UserProfileActivity
                context.startActivity(intent);
            } else {
                Log.e("PostsAdapter", "User ID is null or context is invalid for post: " + post.getPostId());
            }
        };

        // Set the click listener on both username and profile image
        holder.usernameTextView.setOnClickListener(profileClickListener);
        holder.userProfileImageView.setOnClickListener(profileClickListener);
    }

    private void loadUserDetails(PostViewHolder holder, String userId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("userName").getValue(String.class);
                    String userProfileImageUrl = dataSnapshot.child("userProfileImage").getValue(String.class);
                    holder.usernameTextView.setText(userName);
                    Picasso.get().load(userProfileImageUrl).into(holder.userProfileImageView);
                } else {
                    Log.e("PostsAdapter", "User data does not exist for user ID: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PostsAdapter", "Database error: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImageView, userProfileImageView, likeButton;
        TextView captionTextView, usernameTextView, likeCountTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.postImageView);
            userProfileImageView = itemView.findViewById(R.id.userProfileImageView);
            likeButton = itemView.findViewById(R.id.likeButton);
            captionTextView = itemView.findViewById(R.id.captionTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
        }
    }
}
