package com.example.journal_firebaseauth_app.ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.journal_firebaseauth_app.R;
import com.example.journal_firebaseauth_app.model.Journal;

import java.util.List;

public class JournalRecyclerAdapter extends RecyclerView.Adapter<JournalViewholder> {

    Context context;
    List<Journal> journalList;

    public JournalRecyclerAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    public JournalRecyclerAdapter() {
    }

    @NonNull
    @Override
    public JournalViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context)
                .inflate(R.layout.journal_list_item,parent,false);
        return new JournalViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewholder holder, int position) {
        Journal journal=journalList.get(position);
        String imgUrl;

        holder.title.setText(journal.getTitle());
        holder.name.setText(journal.getUsername());
        holder.thoughts.setText(journal.getThoughts());
        imgUrl=journal.getImageUrl();

        String timeAgo=(String) DateUtils.getRelativeTimeSpanString(journal.getTimeAdded()
                .getSeconds()*1000);
        holder.dateAdded.setText(timeAgo);
        //using glide library to display the images

        Glide.with(context).load(imgUrl).fitCenter().into(holder.image);

    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

}
