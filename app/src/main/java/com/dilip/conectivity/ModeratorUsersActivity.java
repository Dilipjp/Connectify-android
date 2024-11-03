package com.dilip.conectivity;

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

public class ModeratorUsersActivity extends AppCompatActivity {

    private RecyclerView usersRecyclerView;
    private UsersAdapter usersAdapter;
    private DatabaseReference usersRef;
    private List<ModeratorUser> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderator_users);

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
                Toast.makeText(ModeratorUsersActivity.this, "Failed to load users.", Toast.LENGTH_SHORT).show();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.moderator_item_user, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            ModeratorUser user = users.get(position);
            holder.userNameTextView.setText(user.getUserName());
            holder.userStatusTextView.setText(user.getUserStatus());
            Picasso.get().load(user.getUserProfileImage()).into(holder.userProfileImageView);

            holder.viewPostsButton.setOnClickListener(v -> {
                // Redirect to ModeratorUserPostActivity with userId
                Intent intent = new Intent(ModeratorUsersActivity.this, ModeratorUserPostsActivity.class);
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
            Button viewPostsButton;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                userProfileImageView = itemView.findViewById(R.id.userProfileImageView);
                userNameTextView = itemView.findViewById(R.id.userNameTextView);
                userStatusTextView = itemView.findViewById(R.id.userStatusTextView);
                viewPostsButton = itemView.findViewById(R.id.viewPostsButton);
            }
        }
    }
}
