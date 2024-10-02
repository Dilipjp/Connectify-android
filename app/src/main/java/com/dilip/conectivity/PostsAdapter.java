package com.dilip.conectivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostsAdapter(List<Post> postList) {
        this.postList = postList;
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

        // Set the post caption
        holder.captionTextView.setText(post.getCaption());

        // Load post image using Picasso
        Picasso.get()
                .load(post.getPostImageUrl())
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(holder.postImageView);

        // Get user details from the 'users' node using the userId
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(post.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get user profile image URL and username
                    String userName = dataSnapshot.child("userName").getValue(String.class);
                    String userProfileImageUrl = dataSnapshot.child("userProfileImage").getValue(String.class);

                    // Set the username
                    holder.usernameTextView.setText(userName);

                    // Load user profile image using Picasso
                    Picasso.get()
                            .load(userProfileImageUrl)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .error(R.drawable.ic_profile_placeholder)
                            .into(holder.userProfileImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }
        });

        // Set like count
        holder.likeCountTextView.setText(post.getLikeCount() + " Likes");

        // Handle like button click
        holder.likeButton.setOnClickListener(v -> {
            // Increment like count and update Firebase
            int newLikeCount = post.getLikeCount() + 1;
            post.setLikeCount(newLikeCount);

            // Update the Firebase database
            DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
            postsRef.child(post.getPostId()).child("likeCount").setValue(newLikeCount);

            // Update like count on the UI
            holder.likeCountTextView.setText(newLikeCount + " Likes");
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImageView;
        ImageView userProfileImageView;
        ImageView likeButton;
        TextView captionTextView;
        TextView usernameTextView;
        TextView likeCountTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            postImageView = itemView.findViewById(R.id.postImageView);
            userProfileImageView = itemView.findViewById(R.id.userProfileImageView);
            likeButton = itemView.findViewById(R.id.likeButton);
            captionTextView = itemView.findViewById(R.id.captionTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
        }
    }
}
