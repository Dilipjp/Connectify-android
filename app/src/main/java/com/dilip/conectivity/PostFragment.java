package com.dilip.conectivity;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;

public class PostFragment extends Fragment {

    private ImageView postImage;
    private Button selectImageButton, uploadPostButton, postCommentButton;
    private EditText postCaption, commentEditText;
    private Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference postsRef;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private RecyclerView commentsRecyclerView;

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


        postCommentButton = view.findViewById(R.id.postCommentButton);
         commentEditText = view.findViewById(R.id.commentEditText);
         commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("PostImages");
        postsRef = FirebaseDatabase.getInstance().getReference("posts");
        progressDialog = new ProgressDialog(getContext());

        // Select image from gallery
        selectImageButton.setOnClickListener(v -> openImagePicker());

        // Upload post
        uploadPostButton.setOnClickListener(v -> uploadPost());

        String postId = "your_post_id";

        postCommentButton.setOnClickListener(v -> {
            String commentText = commentEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(commentText)) {
               postComment(postId, commentText);
                commentEditText.setText(""); // Clear the input field after posting
            } else {
                commentEditText.setError("Comment cannot be empty");
            }
        });



        return view;
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


    private void postComment(String postId, String commentText) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get the current user's ID
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("posts").child(postId).child("comments");

        String commentId = commentsRef.push().getKey(); // Generate a unique ID for the comment
        long timestamp = System.currentTimeMillis();

        Comment comment = new Comment(commentId, postId, userId, commentText, timestamp);

        // Save the comment to Firebase
        if (commentId != null) {
            commentsRef.child(commentId).setValue(comment).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Comment posted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to post comment", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
        }
    }
}




