package com.example.multinotes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.checkerframework.checker.nullness.qual.NonNull;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    Context context;
    public NoteAdapter(@androidx.annotation.NonNull FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@androidx.annotation.NonNull NoteViewHolder holder, int position, @androidx.annotation.NonNull Note model) {
        holder.titleTextView.setText(model.title);
        holder.contentTextView.setText(model.content);
        holder.timestampTextView.setText(Utitlity.timestampToString(model.timestamp));
        holder.imgPath.setText(model.img);
        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(context, NoteDetail.class);
            intent.putExtra("title", model.title);
            intent.putExtra("content", model.content);
            intent.putExtra("img", model.img);
            String docId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId", docId);
            context.startActivity(intent);
        });
    }

    @androidx.annotation.NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_note, parent, false);
        return new NoteViewHolder(view);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView, timestampTextView, imgPath;

        public NoteViewHolder(@NonNull View itemView){
            super(itemView);
            titleTextView = itemView.findViewById(R.id.note_title_text_view);
            contentTextView = itemView.findViewById(R.id.note_content_text_view);
            timestampTextView = itemView.findViewById(R.id.textDate);
            imgPath = itemView.findViewById(R.id.textPath);
        }
    }
}
