package com.pdm.theway.ui.home.model;

import com.google.firebase.database.Exclude;

public class Songs {
    public String mediaId, songDuration,title, songUrl;

    public Songs(String songDuration, String title, String songUrl) {

        if(title.trim().equals("")){
            title = "No title";
        }

        this.songDuration = songDuration;
        this.title = title;
        this.songUrl = songUrl;

    }
@Exclude
    public String getMediaId() {
        return mediaId;
    }
@Exclude
    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

}
