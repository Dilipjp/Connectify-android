package com.dilip.conectivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private Context context;
    private List<Report> reportList;

    public ReportAdapter(Context context, List<Report> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reportList.get(position);

        // Set the reason and timestamp directly
        holder.reasonTextView.setText("Report message: " + report.getReason());
//        holder.timestampTextView.setText(String.valueOf(report.getTimestamp())); // Format as needed
        long timestamp = report.getTimestamp();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault()); // Adjust the format as needed
        String formattedDate = sdf.format(new Date(timestamp));
        holder.timestampTextView.setText(formattedDate);

        // Fetch user name based on userId of the reporter
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(report.getUserId());
        usersRef.child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.getValue(String.class);
                    holder.userIdTextView.setText("Reporter: " + userName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors
            }
        });

        // Fetch post details based on postId
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts").child(report.getPostId());
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String caption = dataSnapshot.child("caption").getValue(String.class);
                    String postImageUrl = dataSnapshot.child("postImageUrl").getValue(String.class);
                    String postUserId = dataSnapshot.child("userId").getValue(String.class); // Get the userId of the post uploader

                    holder.captionTextView.setText("Caption: " + caption);
                    // Load image using Picasso
                    Picasso.get().load(postImageUrl).into(holder.postImageView);

                    // Now fetch the uploader's username based on the postUserId
                    DatabaseReference uploaderRef = FirebaseDatabase.getInstance().getReference("users").child(postUserId);
                    uploaderRef.child("userName").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String uploaderUserName = dataSnapshot.getValue(String.class);
                                holder.uploaderTextView.setText("Uploader: " + uploaderUserName); // Set uploader's username
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle potential errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors
            }
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView reasonTextView;
        TextView timestampTextView;
        TextView userIdTextView;
        TextView captionTextView; // New TextView for caption
        ImageView postImageView; // New ImageView for post image
        TextView uploaderTextView; // New TextView for uploader's username

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reasonTextView = itemView.findViewById(R.id.report_reason);
            timestampTextView = itemView.findViewById(R.id.report_timestamp);
            userIdTextView = itemView.findViewById(R.id.report_user_id);
            captionTextView = itemView.findViewById(R.id.report_caption); // Initialize caption TextView
            postImageView = itemView.findViewById(R.id.report_post_image); // Initialize ImageView for post image
            uploaderTextView = itemView.findViewById(R.id.report_uploader); // Initialize TextView for uploader's username
        }
    }
}


