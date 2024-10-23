package com.dilip.conectivity;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private String currentUserId;
    private DatabaseReference postRef;

    public CommentsAdapter(List<Comment> commentList, String postId) {
        this.commentList = commentList;
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);


    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.commentTextView.setText(comment.getCommentText());
        holder.userNameTextView.setText(comment.getUserName());

        // Optionally format timestamp to a readable date
        String formattedDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(comment.getTimestamp()));
        holder.timestampTextView.setText(formattedDate);
        Picasso.get()
                .load(comment.getUserProfileImage())
                .placeholder(R.drawable.ic_profile_placeholder)  // Show placeholder while loading
                .error(R.drawable.ic_profile_placeholder)  // Fallback image in case of error
                .into(holder.userProfileImageView);
        // Show Edit/Delete buttons only if the comment belongs to the current user
        if (comment.getUserId().equals(currentUserId)) {
            holder.editCommentButton.setVisibility(View.VISIBLE);
            holder.deleteCommentButton.setVisibility(View.VISIBLE);
        } else {
            holder.editCommentButton.setVisibility(View.GONE);
            holder.deleteCommentButton.setVisibility(View.GONE);
        }

        // Handle Edit button click
        holder.editCommentButton.setOnClickListener(v -> {
            // Handle editing the comment
            editComment(comment.getCommentId(), comment.getCommentText(), holder.itemView.getContext());
//            editComment(commentId, currentText, holder.itemView.getContext());  // Pass the context to editComment

        });

        // Handle Delete button click
        holder.deleteCommentButton.setOnClickListener(v -> {
            // Handle deleting the comment
            deleteComment(comment.getCommentId(), holder.itemView.getContext());
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentTextView, userNameTextView, timestampTextView;
        ImageView userProfileImageView;
        Button editCommentButton, deleteCommentButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            userProfileImageView = itemView.findViewById(R.id.userProfileImageView);
            editCommentButton = itemView.findViewById(R.id.editCommentButton);
            deleteCommentButton = itemView.findViewById(R.id.deleteCommentButton);

        }
    }
    private void editComment(String commentId, String currentText, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Comment");

        final EditText input = new EditText(context);
        input.setText(currentText);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String updatedText = input.getText().toString().trim();
            if (!updatedText.isEmpty()) {
                postRef.child("comments").child(commentId).child("commentText").setValue(updatedText)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Comment updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to update comment", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(context, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    private void deleteComment(String commentId, Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Comment")
                .setMessage("Are you sure you want to delete this comment?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    postRef.child("comments").child(commentId).removeValue((error, ref) -> {
                        if (error == null) {
                            Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to delete comment", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

}