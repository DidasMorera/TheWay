package com.pdm.theway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.pdm.theway.ui.home.model.Songs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowSongActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    List<Songs> mSongs;
    DatabaseReference reference;
    MediaPlayer mediaPlayer;

    ValueEventListener valueEventListener;

    SongsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_song);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBarshowSongs);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSongs = new ArrayList<>();

        adapter = new SongsAdapter(ShowSongActivity.this, mSongs);
        recyclerView.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance().getReference("songs");

        progressBar.setVisibility(View.INVISIBLE);
        valueEventListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mSongs.clear();
                for (DataSnapshot dss : snapshot.getChildren()){
                    Songs songs = dss.getValue(Songs.class);
                    songs.setMediaId(dss.getKey());
                    mSongs.add(songs);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ShowSongActivity.this,""+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reference.removeEventListener(valueEventListener);
    }

    public void playSong(List<Songs> arrayListSongs, int adapterPosition) throws IOException {
        Songs songs = arrayListSongs.get(adapterPosition);

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setDataSource(songs.getSongUrl());
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.prepareAsync();
    }
}