package com.dilip.conectivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView postsRecyclerView;
    private PostsAdapter postsAdapter;
    private List<Post> postList;
    private DatabaseReference postsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        postsRecyclerView = view.findViewById(R.id.postsRecyclerView);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postList = new ArrayList<>();
        postsAdapter = new PostsAdapter(postList);
        postsRecyclerView.setAdapter(postsAdapter);

        // Firebase reference to the "posts" node
        postsRef = FirebaseDatabase.getInstance().getReference("posts");

        // Load posts from Firebase
        loadPosts();

        return view;
    }

    private void loadPosts() {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();  // Clear previous list to avoid duplication
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);  // Retrieve the post object

                    if (post != null) {
                        postList.add(post);  // Add the post to the list
                    }
                }
                postsAdapter.notifyDataSetChanged();  // Notify the adapter to refresh the RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if the Firebase request fails
            }
        });
    }
}
