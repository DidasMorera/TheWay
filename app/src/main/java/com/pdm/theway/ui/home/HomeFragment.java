package com.pdm.theway.ui.home;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.pdm.theway.databinding.FragmentHomeBinding;
import com.pdm.theway.ui.home.model.Songs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    TextInputLayout textInputLayout;
    TextView textViewimage;
    ProgressBar progressBar;
    Uri audiouri;
    StorageReference mStorageRef;
    StorageTask mUploadTask;
    DatabaseReference referenceSongs;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        referenceSongs = FirebaseDatabase.getInstance().getReference().child("songs");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("songs");


        binding.btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAudioFile(v);
            }
        });


        binding.btnUpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAudioToFirebase(v);
            }
        });
    }
    public void openAudioFile(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && resultCode == RESULT_OK && data.getData() !=null){
            audiouri = data.getData();
            String fileName = getFileName(audiouri);
            textViewimage.setText(fileName);
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if(uri.getScheme().equals("content")){
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null);

            try {

                if(cursor!=null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {

                cursor.close();
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
            Toast.makeText(getActivity(),"Please Select an Image",Toast.LENGTH_SHORT).show();
        }else{
            if (mUploadTask!=null && mUploadTask.isInProgress()){
                Toast.makeText(getActivity(), "Song upload is already in Progress", Toast.LENGTH_SHORT).show();
            }else{
                uploadFile();
            }
        }
  }

    private void uploadFile() {
        if(audiouri != null) {
            String durationText;
            Toast.makeText(getActivity(), "Please Select an Image", Toast.LENGTH_SHORT).show();

            progressBar.setVisibility(View.VISIBLE);

            StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(audiouri));


            int durationInMillis = findSongDuration(audiouri);

            if (durationInMillis == 0) {
                durationText = "NA";
            }
            durationText = getDurationFromMilli(durationInMillis);

            String finalDurationText = durationText;
            mUploadTask = storageReference.putFile(audiouri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Songs songs = new Songs(textInputLayout.getEditText().getText().toString(),
                                    finalDurationText, audiouri.toString());

                            String uploaId = referenceSongs.push().getKey();
                            referenceSongs.child(uploaId).setValue(songs);

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });

        }else{
            Toast.makeText(getActivity(),"No File Selected to upload", Toast.LENGTH_SHORT).show();

        }
    }

    private String getDurationFromMilli(int durationInMillis) {
        Date date = new Date(durationInMillis);

        SimpleDateFormat simple = new SimpleDateFormat("mm:ss", Locale.getDefault());

        String myTime = simple.format(date);
        return myTime;


    }

    private int findSongDuration(Uri audiouri) {
        int timeMillisec = 0;
        try {
            MediaMetadataRetriever receiver = new MediaMetadataRetriever();
            receiver.setDataSource(getActivity(), audiouri);
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
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(audiouri));
    }
}