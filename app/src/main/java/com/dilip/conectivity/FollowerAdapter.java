package com.dilip.conectivity;

import static android.app.PendingIntent.getActivity;
import static java.security.AccessController.getContext;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.UserViewHolder> {

    private List<User> userList;

    public FollowerAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follower, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String targetUserId = user.getUserId();

        if (currentUserId != null && targetUserId != null) {
            DatabaseReference followerRef = userRef.child(targetUserId).child("followers").child(currentUserId);

            followerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        holder.followButton.setText("Unfollow");
                    } else {
                        holder.followButton.setText("Follow");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        public TextView userNameTextView;
        public ImageView userProfileImageView;
        public Button followButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            userProfileImageView = itemView.findViewById(R.id.userProfileImageView);
            followButton = itemView.findViewById(R.id.followButton);
        }

        // Bind the user data to the ViewHolder
        public void bind(User user) {
            userNameTextView.setText(user.getUserName());

            // Load profile image using Picasso
            Picasso.get()
                    .load(user.getUserProfileImage())
                    .placeholder(R.drawable.ic_profile_placeholder)  // Show placeholder while loading
                    .error(R.drawable.ic_profile_placeholder)  // Fallback image in case of error
                    .into(userProfileImageView);

            followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
                    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Current user's ID
                    String targetUserId = user.getUserId(); // ID of the user being followed/unfollowed

                    if (currentUserId != null && targetUserId != null) {
                        // Path to the target user's followers list
                        DatabaseReference followerRef = userRef.child(targetUserId).child("followers").child(currentUserId);

                        followerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    // Already following, so unfollow
                                    followerRef.removeValue();
                                    Toast.makeText(itemView.getContext(), "Unfollowed " + user.getUserName(), Toast.LENGTH_SHORT).show();
                                    followButton.setText("Follow"); // Change button text to "Follow"
                                } else {
                                    // Not following, so follow
                                    followerRef.setValue(true);
                                    Toast.makeText(itemView.getContext(), "Followed " + user.getUserName(), Toast.LENGTH_SHORT).show();
                                    followButton.setText("Unfollow"); // Change button text to "Unfollow"
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(itemView.getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }
}