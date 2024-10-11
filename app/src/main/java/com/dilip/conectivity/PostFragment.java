package com.dilip.conectivity;

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
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostFragment extends Fragment {

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 2000;

    private ImageView postImage;
    private Button selectImageButton, uploadPostButton, selectLocationButton;
    private EditText postCaption;
    private TextView selectedLocationText;
    private Uri imageUri;
    private String postLocation = "";
    private StorageReference storageReference;
    private DatabaseReference postsRef;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        postImage = view.findViewById(R.id.postImage);
        selectImageButton = view.findViewById(R.id.selectImageButton);
        uploadPostButton = view.findViewById(R.id.uploadPostButton);
        postCaption = view.findViewById(R.id.postCaption);
        selectLocationButton = view.findViewById(R.id.searchLocationButton);
        selectedLocationText = view.findViewById(R.id.postLocation);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("PostImages");
        postsRef = FirebaseDatabase.getInstance().getReference("posts");
        progressDialog = new ProgressDialog(getContext());

        // Initialize the Places API
        Places.initialize(getContext(), "AIzaSyCjElsVqyTBv23vxQWkOy2s3RcclGtQeWA");

        // Select image from gallery
        selectImageButton.setOnClickListener(v -> openImagePicker());

        // Select location
        selectLocationButton.setOnClickListener(v -> openPlacePicker());

        // Upload post
        uploadPostButton.setOnClickListener(v -> uploadPost());

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void openPlacePicker() {
        // Set the fields to specify which types of place data to return
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        // Start the autocomplete intent
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(getContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(postImage);
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            postLocation = place.getName();  // Store the selected location
            selectedLocationText.setText(postLocation);  // Display the selected location
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getContext(), "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
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

        if (TextUtils.isEmpty(postLocation)) {
            Toast.makeText(getContext(), "Please select a location", Toast.LENGTH_SHORT).show();
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
                savePostToDatabase(postImageUrl, caption, userId, postLocation);

                progressDialog.dismiss();
                Toast.makeText(getContext(), "Post uploaded successfully!", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "Failed to upload post", Toast.LENGTH_SHORT).show();
        });
    }

    private void savePostToDatabase(String postImageUrl, String caption, String userId, String postLocation) {
        String postId = postsRef.push().getKey();

        Map<String, Object> postDetails = new HashMap<>();
        postDetails.put("postId", postId);
        postDetails.put("postImageUrl", postImageUrl);
        postDetails.put("caption", caption);
        postDetails.put("userId", userId);
        postDetails.put("locationName", postLocation);
        postDetails.put("timestamp", System.currentTimeMillis());

        if (postId != null) {
            // Save the post details to the posts node
            postsRef.child(postId).setValue(postDetails)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            updateUserPostsCount(userId);
                        }
                    });
        }
    }

    private void updateUserPostsCount(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.child("userPosts").setValue(com.google.firebase.database.ServerValue.increment(1));
    }
}
