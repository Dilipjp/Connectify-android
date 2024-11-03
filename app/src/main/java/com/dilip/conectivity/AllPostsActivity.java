package com.dilip.conectivity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

public class AllPostsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUserPosts;
    private List<Post> postList;
    private UserPostAdapter userPostAdapter;
    private DatabaseReference postsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_posts);

        recyclerViewUserPosts = findViewById(R.id.recyclerViewUserPosts);
        recyclerViewUserPosts.setLayoutManager(new LinearLayoutManager(this));

        postList = new ArrayList<>();
        userPostAdapter = new UserPostAdapter(postList);
        recyclerViewUserPosts.setAdapter(userPostAdapter);

        mAuth = FirebaseAuth.getInstance();
        postsRef = FirebaseDatabase.getInstance().getReference("posts");

        // Load user's posts
        loadUserPosts();
    }

    private void loadUserPosts() {
        String userId = mAuth.getCurrentUser().getUid();

        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();  // Clear list before adding new data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null) {
                        post.setPostId(snapshot.getKey());  // Set the post ID for editing/deleting
                        postList.add(post);
                    }
                }
                userPostAdapter.notifyDataSetChanged();  // Refresh the RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AllPostsActivity.this, "Failed to load posts.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}