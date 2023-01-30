package com.pdm.theway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pdm.theway.ui.home.model.Songs;

import java.io.IOException;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongsAdapterViewHolder> {

    Context context;
    List<Songs> arrayListSongs;

    public SongsAdapter(Context context, List<Songs> arrayListSongs){
        this.context = context;
        this.arrayListSongs = arrayListSongs;
    }

    @NonNull
    @Override
    public SongsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.song_item,parent,false);

        return new SongsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsAdapterViewHolder holder, int position) {

        Songs songs = arrayListSongs.get(position);
        holder.textViewTitle.setText(songs.getTitle());
        holder.textViewduration.setText(songs.getSongDuration());
    }

    @Override
    public int getItemCount() {
        return arrayListSongs.size();
    }

    public class SongsAdapterViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewTitle, textViewduration;

        public SongsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.Songs_Title1);
            textViewduration = itemView.findViewById(R.id.song_duration);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            try {
                ((ShowSongActivity)context).playSong(arrayListSongs, getAdapterPosition());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
