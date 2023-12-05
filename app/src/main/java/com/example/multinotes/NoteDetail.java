package com.example.multinotes;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NoteDetail extends Activity {

    EditText titleText, contentText;
    TextView pageTitleTextView, pageImgPathTextView, deleteNote;
    ImageButton btnDone;
    Button btnAddImg, btnAlarm;
    private final int PICK_IMAGE_REQUEST = 22;
    ImageView imageView;
    boolean isEditNote = false;
    String title, content, docId, imgPath;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        titleText = findViewById(R.id.title_text);
        contentText = findViewById(R.id.content_text);
        btnDone = findViewById(R.id.btnDone);
        btnAddImg = findViewById(R.id.btnImage);
        imageView = findViewById(R.id.imageView);
        pageTitleTextView = findViewById(R.id.page_title);
        pageImgPathTextView = findViewById(R.id.textImagePath);
        deleteNote = findViewById(R.id.delete_note);
        btnAlarm = findViewById(R.id.btnAlarm);

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        imgPath = getIntent().getStringExtra("img");
        docId = getIntent().getStringExtra("docId");

        if(docId != null && !docId.isEmpty()){
            isEditNote = true;
        }

        if(isEditNote){
            pageTitleTextView.setText("Edit Your Note");
            btnAddImg.setText("Change Image");
            titleText.setText(title);
            contentText.setText(content);
            pageImgPathTextView.setText(imgPath);
            Glide.with(this).load(Uri.parse(pageImgPathTextView.getText().toString())).into(imageView);
            deleteNote.setVisibility(View.VISIBLE);
            btnAlarm.setVisibility(View.VISIBLE);
        }

        btnDone.setOnClickListener((v) -> saveNote());
        btnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        deleteNote.setOnClickListener((v) -> deleteNoteFromFireBase());
    }

    void deleteNoteFromFireBase() {
        DocumentReference documentReference;
        documentReference = Utitlity.getCollectionReferenceForNotes().document(docId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Utitlity.showToast(NoteDetail.this, "Delete Successfully");
                    finish();
                } else {
                    Utitlity.showToast(NoteDetail.this, "Delete Failed");

                }
            }
        });
    }

    void saveNote() {
        String noteTitle = titleText.getText().toString();
        String noteContent = contentText.getText().toString();
        String noteImg = pageImgPathTextView.getText().toString();
        if (noteTitle == null || noteTitle.isEmpty()) {
            titleText.setError("Title is required");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());
        note.setImg(noteImg);
        saveNoteToFirebase(note);

    }

    void saveNoteToFirebase(Note note) {
        DocumentReference documentReference;
        if(isEditNote){
            documentReference = Utitlity.getCollectionReferenceForNotes().document(docId);
        }
        else {
            documentReference = Utitlity.getCollectionReferenceForNotes().document();
        }
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Utitlity.showToast(NoteDetail.this, "Successfully");
                    finish();
                } else {
                    Utitlity.showToast(NoteDetail.this, "Fail");

                }
            }
        });
    }

    void SelectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            Uri filePath = data.getData();
            pageImgPathTextView.setText(filePath.toString());
            Glide.with(this).load(Uri.parse(pageImgPathTextView.getText().toString())).into(imageView);
        }
    }
    private void scheduleNotification (Notification notification , long delay) {

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            Intent serviceIntent = new Intent(this, MyForegroundService.class);
            this.startForegroundService(serviceIntent);
        }

        Intent notificationIntent = new Intent( this, MyNotificationPublisher. class ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION , notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent.FLAG_UPDATE_CURRENT ) ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;

        alarmManager.set(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
    }
    protected Notification getNotification () {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        builder.setContentTitle( "Nhắc Nhở" ) ;
        builder.setContentText(title) ;
        builder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet (DatePicker view , int year , int monthOfYear , int dayOfMonth) {
            myCalendar .set(Calendar. YEAR , year) ;
            myCalendar .set(Calendar. MONTH , monthOfYear) ;
            myCalendar .set(Calendar. DAY_OF_MONTH , dayOfMonth) ;
            updateLabel() ;
        }
    } ;
    public void setDate (View view) {
        new DatePickerDialog(NoteDetail.this, date ,
                myCalendar .get(Calendar. YEAR ) ,
                myCalendar .get(Calendar. MONTH ) ,
                myCalendar .get(Calendar. DAY_OF_MONTH )
        ).show() ;
    }
    protected void updateLabel () {
        String myFormat = "dd/MM/yy" ; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat , Locale. getDefault ()) ;Date date = myCalendar .getTime() ;
        btnAlarm.setText(sdf.format(date)) ;
        //+ 10000 la 10 giay sau, date.getTime() la ngay duoc chon
        scheduleNotification(getNotification() , date.getTime() + 10000);
    }

}