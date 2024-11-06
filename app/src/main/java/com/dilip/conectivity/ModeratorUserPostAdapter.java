package com.dilip.conectivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ModeratorUserPostAdapter extends RecyclerView.Adapter<ModeratorUserPostAdapter.UserPostViewHolder> {
    private Context context;

    private List<Post> postList;
    private DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");

    public ModeratorUserPostAdapter(Context context, List<Post> postList) {
        this.context = context;

        this.postList = postList;
    }

    @NonNull
    @Override
    public UserPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.moderator_item_user_post, parent, false);
        return new UserPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.postCaption.setText(post.getCaption());
        Picasso.get().load(post.getPostImageUrl()).into(holder.postImage);


        holder.editButton.setOnClickListener(v -> {
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

//        holder.sendWarningButton.setOnClickListener(v -> {
//
//        });

        //
        holder.sendWarningButton.setOnClickListener(v -> {
            DatabaseReference warningRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(post.getUserId())
                    .child("userWarnings");

            String postId = post.getPostId();

            warningRef.orderByChild("postId").equalTo(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        // Warning exists, show dialog to remove it
                        new AlertDialog.Builder(context)
                                .setTitle("Remove Warning")
                                .setMessage("Are you sure you want to remove the warning for this post?")
                                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                .setPositiveButton("Remove Warning", (dialog, which) -> {
                                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                        childSnapshot.getRef().removeValue();
                                    }
                                    Toast.makeText(context, "Warning removed successfully", Toast.LENGTH_SHORT).show();
                                })
                                .show();
                    } else {
                        // No warning exists, show dialog to send a new one
                        EditText warningInput = new EditText(context);
                        warningInput.setText("Please review the content of this post.");

                        new AlertDialog.Builder(context)
                                .setTitle("Send Warning")
                                .setView(warningInput)
                                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                .setPositiveButton("Send", (dialog, which) -> {
                                    String warningMessage = warningInput.getText().toString();
                                    long timestamp = System.currentTimeMillis();

                                    DatabaseReference newWarningRef = warningRef.push();
                                    newWarningRef.child("message").setValue(warningMessage);
                                    newWarningRef.child("postId").setValue(postId);
                                    newWarningRef.child("timestamp").setValue(timestamp)
                                            .addOnSuccessListener(aVoid ->
                                                    Toast.makeText(context, "Warning sent successfully", Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(context, "Error sending warning: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                })
                                .show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Failed to check warnings: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });


        //

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class UserPostViewHolder extends RecyclerView.ViewHolder {

        private ImageView postImage;
        private TextView postCaption;
        private Button editButton, deleteButton, sendWarningButton;

        public UserPostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.postImage);
            postCaption = itemView.findViewById(R.id.postCaption);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            sendWarningButton = itemView.findViewById(R.id.sendWarningButton);
        }
    }
}