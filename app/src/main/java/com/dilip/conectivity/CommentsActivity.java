package com.dilip.conectivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView commentsRecyclerView;
    private EditText commentEditText;
    private Button postCommentButton;
    private List<Comment> commentList;
    private CommentsAdapter commentsAdapter;
    private String postId;
    private DatabaseReference postRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        // Initialize UI components
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentEditText = findViewById(R.id.commentEditText);
        postCommentButton = findViewById(R.id.postCommentButton);

        // Get postId from Intent
        postId = getIntent().getStringExtra("POST_ID");
        postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);

        // Initialize RecyclerView
        commentList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(commentList);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentsAdapter);

        // Load existing comments
        loadComments();

        // Post a comment when button is clicked
        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
    }

    // Load comments from Firebase
    private void loadComments() {
        postRef.child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear(); // Clear old data
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        // Fetch user details from the users node
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(comment.getUserId());
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                String userName = userSnapshot.child("userName").getValue(String.class);
                                String userProfileImage = userSnapshot.child("userProfileImage").getValue(String.class);

                                comment.setUserName(userName);
                                comment.setUserProfileImage(userProfileImage);

                                commentList.add(comment);
                                commentsAdapter.notifyDataSetChanged(); // Notify the adapter for the new comment
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(CommentsActivity.this, "Failed to load user details.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CommentsActivity.this, "Failed to load comments.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Post a new comment to Firebase
    private void postComment() {
        String commentText = commentEditText.getText().toString().trim();

        if (!commentText.isEmpty()) {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

            // Get current user details
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userName = snapshot.child("userName").getValue(String.class);

                    // Create a unique key for the comment
                    String commentId = postRef.child("comments").push().getKey();
                    long timestamp = System.currentTimeMillis();

                    // Create comment data
                    Map<String, Object> commentData = new HashMap<>();
                    commentData.put("commentText", commentText);
                    commentData.put("userId", currentUserId);
                    commentData.put("userName", userName);
                    commentData.put("timestamp", timestamp);

                    // Save comment to Firebase
                    postRef.child("comments").child(commentId).setValue(commentData);

                    // Increment comment count
                    postRef.child("commentCount").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int commentCount = dataSnapshot.exists() ? dataSnapshot.getValue(Integer.class) : 0;
                            postRef.child("commentCount").setValue(commentCount + 1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                        }
                    });

                    // Clear the input field
                    commentEditText.setText("");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        } else {
            Toast.makeText(this, "Comment cannot be empty.", Toast.LENGTH_SHORT).show();
        }
    }
}
