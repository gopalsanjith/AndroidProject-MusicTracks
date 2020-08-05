package com.example.homwwork02;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TrackAdapter extends ArrayAdapter<MusicTrack> {

    public TrackAdapter(@NonNull MainActivity context, int resource, @NonNull List<MusicTrack> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        MusicTrack track = getItem(position);
        ItemTextviewsHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_item,parent,false);
            holder = new ItemTextviewsHolder();
            holder.tv_artist = (TextView) convertView.findViewById(R.id.tv_artist);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.tv_track = (TextView) convertView.findViewById(R.id.tv_track);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            convertView.setTag(holder);
        }else{
            holder = (ItemTextviewsHolder) convertView.getTag();
        }
        TextView tv_track = convertView.findViewById(R.id.tv_track);
        TextView tv_price = convertView.findViewById(R.id.tv_price);
        TextView tv_artist = convertView.findViewById(R.id.tv_artist);
        TextView tv_date = convertView.findViewById(R.id.tv_date);

        holder.tv_track.setText(track.getTrack());
        holder.tv_price.setText(track.getTrackPrice()+"");
        holder.tv_artist.setText(track.getArtist());
        holder.tv_date.setText(track.getRealeaseDate()+"");

        return convertView;
    }

    class ItemTextviewsHolder{
        TextView tv_track;
        TextView tv_price;
        TextView tv_artist;
        TextView tv_date;
    }
}
