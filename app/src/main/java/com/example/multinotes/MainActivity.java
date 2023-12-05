package com.example.multinotes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class MainActivity extends Activity {
    ImageButton btnAdd;
    RecyclerView recyclerView;
    NoteAdapter noteAdapter;
    NoteDetail noteDetail;
    @Override
    protected void onCreate(Bundle SaveInstanceState) {
        super.onCreate(SaveInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyler_view);
        btnAdd = findViewById(R.id.imageButton);
        btnAdd.setOnClickListener((v) -> startActivity(new Intent(MainActivity.this, NoteDetail.class)));

        setupRecycleView();
    }
    void setupRecycleView(){
        Query query = Utitlity.getCollectionReferenceForNotes().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options, this);
        recyclerView.setAdapter(noteAdapter);
        noteDetail = new NoteDetail();
    }

    @Override
    protected void onStart(){
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    protected void onResume(){
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}
