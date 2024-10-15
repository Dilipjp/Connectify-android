package com.dilip.conectivity;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private List<Comment> commentList;

    public CommentsAdapter(List<Comment> commentList) {
        this.commentList = commentList;
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
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentTextView, userNameTextView, timestampTextView;
        ImageView userProfileImageView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            userProfileImageView = itemView.findViewById(R.id.userProfileImageView);
        }
    }
}

