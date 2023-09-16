package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;

import java.sql.Timestamp;

public class NoteActivity extends AppCompatActivity {

    EditText titleEditText , contentEditText ;
    ImageButton saveNoteBtn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);



        titleEditText = findViewById(R.id.note_title);
        contentEditText = findViewById(R.id.note_content);
        saveNoteBtn = findViewById(R.id.save_note_btn);

        saveNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
    }

    private void saveNote()
    {
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();
        if (noteTitle == null || noteTitle.isEmpty()){
            titleEditText.setError("Title is require");
            return;
        }


        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        saveNoteToFirebase(note);

    }

    void saveNoteToFirebase(Note note)
    {
        DatabaseReference databaseReference;
        databaseReference = Utility.getCollectionReferenceForNotes();
        databaseReference.setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Utility.showToast(NoteActivity.this,"Note added successfully");
                    finish();
                }
                else {
                    Utility.showToast(NoteActivity.this,"Failed while adding note");

                }
            }
        });

    }

}