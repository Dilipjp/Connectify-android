package com.dilip.conectivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {
    private Context context;
    private DatabaseReference usersRef, postsRef;
    private List<Post> postList;

    // Constructor with context and post list
    public PostsAdapter(Context context, List<Post> postList) {
        this.context = context; // Initialize context
        this.postList = postList; // Initialize post list
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        postsRef = FirebaseDatabase.getInstance().getReference("posts");
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set the post caption
        holder.captionTextView.setText(post.getCaption());

        // Load post image using Picasso
        Picasso.get()
                .load(post.getPostImageUrl())
                .placeholder(R.drawable.ic_post_placeholder)
                .error(R.drawable.ic_post_placeholder)
                .into(holder.postImageView);

        // Handle location display
        if (post.getLocationName() != null && !post.getLocationName().isEmpty()) {
            holder.locationTextView.setText(post.getLocationName());
            holder.locationLayout.setVisibility(View.VISIBLE);
        } else {
            holder.locationLayout.setVisibility(View.GONE);
        }

        holder.commentCountTextView.setText(String.valueOf(post.getCommentCount()));

        holder.likeCountTextView.setText(String.valueOf(post.getLikeCount()));

        // Check if the user has liked the post
        postsRef.child(post.getPostId()).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(currentUserId)) {
                    holder.Likebutton.setImageResource(R.drawable.ic_like); // Set to liked icon
                } else {
                    holder.Likebutton.setImageResource(R.drawable.like); // Set to default like icon
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });

        // Get user details from the 'users' node using the userId
        usersRef.child(post.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("userName").getValue(String.class);
                    String userProfileImageUrl = dataSnapshot.child("userProfileImage").getValue(String.class);

                    if (userName != null) {
                        holder.usernameTextView.setText(userName);
                    }
                    if (userProfileImageUrl != null && !userProfileImageUrl.isEmpty()) {
                        Picasso.get()
                                .load(userProfileImageUrl)
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .error(R.drawable.ic_profile_placeholder)
                                .into(holder.userProfileImageView);
                    } else {
                        holder.userProfileImageView.setImageResource(R.drawable.ic_profile_placeholder);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }
        });

        // Click listener for user profile image
        holder.userProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = post.getUserId();
                Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                intent.putExtra("USER_ID", userId); // Pass the user ID as an extra
                view.getContext().startActivity(intent); // Start the activity
            }
        });

        // Click listener for share button
        holder.Sharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePost(post.getPostImageUrl(), post.getCaption(), view.getContext());
            }
        });

        // Click listener for like button
        holder.Likebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postsRef.child(post.getPostId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.child("likes").hasChild(currentUserId)) {
                                postsRef.child(post.getPostId()).child("likes").child(currentUserId).removeValue();
                                postsRef.child(post.getPostId()).child("likeCount").setValue(post.getLikeCount() - 1);
                                holder.Likebutton.setImageResource(R.drawable.like);
                            } else {
                                postsRef.child(post.getPostId()).child("likes").child(currentUserId).setValue(true);
                                postsRef.child(post.getPostId()).child("likeCount").setValue(post.getLikeCount() + 1);
                                holder.Likebutton.setImageResource(R.drawable.ic_like);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle possible errors
                    }
                });
            }
        });

        // Click listener for comment icon
        holder.commentIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentsActivity.class);
                intent.putExtra("POST_ID", post.getPostId()); // Pass the postId to the CommentsActivity
                context.startActivity(intent);
            }
        });
    }

    private void sharePost(String imageUrl, String caption, Context context) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        // Add post data to the intent
        shareIntent.putExtra(Intent.EXTRA_TEXT, caption + "\n" + imageUrl);

        // Start the sharing activity
        context.startActivity(Intent.createChooser(shareIntent, "Share post via"));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImageView;
        ImageView userProfileImageView;
        TextView captionTextView;
        TextView usernameTextView;
        TextView locationTextView;
        LinearLayout locationLayout;
        LinearLayout Sharebutton;
        ImageView Likebutton;
        TextView likeCountTextView;
        ImageView commentIcon;
        TextView commentCountTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            postImageView = itemView.findViewById(R.id.postImageView);
            userProfileImageView = itemView.findViewById(R.id.userProfileImageView);
            captionTextView = itemView.findViewById(R.id.captionTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            locationLayout = itemView.findViewById(R.id.locationLayout);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            Sharebutton = itemView.findViewById(R.id.Sharebutton);
            Likebutton = itemView.findViewById(R.id.likeIcon);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
            commentIcon = itemView.findViewById(R.id.commentIcon);
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView);
        }
    }
}
