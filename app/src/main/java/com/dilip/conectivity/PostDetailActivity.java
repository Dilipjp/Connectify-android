package com.dilip.conectivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class PostDetailActivity extends AppCompatActivity {

    private ImageView postImageView;
    private TextView postCaptionTextView;
    private RecyclerView commentsRecyclerView;
    private CommentsAdapter commentsAdapter;
    private List<Comment> commentsList;

    private DatabaseReference postsRef;
    private String postId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        postImageView = findViewById(R.id.postImageView);
        postCaptionTextView = findViewById(R.id.postCaptionTextView);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        EditText commentEditText = findViewById(R.id.commentEditText);
        Button submitCommentButton = findViewById(R.id.submitCommentButton);

        // Initialize Firebase Database Reference
        postsRef = FirebaseDatabase.getInstance().getReference("posts");

        // Get postId from Intent
        postId = getIntent().getStringExtra("POST_ID");

        // Set up RecyclerView for comments
        commentsList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(commentsList);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentsAdapter);

        // Load post details and comments
        loadPostDetails(postId);

        // Load comments
        loadComments(postId);

        // Set up the button click listener to add a comment
        submitCommentButton.setOnClickListener(v -> {
            String commentText = commentEditText.getText().toString().trim();
            if (!commentText.isEmpty()) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                addComment(postId, commentText, userId);
                commentEditText.setText("");
            } else {
                Toast.makeText(PostDetailActivity.this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPostDetails(String postId) {
        DatabaseReference postRef = postsRef.child(postId);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                if (post != null) {
                    Picasso.get().load(post.getPostImageUrl()).into(postImageView);
                    postCaptionTextView.setText(post.getCaption());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PostDetailActivity.this, "Failed to load post details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadComments(String postId) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("posts").child(postId).child("comments");
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList.clear(); // Clear list to avoid duplicate entries
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    if (comment != null) {
                        commentsList.add(comment); // Add comment to list
                    }
                }
                commentsAdapter.updateComments(commentsList); // Update the adapter with new data
                commentsAdapter.notifyDataSetChanged(); // Notify adapter to refresh the RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PostDetailActivity.this, "Failed to load comments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addComment(String postId, String commentText, String userId) {
        DatabaseReference commentsRef = postsRef.child(postId).child("comments");
        String commentId = commentsRef.push().getKey();
        Comment comment = new Comment(commentId, postId, userId, commentText);

        if (commentId != null) {
            commentsRef.child(commentId).setValue(comment)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(PostDetailActivity.this, "Comment added!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PostDetailActivity.this, "Failed to add comment", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
