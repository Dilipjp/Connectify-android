package com.dilip.conectivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private RecyclerView followersRecyclerView;
    private TextView emptyView;
    private FollowersAdapter followersAdapter;
    private List<User> followersList;
    private DatabaseReference usersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_followers_fragment, container, false);

        // Initialize UI components
        followersRecyclerView = view.findViewById(R.id.followersRecyclerView);
        emptyView = view.findViewById(R.id.emptyView);

        // Initialize Firebase Database reference
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        // Set up RecyclerView
        followersList = new ArrayList<>();
        followersAdapter = new FollowersAdapter(followersList, getActivity()); // Pass context here
        followersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        followersRecyclerView.setAdapter(followersAdapter);

        // Load followers
        loadFollowers();

        return view;
    }

    private void loadFollowers() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        usersRef.child(currentUserId).child("followers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followersList.clear(); // Clear the current list

                if (dataSnapshot.exists()) {
                    for (DataSnapshot followerSnapshot : dataSnapshot.getChildren()) {
                        String followerId = followerSnapshot.getKey();
                        loadFollowerDetails(followerId);
                    }
                } else {
                    // Show empty view if there are no followers
                    emptyView.setVisibility(View.VISIBLE);
                    followersRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void loadFollowerDetails(String followerId) {
        usersRef.child(followerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User follower = dataSnapshot.getValue(User.class);
                    if (follower != null) {
                        followersList.add(follower);
                    }

                    // Notify adapter of data changes
                    followersAdapter.notifyDataSetChanged();

                    // Hide empty view if we have followers now
                    if (!followersList.isEmpty()) {
                        emptyView.setVisibility(View.GONE);
                        followersRecyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }
}
