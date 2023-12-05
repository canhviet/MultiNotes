package com.example.multinotes;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;

public class Utitlity {
    static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    static CollectionReference getCollectionReferenceForNotes(){
        return FirebaseFirestore.getInstance().collection("notes").document("12345").collection("my_notes");
    }

    static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(timestamp.toDate());
    }
}

