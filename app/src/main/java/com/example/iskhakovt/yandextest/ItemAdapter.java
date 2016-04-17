package com.example.iskhakovt.yandextest;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ItemAdapter extends BaseAdapter {
    private ArrayList listData;
    private LayoutInflater layoutInflater;

    public ItemAdapter(Context context, ArrayList listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.activity_item, parent, false);

            holder = new ViewHolder();
            holder.artistNameView = (TextView) convertView.findViewById(R.id.artistName);
            holder.artistGenreView = (TextView) convertView.findViewById(R.id.artistGenre);
            holder.artistDescriptionView = (TextView) convertView.findViewById(R.id.artistDescription);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ArtistItem artistItem = (ArtistItem) listData.get(position);
        holder.artistNameView.setText(artistItem.getName());
        holder.artistGenreView.setText(artistItem.getGenre());
        holder.artistDescriptionView.setText("");

        if (holder.imageView != null) {
            new ImageDownloadTask(holder.imageView).execute(artistItem.getSmallImageUrl());
        }

        return convertView;
    }

    static class ViewHolder {
        TextView artistNameView;
        TextView artistGenreView;
        TextView artistDescriptionView;
        ImageView imageView;
    }
}