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
                .placeholder(R.drawable.ic_post_placeholder)
                .error(R.drawable.ic_post_placeholder)
                .into(holder.postImageView);

<<<<<<< HEAD
        if (post.getLocationName() != null && !post.getLocationName().isEmpty()) {
            holder.locationTextView.setText(post.getLocationName());
            holder.locationLayout.setVisibility(View.VISIBLE);
        } else {
            holder.locationLayout.setVisibility(View.GONE);
        }
=======
                    if (post.getLocationName() != null && !post.getLocationName().isEmpty()) {
                        holder.locationTextView.setText(post.getLocationName());
                        holder.locationLayout.setVisibility(View.VISIBLE);
                    } else {
                        holder.locationLayout.setVisibility(View.GONE);
                    }
>>>>>>> 8a2c616d44f5bc17f2d47b124eac2ebddb0df47e

        // Get user details from the 'users' node using the userId
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(post.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get user profile image URL, username, and locationName safely
                    String userName = dataSnapshot.child("userName").getValue(String.class);
                    String userProfileImageUrl = dataSnapshot.child("userProfileImage").getValue(String.class);

                    // Set the username if available
                    if (userName != null) {
                        holder.usernameTextView.setText(userName);
                    }
                    // Load user profile image safely
                    if (userProfileImageUrl != null && !userProfileImageUrl.isEmpty()) {
                        Picasso.get()
                                .load(userProfileImageUrl)
                                .placeholder(R.drawable.ic_profile_placeholder)  // Show placeholder while loading
                                .error(R.drawable.ic_profile_placeholder)  // Fallback image in case of error
                                .into(holder.userProfileImageView);
                    } else {
                        // If no profile image, set the placeholder directly
                        holder.userProfileImageView.setImageResource(R.drawable.ic_profile_placeholder);
                    }
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }
        });

        holder.userProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Assuming 'userId' is available in your context
                String userId = post.getUserId(); // Replace with the actual method to get the user ID

                // Create an Intent to start ProfileActivity
                Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                intent.putExtra("USER_ID", userId); // Pass the user ID as an extra
                view.getContext().startActivity(intent); // Start the activity
            }
        });

        holder.Sharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePost(post.getPostImageUrl(), post.getCaption(), view.getContext());

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


        }

    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 8a2c616d44f5bc17f2d47b124eac2ebddb0df47e
