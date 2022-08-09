package com.example.androidx;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import io.realm.Realm;

public class UpdateNote extends AppCompatActivity {

    Realm realm;

    EditText titleUpdate;
    EditText descriptionUpdate;
    MaterialButton updatebtn;
    Note note;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);



        realm = Realm.getDefaultInstance();
        titleUpdate = findViewById(R.id.titleupdate);
        descriptionUpdate = findViewById(R.id.descriptionupdate);
        updatebtn = findViewById(R.id.updatebtn);


        String title = getIntent().getExtras().getString("data_title");
        note=realm.where(Note.class).equalTo("title", title).findFirst();


        titleUpdate.setText(note.getTitle());
        descriptionUpdate.setText(note.getDescription());

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(title);
            }
        });
    }

    private void updateData(String title){

        realm.executeTransactionAsync(
                realm1 -> {
                    Note notes = realm1.where(Note.class).equalTo("title", title).findFirst();

                    notes.setTitle(titleUpdate.getText().toString());
                    notes.setDescription(descriptionUpdate.getText().toString());

                    finish();
                },
                () -> Toast.makeText(UpdateNote.this, "Berhasil Update", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(UpdateNote.this, "Gagal", Toast.LENGTH_LONG).show()
        );
    }
}