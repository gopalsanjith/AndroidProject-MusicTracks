package com.example.homwwork02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    EditText et_keyWord;
    TextView tv_limit;
    Button bt_search,bt_reset;
    SeekBar sb_limit;
    String keyword,url;
    String limit = "10";
    ListView list;
    Switch sw_sort;
    ProgressBar pb_progress;
    ArrayList<MusicTrack> songs = new ArrayList<>();
    ArrayList<MusicTrack> musicTracks = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("i Tunes Music Search");
        et_keyWord= findViewById(R.id.et_keyWord);
        bt_search= findViewById(R.id.bt_search);
        sb_limit= findViewById(R.id.sb_limit);
        tv_limit= findViewById(R.id.tv_limit);
        sw_sort = findViewById(R.id.switch_sort);
        pb_progress = findViewById(R.id.pb_progress);
        pb_progress.setVisibility(ProgressBar.INVISIBLE);
        if(isConnected()){
            tv_limit.setText(limit);
            sb_limit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    limit =(i+10)+"";
                    tv_limit.setText(limit);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            bt_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    musicTracks.clear();
                    keyword = (et_keyWord.getText().toString().trim()).replace(" ","+");
                    if(keyword!=null && !keyword.isEmpty() && keyword!=""){
                        try {
                            url ="https://itunes.apple.com/search?term="+ keyword +"&limit="+URLEncoder.encode(limit,"UTF-8");
                            new getTracks().execute(url);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Please enter some keyword", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    class getTracks extends AsyncTask<String,Void,ArrayList<MusicTrack>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb_progress.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected ArrayList<MusicTrack> doInBackground(String... strings) {
            HttpURLConnection connection = null;
            ArrayList<MusicTrack> result = new ArrayList<>();
            try {
                URL urlcon = new URL(strings[0]);
                connection = (HttpURLConnection) urlcon.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);
                    JSONArray musicTracks = root.getJSONArray("results");
                    for (int i=0;i<musicTracks.length();i++) {
                        JSONObject musicTrack = musicTracks.getJSONObject(i);
                        MusicTrack song = new MusicTrack();
                        song.setArtist(musicTrack.getString("artistName"));
                        song.setAlbum(musicTrack.getString("collectionName"));
                        song.setAlbumPrice(musicTrack.getDouble("collectionPrice"));
                        song.setGenre(musicTrack.getString("primaryGenreName"));
                        song.setTrack(musicTrack.getString("trackName"));
                        song.setTrackPrice(musicTrack.getDouble("trackPrice"));
                        song.setImageurl(musicTrack.getString("artworkUrl100"));
                        String date_string = musicTrack.getString("releaseDate").substring(0,10);

                        Date last_date_date = new SimpleDateFormat("yyyy-MM-dd").parse(date_string);
                       song.setRealeaseDate(new SimpleDateFormat("MM-dd-yyyy").format(last_date_date));
                        result.add(song);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<MusicTrack> mts) {
            pb_progress.setVisibility(ProgressBar.INVISIBLE);
//            final TrackAdapter adapter;
            if(mts.size() != 0){
                for(MusicTrack m : mts){
                    musicTracks.add(m);
                }
                sort(musicTracks,false);
                sw_sort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        sort(musicTracks,isChecked);
                    }
                });
                list = (ListView) findViewById(R.id.lv_list);
                final TrackAdapter adapter = new TrackAdapter(MainActivity.this ,R.layout.track_item, musicTracks);
                list.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(MainActivity.this,Display_Details.class);
                        intent.putExtra("details",musicTracks.get(i));
                        startActivity(intent);
                    }
                });

                bt_reset = findViewById(R.id.bt_reset);
                bt_reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        et_keyWord.setText("");
                        sb_limit.setProgress(0);
                        musicTracks.clear();
                        sw_sort.setChecked(false);
                        adapter.notifyDataSetChanged();
                    }
                });


            }else{
                list = (ListView) findViewById(R.id.lv_list);
                final TrackAdapter adapter = null;
                list.setAdapter(adapter);
                Toast.makeText(MainActivity.this, "No results found", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void sort(ArrayList<MusicTrack> tracks,boolean isChecked){
        if (!isChecked) {
            Collections.sort(tracks, new Comparator<MusicTrack>() {
                @Override
                public int compare(MusicTrack t1, MusicTrack t2) {
                    return Double.valueOf(t1.getTrackPrice()).compareTo(t2.getTrackPrice());
                }
            });
        } else {
            Collections.sort(musicTracks, new Comparator<MusicTrack>() {
                @Override
                public int compare(MusicTrack o1, MusicTrack o2) {

                    Date d1 = new Date();
                    Date d2 = new Date();

                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                    try {
                         d1 = sdf.parse(o1.getRealeaseDate());
                         d2 = sdf.parse(o2.getRealeaseDate());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    return d1.compareTo(d2);
                }

            });
        }
    }
}
