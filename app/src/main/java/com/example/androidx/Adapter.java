package com.example.androidx;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;

import io.realm.Realm;
import io.realm.RealmResults;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    Context context;
    RealmResults<Note> notesList;

    public Adapter(Context context, RealmResults<Note> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    @NonNull
    @Override
    public Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.MyViewHolder holder, int position) {
        Note note = notesList.get(position);
        holder.titleOutput.setText(note.getTitle());
        holder.descriptionOutput.setText(note.getDescription());
        holder.locationOutput.setText(note.getLocation());

        String formatedTime = DateFormat.getDateTimeInstance().format(note.getCreatedTime());
        holder.timeOutput.setText(formatedTime);

        holder.itemList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder alerBuilder = new AlertDialog.Builder(v.getContext());

                String[] items = {"Update", "Delete"};
                alerBuilder.setItems(items, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Bundle bundle = new Bundle();
                            Intent intent = new Intent(v.getContext(), UpdateNote.class);

                            intent.putExtra("data_title", note.getTitle());
                            v.getContext().startActivity(intent);

                            break;
                        case 1:
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            note.deleteFromRealm();
                            realm.commitTransaction();
                            Toast.makeText(context,"Note deleted",Toast.LENGTH_SHORT).show();

                            break;
                    }
                });

                alerBuilder.create();
                alerBuilder.show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {

        return notesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titleOutput;
        TextView descriptionOutput;
        TextView timeOutput;
        TextView locationOutput;
        RelativeLayout itemList;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleOutput = itemView.findViewById(R.id.titleoutput);
            descriptionOutput = itemView.findViewById(R.id.descriptionoutput);
            locationOutput = itemView.findViewById(R.id.locationoutput);
            timeOutput = itemView.findViewById(R.id.timeoutput);
            itemList = itemView.findViewById(R.id.relative);
        }
    }
}