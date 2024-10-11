package com.dilip.conectivity;

import static android.content.Intent.getIntent;

import android.os.Bundle;
import android.support.annotation.NonNull;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

    private RecyclerView followersRecyclerView;
    private FollowersAdapter followersAdapter;
    private List<User> followersList;
    private String profileUserId;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        profileUserId = getIntent().getStringExtra("profileUserId");
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        followersRecyclerView = findViewById(R.id.followersRecyclerView);
        followersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        followersList = new ArrayList<>();
        followersAdapter = new FollowersAdapter(followersList, this);
        followersRecyclerView.setAdapter(followersAdapter);

        loadFollowers();
    }

    private void loadFollowers() {
        usersRef.child(profileUserId).child("followers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        followersList.clear();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String followerId = snapshot.getKey();

                            // Fetch user details by followerId
                            usersRef.child(followerId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                    User follower = userSnapshot.getValue(User.class);
                                    if (follower != null) {
                                        followersList.add(follower);
                                    }

                                    followersAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
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
}