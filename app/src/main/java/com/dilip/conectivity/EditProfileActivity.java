package com.dilip.conectivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView userProfileImage;
    private EditText userNameEditText, userBioEditText;
    private Button changeProfileImageButton, saveProfileButton;

    // Assuming you're using Firebase to store user data
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private StorageReference storageReference;

    private Uri imageUri;  // To store selected image URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase references
        mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        storageReference = FirebaseStorage.getInstance().getReference("ProfileImages");

        // Initialize views
        userProfileImage = findViewById(R.id.userProfileImage);
        userNameEditText = findViewById(R.id.userNameEditText);
        userBioEditText = findViewById(R.id.userBioEditText);
        changeProfileImageButton = findViewById(R.id.changeProfileImageButton);
        saveProfileButton = findViewById(R.id.saveProfileButton);

        // Load current user data from Firebase (Profile image, username, bio)
        loadUserData();

        // Change profile image on button click
        changeProfileImageButton.setOnClickListener(v -> {
            // Open gallery or image picker
            openImagePicker();
        });

        // Save profile changes
        saveProfileButton.setOnClickListener(v -> {
            saveUserProfile();
        });
    }

    // Function to load user data from Firebase
    private void loadUserData() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Retrieve user details
                String userName = dataSnapshot.child("userName").getValue(String.class);
                String userBio = dataSnapshot.child("userBio").getValue(String.class);
                String profileImageUrl = dataSnapshot.child("userProfileImage").getValue(String.class);

                userNameEditText.setText(userName);
                userBioEditText.setText(userBio);
                if (profileImageUrl != null) {
                    Picasso.get().load(profileImageUrl).into(userProfileImage); // Using Picasso for image loading
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    // Function to open image picker for profile picture
    private void openImagePicker() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            userProfileImage.setImageURI(imageUri);
        }
    }

    // Function to save user profile changes to Firebase
    private void saveUserProfile() {
        String userName = userNameEditText.getText().toString().trim();
        String userBio = userBioEditText.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            userNameEditText.setError("Username is required");
            return;
        }

        // Save the image to Firebase Storage if it was changed
        if (imageUri != null) {
            StorageReference fileRef = storageReference.child(mAuth.getCurrentUser().getUid() + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    // Save other user details along with image URL
                    saveUserDetails(userName, userBio, imageUrl);
                                    });
            }).addOnFailureListener(e -> {
                // Handle failure
            });
        } else {
            // Save only the username and bio if no image change
            saveUserDetails(userName, userBio, null);
        }
    }

    private void saveUserDetails(String userName, String userBio, @Nullable String profileImageUrl) {
        // Save details in the database
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("userName", userName);
        userDetails.put("userBio", userBio);
        if (profileImageUrl != null) {
            userDetails.put("userProfileImage", profileImageUrl);
        }

        userRef.updateChildren(userDetails).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(EditProfileActivity.this, "Profile update failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}