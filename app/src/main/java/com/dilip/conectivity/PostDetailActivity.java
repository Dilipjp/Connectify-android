package com.dilip.conectivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class PostDetailActivity extends AppCompatActivity {
    private RecyclerView commentsRecyclerView;
    private EditText commentEditText;
    private Button postCommentButton;
    private CommentsAdapter commentsAdapter;
    private List<Comment> commentsList = new ArrayList<>();
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail); // Your layout

        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentEditText = findViewById(R.id.commentEditText);
        postCommentButton = findViewById(R.id.postCommentButton);

        // Initialize RecyclerView
        commentsAdapter = new CommentsAdapter(commentsList);
        commentsRecyclerView.setAdapter(commentsAdapter);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the postId from the Intent
        postId = getIntent().getStringExtra("POST_ID"); // Ensure the key matches how you're passing it

        // Load comments
        loadComments();

        // Post comment action
        postCommentButton.setOnClickListener(view -> {
            String commentText = commentEditText.getText().toString();
            if (!commentText.isEmpty()) {
                postComment(commentText);
            } else {
                Toast.makeText(PostDetailActivity.this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postComment(String commentText) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user ID
        String commentId = FirebaseDatabase.getInstance().getReference().push().getKey(); // Generate a unique comment ID
        long timestamp = System.currentTimeMillis(); // Get current timestamp

        // Create a new comment object with all required fields
        Comment newComment = new Comment(commentId, postId, userId, commentText, timestamp);

        // Reference to the specific post's comments node in Firebase
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId).child("comments");

        // Push the new comment to the database
        postRef.child(commentId).setValue(newComment).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                commentEditText.setText(""); // Clear the input field
                // Add the new comment to the comments list and refresh the RecyclerView
                commentsList.add(newComment);
                commentsAdapter.notifyItemInserted(commentsList.size() - 1);
                commentsRecyclerView.scrollToPosition(commentsList.size() - 1); // Scroll to the latest comment
                Toast.makeText(PostDetailActivity.this, "Comment posted!", Toast.LENGTH_SHORT).show();
            } else {
                // Log the error
                Log.e("PostDetailActivity", "Failed to post comment: " + task.getException().getMessage());
                Toast.makeText(PostDetailActivity.this, "Failed to post comment.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadComments() {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId).child("comments");

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList.clear(); // Clear the current list of comments
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentsList.add(comment); // Add each comment to the list
                }
                commentsAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PostDetailActivity", "Database Error: " + databaseError.getMessage());
            }
        });
    }
}
