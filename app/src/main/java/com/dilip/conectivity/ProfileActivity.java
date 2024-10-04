package com.dilip.conectivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView userName;
    private TextView userBio;
    private TextView userEmail;
    private TextView userPhone;
    private TextView userPosts;
    private TextView userFollowers;
    private TextView userFollowing;
    private Button followButton;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Views
        profileImage = findViewById(R.id.profileImage);
        userName = findViewById(R.id.userName);
        userBio = findViewById(R.id.userBio);
        userEmail = findViewById(R.id.userEmail);
        userPhone = findViewById(R.id.userPhone);
        userPosts = findViewById(R.id.userPosts);
        userFollowers = findViewById(R.id.userFollowers);
        userFollowing = findViewById(R.id.userFollowing);
        followButton = findViewById(R.id.followButton);

        // Get userId from Intent
        String userId = getIntent().getStringExtra("USER_ID");

        // Initialize Firebase Database Reference
        usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Fetch User Data
        fetchUserData();
    }

    private void fetchUserData() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Assuming your User class has these fields
                    String profileImageUrl = snapshot.child("userProfileImage").getValue(String.class);
                    String name = snapshot.child("userName").getValue(String.class);
                    String bio = snapshot.child("userBio").getValue(String.class);
                    String email = snapshot.child("userEmail").getValue(String.class);
                    String phone = snapshot.child("userPhone").getValue(String.class);
                    // Initialize counts to default values
                    int postsCount = 0;
                    int followersCount = 0;
                    int followingCount = 0;

// Safely retrieve postsCount
                    if (snapshot.child("userPosts").exists()) {
                        Integer postsValue = snapshot.child("userPosts").getValue(Integer.class);
                        if (postsValue != null) {
                            postsCount = postsValue;
                        }
                    }

// Safely retrieve followersCount
                    if (snapshot.child("userFollowersCount").exists()) {
                        Integer followersValue = snapshot.child("userFollowersCount").getValue(Integer.class);
                        if (followersValue != null) {
                            followersCount = followersValue;
                        }
                    }

// Safely retrieve followingCount
                    if (snapshot.child("userFollowingCount").exists()) {
                        Integer followingValue = snapshot.child("userFollowingCount").getValue(Integer.class);
                        if (followingValue != null) {
                            followingCount = followingValue;
                        }
                    }



                    // Set the data to the views
                    Picasso.get()
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_profile_placeholder) // Placeholder while loading
                            .into(profileImage);

                    userName.setText(name);
                    userBio.setText(bio);
                    userEmail.setText(email);
                    userPhone.setText(phone);
                    userPosts.setText(postsCount + " Posts");
                    userFollowers.setText(followersCount + " Followers");
                    userFollowing.setText(followingCount + " Following");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
