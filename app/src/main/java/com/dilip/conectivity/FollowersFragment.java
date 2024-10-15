package com.dilip.conectivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowersFragment extends Fragment {

    private RecyclerView usersRecyclerView;
    private FollowerAdapter followersAdapter;
    private List<User> userList;
    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;

    public FollowersFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_followers, container, false);

        // Initialize views
        usersRecyclerView = view.findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Set LayoutManager for RecyclerView

        // Initialize user list and adapter
        userList = new ArrayList<>(); // Initialize the user list
        followersAdapter = new FollowerAdapter(userList); // Create adapter with user list
        usersRecyclerView.setAdapter(followersAdapter); // Set the adapter to the RecyclerView

        // Initialize Firebase references
        mAuth = FirebaseAuth.getInstance(); // Get Firebase Auth instance
        String currentUserId = mAuth.getCurrentUser().getUid(); // Get the current user's ID
        usersRef = FirebaseDatabase.getInstance().getReference("users"); // Reference to the users node

        // Load all users
        loadUsers(); // Call method to load users from Firebase

        return view;
    }

    private void loadUsers() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear(); // Clear the list before adding
                String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null; // Safely get current user ID

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    if (user != null) {
                        // Ensure userId is set from the snapshot key
                        user.setUserId(snapshot.getKey());

                        if (!user.getUserId().equals(currentUserId)) {
                            userList.add(user);
                        }
                    }
                }

                followersAdapter.notifyDataSetChanged(); // Refresh RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load users.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}