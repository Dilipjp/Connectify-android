package com.dilip.conectivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostFragment extends Fragment {

    private ImageView postImage;
    private Button selectImageButton, uploadPostButton, submitCommentButton;
    private EditText postCaption, commentEditText;
    private Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference postsRef;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private RecyclerView commentsRecyclerView;
    private CommentsAdapter commentsAdapter;
    private List<Comment> commentsList;
    private String postId;

    private static final int IMAGE_PICK_CODE = 1000;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        postImage = view.findViewById(R.id.postImage);
        selectImageButton = view.findViewById(R.id.selectImageButton);
        uploadPostButton = view.findViewById(R.id.uploadPostButton);
        postCaption = view.findViewById(R.id.postCaption);
        commentEditText = view.findViewById(R.id.commentEditText);
        submitCommentButton = view.findViewById(R.id.submitCommentButton);
        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("PostImages");
        postsRef = FirebaseDatabase.getInstance().getReference("posts");
        progressDialog = new ProgressDialog(getContext());

        commentsList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(commentsList);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsRecyclerView.setAdapter(commentsAdapter);

        // Select image from gallery
        selectImageButton.setOnClickListener(v -> openImagePicker());

        // Upload post
        uploadPostButton.setOnClickListener(v -> uploadPost());

        // Submit comment
        submitCommentButton.setOnClickListener(v -> submitComment());

        if (getArguments() != null) {
            postId = getArguments().getString("POST_ID");
            if (postId != null) {
                loadComments(postId);  // Load comments if postId is valid
            }
        } else {
            Toast.makeText(getContext(), "Error: Post ID not found!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void submitComment() {
        String commentText = commentEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(commentText)) {
            String commentId = postsRef.child(postId).child("comments").push().getKey(); // Generate a unique ID for the comment
            Comment comment = new Comment(commentId, postId, mAuth.getCurrentUser().getUid(), commentText); // No timestamp
            postsRef.child(postId).child("comments").child(commentId).setValue(comment)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            commentEditText.setText(""); // Clear input
                            Toast.makeText(getContext(), "Comment added!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to submit comment", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(postImage);
        }
    }

    private void loadComments(String postId) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("posts").child(postId).child("comments");
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList.clear(); // Clear the list to avoid duplicate entries
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    if (comment != null) {
                        commentsList.add(comment); // Add comment to the list
                    }
                }
                commentsAdapter.updateComments(commentsList); // Update the adapter with new data
                commentsAdapter.notifyDataSetChanged(); // Notify adapter to refresh the RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load comments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadPost() {
        String caption = postCaption.getText().toString().trim();

        if (imageUri == null) {
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(caption)) {
            postCaption.setError("Caption is required");
            return;
        }

        progressDialog.setMessage("Uploading post...");
        progressDialog.show();

        String userId = mAuth.getCurrentUser().getUid();
        StorageReference fileRef = storageReference.child(userId + "_" + System.currentTimeMillis() + ".jpg");

        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String postImageUrl = uri.toString();
                // Save post details to Realtime Database
                savePostToDatabase(postImageUrl, caption, userId);
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Post uploaded successfully!", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "Failed to upload post", Toast.LENGTH_SHORT).show();
        });
    }

    private void savePostToDatabase(String postImageUrl, String caption, String userId) {
        String postId = postsRef.push().getKey();

        Map<String, Object> postDetails = new HashMap<>();
        postDetails.put("postId", postId);
        postDetails.put("postImageUrl", postImageUrl);
        postDetails.put("caption", caption);
        postDetails.put("userId", userId);
        postDetails.put("timestamp", System.currentTimeMillis());

        if (postId != null) {
            postsRef.child(postId).setValue(postDetails);

            // Start PostDetailActivity with the newly created postId
            Intent intent = new Intent(getActivity(), PostDetailActivity.class);
            intent.putExtra("POST_ID", postId); // Pass the post ID
            startActivity(intent);
        }
    }
}
