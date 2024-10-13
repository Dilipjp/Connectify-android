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

public class ProfilePostsAdapter extends RecyclerView.Adapter<ProfilePostsAdapter.PostViewHolder> {

    private List<Post> postList;

    public ProfilePostsAdapter(List<Post> postList) {
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
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImageView;
        TextView captionTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            postImageView = itemView.findViewById(R.id.postImageView);
            captionTextView = itemView.findViewById(R.id.captionTextView);
        }
    }
}