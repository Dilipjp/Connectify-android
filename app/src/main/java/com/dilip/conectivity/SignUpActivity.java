package com.dilip.conectivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText, userNameEditText, userBioEditText;
    private Button signUpButton;
    private TextView signInTextView, errorTextView, successTextView;
    private RadioGroup roleRadioGroup;
    private FirebaseAuth mAuth;
    private DatabaseReference usersDatabase;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        usersDatabase = FirebaseDatabase.getInstance().getReference("users");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        userNameEditText = findViewById(R.id.userNameEditText);
        userBioEditText = findViewById(R.id.userBioEditText);
        signUpButton = findViewById(R.id.signUpButton);
        signInTextView = findViewById(R.id.signInTextView);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        errorTextView = findViewById(R.id.errorTextView);
        successTextView = findViewById(R.id.successTextView);


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
                Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
            }
        });

        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });
    }

    private void createNewAccount() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String userName = userNameEditText.getText().toString().trim();
        String userBio = userBioEditText.getText().toString().trim();
        String userRole = getUserRole();
        if (TextUtils.isEmpty(userName)) {
            userNameEditText.setError("Username is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password should be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }

        if (TextUtils.isEmpty(userRole)) {
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText("User role is required");
            return;
        }
        errorTextView.setVisibility(View.GONE);
        successTextView.setVisibility(View.GONE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            successTextView.setVisibility(View.VISIBLE);
                            successTextView.setText("Account created success.");
                            saveUserDetails(userId, userName, email, userBio, userRole);
                        }
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Account creation failed.";
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.setText(errorMessage);  // Show error message in TextView
                    }
                });
    }

    private String getUserRole() {
        RadioGroup roleRadioGroup = findViewById(R.id.roleRadioGroup);  // Get reference to the RadioGroup
        int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();  // Get the ID of the selected RadioButton

        if (selectedRoleId == -1) {
            // No role is selected
            return null;
        }

        RadioButton selectedRadioButton = findViewById(selectedRoleId);  // Find the selected RadioButton
        return selectedRadioButton.getText().toString();  // Return the text of the selected RadioButton
    }


    private void saveUserDetails(String userId, String userName, String email, String userBio, String userRole) {
        HashMap<String, Object> userDetails = new HashMap<>();
        userDetails.put("userName", userName);
        userDetails.put("userEmail", email);
        userDetails.put("userProfileImage", "https://via.placeholder.com/150");
        userDetails.put("userBio", userBio);
        userDetails.put("userRole", userRole);  // Save the selected user role
        userDetails.put("userStatus", "active");
        userDetails.put("userCreatedAt", System.currentTimeMillis());

        usersDatabase.child(userId).setValue(userDetails)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        successTextView.setVisibility(View.GONE);
                        startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.setText("Failed to save user details.");
                    }
                });
    }
}

