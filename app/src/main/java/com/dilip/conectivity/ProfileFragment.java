package com.dilip.conectivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private TextView userName, userEmail, userPhone, userBio;
    private ImageView profileImage;
    private Button signOutButton, editProfileButton;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase and UI elements
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        userPhone = view.findViewById(R.id.userPhone);
        userBio = view.findViewById(R.id.userBio);
        profileImage = view.findViewById(R.id.profileImage);
        signOutButton = view.findViewById(R.id.signOutButton);
        editProfileButton = view.findViewById(R.id.editProfileButton);

        // Load user details from Firebase Realtime Database
        loadUserDetails();

        // Sign out functionality
        signOutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Clear back stack
            startActivity(intent);
            getActivity().finish();  // Close current activity
        });
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadUserDetails() {
        String userId = mAuth.getCurrentUser().getUid();

        usersRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("userName").getValue(String.class);
                    String email = dataSnapshot.child("userEmail").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    String image = dataSnapshot.child("userProfileImage").getValue(String.class);
                    String bio = dataSnapshot.child("userBio").getValue(String.class);

                    userName.setText(name);
                    userEmail.setText(email);
                    userPhone.setText(phone);
                    // profileImage
                    userBio.setText(bio);
                    if (image != null && !image.isEmpty()) {
                        Picasso.get()
                                .load(image)
                                .placeholder(R.drawable.ic_profile_placeholder) // Use placeholder while loading
                                .error(R.drawable.ic_profile_placeholder) // Fallback in case of an error
                                .into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error, e.g., show a Toast message
            }
        });
    }
}