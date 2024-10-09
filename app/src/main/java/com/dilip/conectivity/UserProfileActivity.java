package com.dilip.conectivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private Button followButton; // Declare the follow button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize UI components
        userNameTextView = findViewById(R.id.userNameTextView);
        followButton = findViewById(R.id.followButton); // Initialize the follow button

        // Set up the follow button click listener
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFollowButtonClick(); // Call the method to handle follow/unfollow logic
            }
        });

        loadUserProfile(); // Load user profile
    }

    private void handleFollowButtonClick() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userIdToFollow = "some_user_id"; // Replace with actual user ID of the profile being viewed

        // Toggle follow/unfollow action
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
                            // Handle error
                        }
                    });
                } else {
                    // Follow user
                    followsRef.setValue(true).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            followButton.setText("Unfollow"); // Update button text
                        } else {
                            // Handle error
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void loadUserProfile() {
        String userId = "some_user_id"; // Replace with actual user ID
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("userName").getValue(String.class);
                    if (userNameTextView != null) {
                        userNameTextView.setText(username != null ? username : "No username available");
                    }
                } else {
                    userNameTextView.setText("User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
