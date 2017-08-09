package com.truevine.christiansongs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SongActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        CharSequence[] songs = getResources().getTextArray(R.array.songs);
        String[] song_names = getResources().getStringArray(R.array.song_names);

        Intent intent = getIntent();
        int songNum = intent.getExtras().getInt("song_index",0);
        String title = songNum + ". " + song_names[songNum-1];
        setTitle(title);
        CharSequence song = songs[songNum-1];
        TextView textView = (TextView) findViewById(R.id.tv_song);
        textView.setText(song);
    }

    @Override
    public void onResume() {
        super.onResume();
    }



}
