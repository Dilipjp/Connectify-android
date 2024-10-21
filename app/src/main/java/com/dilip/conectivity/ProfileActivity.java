package com.dilip.conectivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

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
    private int postsCount = 0;
    private int followersCount = 0;
    private int followingCount = 0;
    private  String userId, currentUserId;
    private FirebaseAuth mAuth;

    private DatabaseReference usersRef, currentUserRef, postsRef;

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
        userId = getIntent().getStringExtra("USER_ID");
//        mAuth = FirebaseAuth.getInstance();
//        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Initialize Firebase Database Reference
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        currentUserRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        postsRef = FirebaseDatabase.getInstance().getReference("posts");
//        if(Objects.equals(userId, currentUserId)){
//            followButton.setVisibility(View.GONE);
//        }
        // Fetch User Data
        fetchUserData();
    }

    private void fetchUserData() {
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Assuming your User class has these fields
                    String profileImageUrl = snapshot.child("userProfileImage").getValue(String.class);
                    String name = snapshot.child("userName").getValue(String.class);
                    String bio = snapshot.child("userBio").getValue(String.class);
                    String email = snapshot.child("userEmail").getValue(String.class);
                    String phone = snapshot.child("userPhone").getValue(String.class);

                    loadPostCount();
                    // Load followers count for the current user
                    loadFollowerCount();
                    // Load followings count for the current user
                    loadFollowingsCount();


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

    private void loadFollowingsCount() {

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long followingCount = 0;

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Check if the current user is listed as a follower under other users
                    if (userSnapshot.hasChild("followers") && userSnapshot.child("followers").hasChild(userId)) {
                        followingCount++;
                    }
                }
                userFollowing.setText(followingCount + " Following");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error, for example, by showing a Toast
                 }
        });
    }

    private void loadFollowerCount() {

        usersRef.child(userId).child("followers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long followerCount = dataSnapshot.getChildrenCount();
                userFollowers.setText(followerCount + " Followers");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getContext(), "Failed to load followers.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPostCount() {
        {

            postsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long postCount = dataSnapshot.getChildrenCount();
                    userPosts.setText(postCount + " Posts");
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Toast.makeText(getContext(), "Failed to load posts.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
