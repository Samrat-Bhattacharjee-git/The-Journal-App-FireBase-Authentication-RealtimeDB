package com.example.journal_firebaseauth_app.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.journal_firebaseauth_app.R;

public class JournalViewholder extends RecyclerView.ViewHolder {

    TextView title,thoughts,dateAdded,name;
    ImageView image,shareButton;
    String userID,username;


    public JournalViewholder(@NonNull View itemView) {
        super(itemView);

        title=itemView.findViewById(R.id.namexml);
        thoughts=itemView.findViewById(R.id.desxml);
        dateAdded=itemView.findViewById(R.id.datexml);
        name=itemView.findViewById(R.id.titlexml);
        image=itemView.findViewById(R.id.imgxml);
        shareButton=itemView.findViewById(R.id.sharebtnxml);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //share the post
            }
        });

    }
}
