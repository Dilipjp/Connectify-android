package com.dilip.conectivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.UserPostViewHolder> {

    private List<Post> postList;
    private DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");

    public UserPostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public UserPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_post, parent, false);
        return new UserPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.postCaption.setText(post.getCaption());
        Picasso.get().load(post.getPostImageUrl()).into(holder.postImage);


        holder.editButton.setOnClickListener(v -> {
            // Navigate to EditPostActivity and pass the post details
            Intent intent = new Intent(holder.itemView.getContext(), EditPostActivity.class);
            intent.putExtra("POST_ID", post.getPostId());
            intent.putExtra("POST_CAPTION", post.getCaption());
            intent.putExtra("POST_IMAGE_URL", post.getPostImageUrl());
            holder.itemView.getContext().startActivity(intent);
        });

        // Delete button click
        holder.deleteButton.setOnClickListener(v -> {
            postsRef.child(post.getPostId()).removeValue((databaseError, databaseReference) -> {
                if (databaseError == null) {
                    Toast.makeText(holder.itemView.getContext(), "Post deleted", Toast.LENGTH_SHORT).show();
                    postList.remove(position);
                    notifyItemRemoved(position);  // Remove item from RecyclerView
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Failed to delete post", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class UserPostViewHolder extends RecyclerView.ViewHolder {

        private ImageView postImage;
        private TextView postCaption;
        private Button editButton, deleteButton;

        public UserPostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.postImage);
            postCaption = itemView.findViewById(R.id.postCaption);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
