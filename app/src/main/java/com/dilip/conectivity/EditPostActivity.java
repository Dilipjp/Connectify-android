package com.dilip.conectivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditPostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText postCaptionEditText;
    private ImageView postImageView;
    private Button saveButton, changeImageButton;

    private String postId, postCaption, postImageUrl;
    private DatabaseReference postsRef;
    private StorageReference postImagesStorageRef;

    private Uri newImageUri; // To store the URI of the new image selected
    private ProgressDialog progressDialog; // Loading spinner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        // Initialize Views
        postCaptionEditText = findViewById(R.id.postCaptionEditText);
        postImageView = findViewById(R.id.postImageView);
        saveButton = findViewById(R.id.saveButton);
        changeImageButton = findViewById(R.id.changeImageButton);

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false); // Prevent dismissing during operations

        // Get data from intent
        Intent intent = getIntent();
        postId = intent.getStringExtra("POST_ID");
        postCaption = intent.getStringExtra("POST_CAPTION");
        postImageUrl = intent.getStringExtra("POST_IMAGE_URL");

        // Set initial values
        postCaptionEditText.setText(postCaption);
        Picasso.get().load(postImageUrl).into(postImageView);

        // Initialize Firebase references
        postsRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);
        postImagesStorageRef = FirebaseStorage.getInstance().getReference("post_images");

        // Change image button click
        changeImageButton.setOnClickListener(v -> openImagePicker());

        // Save button click
        saveButton.setOnClickListener(v -> {
            String updatedCaption = postCaptionEditText.getText().toString().trim();
            if (!updatedCaption.isEmpty()) {
                // Show the loading spinner
                progressDialog.show();

                // Check if a new image is selected
                if (newImageUri != null) {
                    uploadNewImageAndSavePost(updatedCaption);
                } else {
                    // Only update caption if no new image is selected
                    updatePostCaptionOnly(updatedCaption);
                }
            } else {
                postCaptionEditText.setError("Caption cannot be empty");
            }
        });
    }

    // Method to open the image picker
    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Post Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            newImageUri = data.getData();
            // Display the new image in the ImageView
            postImageView.setImageURI(newImageUri);
        }
    }

    // Method to upload new image and update the post
    private void uploadNewImageAndSavePost(String updatedCaption) {
        if (newImageUri != null) {
            // Generate a unique name for the image in Firebase Storage
            StorageReference fileReference = postImagesStorageRef.child(postId + ".jpg");

            fileReference.putFile(newImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the new image URL after upload
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String newPostImageUrl = uri.toString();
                            // Update the post with the new image URL and updated caption
                            updatePostWithNewImage(updatedCaption, newPostImageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditPostActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss(); // Dismiss the loading spinner
                    });
        }
    }

    // Method to update post in Firebase with new image URL and caption
    private void updatePostWithNewImage(String updatedCaption, String newPostImageUrl) {
        Map<String, Object> postUpdates = new HashMap<>();
        postUpdates.put("caption", updatedCaption);
        postUpdates.put("postImageUrl", newPostImageUrl);

        postsRef.updateChildren(postUpdates)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss(); // Dismiss the loading spinner

                    if (task.isSuccessful()) {
                        Toast.makeText(EditPostActivity.this, "Post updated successfully", Toast.LENGTH_SHORT).show();
                        finish();  // Close the activity after saving
                    } else {
                        Toast.makeText(EditPostActivity.this, "Failed to update post", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to update only the caption if no new image is selected
    private void updatePostCaptionOnly(String updatedCaption) {
        postsRef.child("caption").setValue(updatedCaption)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss(); // Dismiss the loading spinner

                    if (task.isSuccessful()) {
                        Toast.makeText(EditPostActivity.this, "Caption updated successfully", Toast.LENGTH_SHORT).show();
                        finish();  // Close the activity after saving
                    } else {
                        Toast.makeText(EditPostActivity.this, "Failed to update caption", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}