package com.dilip.conectivity;

import android.os.Bundle;
import android.util.Log;
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
    private TextView userNameTextView;
    private Button followButton;
    private String userIdToFollow; // Declare userIdToFollow without initializing it here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userNameTextView = findViewById(R.id.userNameTextView);
        followButton = findViewById(R.id.followButton);

        // Get the user ID passed from the previous activity
        userIdToFollow = getIntent().getStringExtra("userIdToFollow");
        Log.d("UserProfileActivity", "Received user ID: " + userIdToFollow);

        followButton.setOnClickListener(v -> handleFollowButtonClick());

        loadUserProfile();
    }

    private void loadUserProfile() {
        Log.d("UserProfileActivity", "Loading profile for user ID: " + userIdToFollow);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userIdToFollow);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the username from the database
                    String username = dataSnapshot.child("userName").getValue(String.class);
                    // Set the username in the TextView
                    userNameTextView.setText(username != null ? username : "No username available");
                } else {
                    userNameTextView.setText("User not found");
                    Log.e("UserProfileActivity", "User not found in database for ID: " + userIdToFollow);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserProfileActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }



    private void checkFollowStatus() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference followsRef = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserId)
                .child("following")
                .child(userIdToFollow);

        followsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    followButton.setText("Unfollow"); // User is followed
                } else {
                    followButton.setText("Follow"); // User is not followed
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserProfileActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void handleFollowButtonClick() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference followsRef = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUserId)
                .child("following")
                .child(userIdToFollow);

        followsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Unfollow user
                    followsRef.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            followButton.setText("Follow"); // Update button text
                        } else {
                            Log.e("UserProfileActivity", "Error unfollowing user");
                        }
                    });
                } else {
                    // Follow user
                    followsRef.setValue(true).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            followButton.setText("Unfollow"); // Update button text
                        } else {
                            Log.e("UserProfileActivity", "Error following user");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserProfileActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }
}
