package com.dilip.conectivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersActivity extends AppCompatActivity {

    private RecyclerView usersRecyclerView;
    private UsersAdapter usersAdapter;
    private DatabaseReference usersRef;
    private List<ModeratorUser> userList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        usersAdapter = new UsersAdapter(userList);
        usersRecyclerView.setAdapter(usersAdapter);

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        loadUsers();
    }

    private void loadUsers() {
        usersRef.orderByChild("userRole").equalTo("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    ModeratorUser user = userSnapshot.getValue(ModeratorUser.class);
                    if (user != null) {
                        user.setUserId(userSnapshot.getKey());
                        userList.add(user);
                    }
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminUsersActivity.this, "Failed to load users.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

        private final List<ModeratorUser> users;

        public UsersAdapter(List<ModeratorUser> users) {
            this.users = users;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_user, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            ModeratorUser user = users.get(position);
            holder.userNameTextView.setText(user.getUserName());
            holder.userStatusTextView.setText(user.getUserStatus());
            Picasso.get().load(user.getUserProfileImage()).into(holder.userProfileImageView);

            // Update button text based on the current user status
            String currentStatus = user.getUserStatus();
            holder.actionButton.setText(currentStatus.equals("active") ? "Deactivate" : "Activate");

            holder.actionButton.setOnClickListener(v -> {
                // Toggle the user status between "active" and "inactive"
                String newStatus = user.getUserStatus().equals("active") ? "deactivated" : "active";

                // Show confirmation dialog
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Confirm Status Change")
                        .setMessage("Are you sure you want to " + (newStatus.equals("active") ? "activate" : "deactivate") + " this user?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Update the userStatus field in Firebase
                            usersRef.child(user.getUserId()).child("userStatus").setValue(newStatus)
                                    .addOnSuccessListener(aVoid -> {
                                        // Update status in the local list and UI
                                        user.setUserStatus(newStatus);
                                        holder.userStatusTextView.setText(newStatus);
                                        holder.actionButton.setText(newStatus.equals("active") ? "Deactivate" : "Activate");
                                        loadUsers();
                                        Toast.makeText(AdminUsersActivity.this, "User status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(AdminUsersActivity.this, "Failed to update user status.", Toast.LENGTH_SHORT).show());
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            });


            holder.viewPostsButton.setOnClickListener(v -> {
                // Redirect to ModeratorUserPostActivity with userId
                Intent intent = new Intent(AdminUsersActivity.this, ModeratorUserPostsActivity.class);
                intent.putExtra("userId", user.getUserId()); // Pass userId to the activity
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        class UserViewHolder extends RecyclerView.ViewHolder {
            ImageView userProfileImageView;
            TextView userNameTextView, userStatusTextView;
            Button viewPostsButton, actionButton;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                userProfileImageView = itemView.findViewById(R.id.userProfileImageView);
                userNameTextView = itemView.findViewById(R.id.userNameTextView);
                userStatusTextView = itemView.findViewById(R.id.userStatusTextView);
                viewPostsButton = itemView.findViewById(R.id.viewPostsButton);
                actionButton = itemView.findViewById(R.id.actionButton);
            }
        }
    }
}
