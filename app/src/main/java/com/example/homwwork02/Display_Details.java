package com.example.homwwork02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Display_Details extends AppCompatActivity {
    Button btn_finish;
    TextView tv_track,tv_genre,tv_artist,tv_album,tv_trackprice,tv_albumprice;
    ImageView iv_mainImager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display__details);
        setTitle("i Tunes Music Search");

        tv_track=findViewById(R.id.tv_trackTitle);
        iv_mainImager=findViewById(R.id.iv_mainImage);
        tv_genre=findViewById(R.id.tv_genre);
        tv_artist=findViewById(R.id.tv_artist);
        tv_album=findViewById(R.id.tv_album);
        tv_trackprice=findViewById(R.id.tv_trackPrice);
        tv_albumprice=findViewById(R.id.tv_albumPrice);
        String imageurl=null;
        MusicTrack music=new MusicTrack();
        music= (MusicTrack) getIntent().getExtras().getSerializable("details");
        tv_track.setText("Track:"+music.getTrack());
        tv_genre.setText("Genre:"+music.getGenre());
        tv_artist.setText("Artist:"+music.getArtist());
        tv_album.setText("Album:"+music.getAlbum());
        tv_trackprice.setText("Track Price:"+music.getTrackPrice()+"$");
        tv_albumprice.setText("Album Price:"+music.getAlbumPrice()+"$");
        Picasso.get().load(music.getImageurl()).into(iv_mainImager);
        btn_finish=findViewById(R.id.btn_finish);

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
