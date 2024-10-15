package com.dilip.conectivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView userName, userEmail, userPhone, userBio, postsHeading;
    private ImageView profileImage;
    private Button signOutButton, editProfileButton;
    private RecyclerView profilePostsRecyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef, postsRef;
    private ProfilePostsAdapter postAdapter; // Use new adapter
    private List<Post> postList;

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
        postsRef = FirebaseDatabase.getInstance().getReference("posts");

        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        userPhone = view.findViewById(R.id.userPhone);
        userBio = view.findViewById(R.id.userBio);
        profileImage = view.findViewById(R.id.profileImage);
        signOutButton = view.findViewById(R.id.signOutButton);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        postsHeading = view.findViewById(R.id.postsHeading); // Assuming this is the ID for your posts label
        postsHeading.setText("Your Posts");

        profilePostsRecyclerView = view.findViewById(R.id.profilePostsRecyclerView);
        profilePostsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // Grid layout (3 columns)
        postList = new ArrayList<>();
        postAdapter = new ProfilePostsAdapter(postList);
        profilePostsRecyclerView.setAdapter(postAdapter);

        // Load user details and posts
        loadUserDetails();
        loadUserPosts();

        signOutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        return view;
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

                    userName.setText(name);
                    userEmail.setText(email);
                    userPhone.setText(phone);
                    userBio.setText(bio);
                    if (image != null && !image.isEmpty()) {
                        Picasso.get()
                                .load(image)
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .error(R.drawable.ic_profile_placeholder)
                                .into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void loadUserPosts() {
        String userId = mAuth.getCurrentUser().getUid();

        postsRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    postList.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
