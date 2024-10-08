package com.dilip.conectivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log; // Import Log for debugging
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {
    private TextView usernameTextView;
    private Button followButton;
    private String profileUserId;
    private DatabaseReference usersRef;
    private String currentUserId; // Get this from your authentication method

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile); // Ensure you have a layout

        usernameTextView = findViewById(R.id.usernameTextView);
        followButton = findViewById(R.id.followButton); // Your follow/unfollow button

        // Get current user ID
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        profileUserId = getIntent().getStringExtra("profileUserId");

        // Log the profileUserId
        Log.d("UserProfileActivity", "Profile User ID: " + profileUserId);

        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        loadUserProfile(); // Call to load user profile
        setupFollowButton();
    }

    private void loadUserProfile() {
        usersRef.child(profileUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    usernameTextView.setText(username);
                    // Log the retrieved username
                    Log.d("UserProfileActivity", "Username: " + username);
                    // Check if current user follows the profile user
                    checkIfFollowing();
                } else {
                    Log.d("UserProfileActivity", "No data exists for this user ID");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserProfileActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void checkIfFollowing() {
        usersRef.child(currentUserId).child("following").child(profileUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            followButton.setText("Unfollow");
                        } else {
                            followButton.setText("Follow");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("UserProfileActivity", "Database error: " + databaseError.getMessage());
                    }
                });
    }

    private void setupFollowButton() {
        followButton.setOnClickListener(v -> {
            // Toggle follow/unfollow state
            if (followButton.getText().toString().equals("Follow")) {
                followUser();
            } else {
                unfollowUser();
            }
        });
    }

    private void followUser() {
        usersRef.child(currentUserId).child("following").child(profileUserId).setValue(true);
        followButton.setText("Unfollow");
    }

    private void unfollowUser() {
        usersRef.child(currentUserId).child("following").child(profileUserId).removeValue();
        followButton.setText("Follow");
    }
}
