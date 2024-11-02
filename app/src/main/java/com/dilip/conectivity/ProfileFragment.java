package com.dilip.conectivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private TextView userName, userEmail, userPhone, userBio, userPosts, userFollowers, userFollowing, userWarnings;
    private ImageView profileImage;
    private Button signOutButton, editProfileButton, usersButton, postButton;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef, postsRef;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase and UI elements
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        postsRef = FirebaseDatabase.getInstance().getReference("posts");  // Reference to posts node


        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        userPhone = view.findViewById(R.id.userPhone);
        userBio = view.findViewById(R.id.userBio);
        userWarnings = view.findViewById(R.id.userWarnings);
        profileImage = view.findViewById(R.id.profileImage);
        userPosts = view.findViewById(R.id.userPosts);
        userFollowers = view.findViewById(R.id.userFollowers);
        userFollowing = view.findViewById(R.id.userFollowing);
        signOutButton = view.findViewById(R.id.signOutButton);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        usersButton = view.findViewById(R.id.usersButton);
        postButton = view.findViewById(R.id.postButton);

        // Load user details from Firebase Realtime Database
        loadUserDetails();
        // Load post count for the current user
        loadPostCount();
        // Load followers count for the current user
        loadFollowerCount();
        // Load followings count for the current user
        loadFollowingsCount();
        // Load warning messages
        loadUserWarnings();

        // Sign out functionality
        signOutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Clear back stack
            startActivity(intent);
            getActivity().finish();  // Close current activity
        });
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });
        usersButton.setOnClickListener(view1 ->  {
                Intent intent = new Intent(getActivity(), ModeratorUsersActivity.class);
                startActivity(intent);
        });
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AllPostsActivity.class);
                startActivity(intent);
            }
        });



        return view;
    }

//    private void loadUserWarnings() {
//        String userId = mAuth.getCurrentUser().getUid();
//
//        usersRef.child(userId).child("userWarnings").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    StringBuilder warningsBuilder = new StringBuilder();
//                    for (DataSnapshot warningSnapshot : dataSnapshot.getChildren()) {
//                        String message = warningSnapshot.child("message").getValue(String.class);
//                        String postId = warningSnapshot.child("postId").getValue(String.class);
//
//                        warningsBuilder.append("• ").append(message+postId).append("\n");
//                    }
//                    userWarnings.setText(warningsBuilder.toString().trim());
//                    userWarnings.setVisibility(View.VISIBLE);  // Make warnings visible if they exist
//                } else {
//                    userWarnings.setVisibility(View.GONE);  // Hide warnings if none exist
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getContext(), "Failed to load warnings.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
private void loadUserWarnings() {
    String userId = mAuth.getCurrentUser().getUid();

    usersRef.child(userId).child("userWarnings").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                StringBuilder warningsBuilder = new StringBuilder();
                int totalWarnings = (int) dataSnapshot.getChildrenCount();
                int[] completedWarnings = {0};  // To track completed caption fetches

                for (DataSnapshot warningSnapshot : dataSnapshot.getChildren()) {
                    String message = warningSnapshot.child("message").getValue(String.class);
                    String postId = warningSnapshot.child("postId").getValue(String.class);

                    // Fetch caption from posts node using postId
                    postsRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                            String caption = postSnapshot.child("caption").getValue(String.class);

                            // Append message and caption to the warningsBuilder
                            warningsBuilder.append("• ").append(message)
                                    .append("\n  Caption: ").append(caption != null ? caption : "No caption available")
                                    .append("\n\n");

                            // Increment completed warnings count
                            completedWarnings[0]++;

                            // If all warnings have been processed, update the TextView
                            if (completedWarnings[0] == totalWarnings) {
                                userWarnings.setText(warningsBuilder.toString().trim());
                                userWarnings.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Failed to load caption.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                userWarnings.setVisibility(View.GONE);  // Hide warnings if none exist
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(getContext(), "Failed to load warnings.", Toast.LENGTH_SHORT).show();
        }
    });
}


    private void loadUserDetails() {
        String userId = mAuth.getCurrentUser().getUid();

        usersRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("userName").getValue(String.class);
                    String email = dataSnapshot.child("userEmail").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    String image = dataSnapshot.child("userProfileImage").getValue(String.class);
                    String bio = dataSnapshot.child("userBio").getValue(String.class);
                    String role = dataSnapshot.child("userRole").getValue(String.class);

                    userName.setText(name);
                    userEmail.setText(email);
                    userPhone.setText(phone);
                    // profileImage
                    userBio.setText(bio);
                    if (image != null && !image.isEmpty()) {
                        Picasso.get()
                                .load(image)
                                .placeholder(R.drawable.ic_profile_placeholder) // Use placeholder while loading
                                .error(R.drawable.ic_profile_placeholder) // Fallback in case of an error
                                .into(profileImage);
                    }


                        if (email != null && role != null) {
                            // Check if the user is an admin
                            if (email.equals("admin@gmail.com") && role.equals("Admin")) {
                                // Add admin-specific Buttons
                                usersButton.setVisibility(View.VISIBLE);
                                postButton.setVisibility(View.VISIBLE);
                            }else if(role.equals("Moderator")) {
                                usersButton.setVisibility(View.VISIBLE);
//                                postButton.setVisibility(View.VISIBLE);
                            }else {
                                postButton.setVisibility(View.GONE);
                                usersButton.setVisibility(View.GONE);
                            }
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error, e.g., show a Toast message
            }
        });
    }
    private void loadPostCount()

    {
        String userId = mAuth.getCurrentUser().getUid();

        postsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long postCount = dataSnapshot.getChildrenCount();
                userPosts.setText(postCount + " Posts");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load posts.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFollowerCount() {
        String userId = mAuth.getCurrentUser().getUid();

        usersRef.child(userId).child("followers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long followerCount = dataSnapshot.getChildrenCount();
                userFollowers.setText(followerCount + " Followers");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load followers.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadFollowingsCount() {
        String currentUserId = mAuth.getCurrentUser().getUid();

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long followingCount = 0;

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Check if the current user is listed as a follower under other users
                    if (userSnapshot.hasChild("followers") && userSnapshot.child("followers").hasChild(currentUserId)) {
                        followingCount++;
                    }
                }

                // Update the UI with the following count
                userFollowing.setText(followingCount + " Following");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error, for example, by showing a Toast
                Toast.makeText(getContext(), "Failed to load followings.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}