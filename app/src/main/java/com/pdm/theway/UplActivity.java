package com.pdm.theway;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.pdm.theway.databinding.ActivityNavBinding;
import com.pdm.theway.databinding.ActivityUplBinding;
import com.pdm.theway.ui.home.model.Songs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class UplActivity extends AppCompatActivity {
    private ActivityUplBinding binding;
    TextInputLayout textInputLayout;
    TextView textViewimage;
    ProgressBar progressBar;
    Uri audiouri;
    StorageReference mStorageRef;
    StorageTask mUploadTask;
    DatabaseReference referenceSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upl);

        binding = ActivityUplBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        textInputLayout = findViewById(R.id.songTitle);
        textViewimage = findViewById(R.id.textviewSongFileSelected);
        progressBar = findViewById(R.id.progressBar);

        referenceSongs = FirebaseDatabase.getInstance().getReference().child("songs");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("songs");


        binding.btnAudio.setOnClickListener(this::openAudioFile);


        binding.btnUpl.setOnClickListener(this::uploadAudioToFirebase);

    }
    public void openAudioFile(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && resultCode == RESULT_OK) {
            assert data != null;
            if (data.getData() !=null) {
                audiouri = data.getData();
                String fileName = getFileName(audiouri);
                textViewimage.setText(fileName);
            }
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if(uri.getScheme().equals("content")){

            try (Cursor cursor = getContentResolver().query(uri, null, null, null)) {

                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if(result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut!=-1){
                result = result.substring(cut +1);
            }
        }
        return result;
    }

    public void uploadAudioToFirebase(View view){
        if (textViewimage.getText().toString().equals("No File Selected")){
            Toast.makeText(this,"Please Select an Image",Toast.LENGTH_SHORT).show();
        }else{
            if (mUploadTask!=null && mUploadTask.isInProgress()){
                Toast.makeText(this
                        , "Song upload is already in Progress", Toast.LENGTH_SHORT).show();
            }else{
                uploadFile();
            }
        }
    }

    private void uploadFile() {
        if(audiouri != null) {
            String durationText;
            Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show();

            progressBar.setVisibility(View.VISIBLE);

            StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(audiouri));


            int durationInMillis = findSongDuration(audiouri);

            if (durationInMillis == 0) {
                durationText = "NA";
            }
            durationText = getDurationFromMilli(durationInMillis);

            String finalDurationText = durationText;
            mUploadTask = storageReference.putFile(audiouri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        Songs songs = new Songs(Objects.requireNonNull(textInputLayout.getEditText()).getText().toString(),
                                finalDurationText, uri.toString());

                        String uploaId = referenceSongs.push().getKey();
                        assert uploaId != null;
                        referenceSongs.child(uploaId).setValue(songs);
                        progressBar.setVisibility(View.GONE);
                    }))
                    .addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    });

        }else{
            Toast.makeText(this,"No File Selected to upload", Toast.LENGTH_SHORT).show();

        }
    }

    private String getDurationFromMilli(int durationInMillis) {
        Date date = new Date(durationInMillis);

        SimpleDateFormat simple = new SimpleDateFormat("mm:ss", Locale.getDefault());

        return simple.format(date);


    }

    private int findSongDuration(Uri audiouri) {
        int timeMillisec;
        try {
            MediaMetadataRetriever receiver = new MediaMetadataRetriever();
            receiver.setDataSource(this, audiouri);
            String time = receiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            timeMillisec = Integer.parseInt(time);

            receiver.release();
            return timeMillisec;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    private String getFileExtension(Uri audiouri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(audiouri));
    }

    public void openSongsActivity(View view) {
        Intent intent = new Intent(UplActivity.this, ShowSongActivity.class);
        startActivity(intent);
    }
}
