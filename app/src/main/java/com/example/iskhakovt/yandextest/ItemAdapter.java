/*
 * Copyright (c) Timur Iskhakov.
 * Distributed under the terms of the MIT License.
 */


package com.example.iskhakovt.yandextest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;


public class ItemAdapter extends ArrayAdapter<ArtistItem> {
    private final List<ArtistItem> items;
    private List<ArtistItem> filteredItems;
    private final ArtistItemFilter filter = new ArtistItemFilter();

    /**
     * Executing image download tasks
     */
    HashMap<ImageView, ImageDownloadTask> imageDownloadTasks;

    public ItemAdapter(Context context, List<ArtistItem> items) {
        super(context, 0, items);

        this.items = items;
        this.filteredItems = items;
        this.imageDownloadTasks = new HashMap<>();
    }

    @Override
    public int getCount() {
        return filteredItems.size();
    }

    @Override
    public ArtistItem getItem(int position) {
        return filteredItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_item, parent, false);

            holder = new ViewHolder();
            holder.artistNameView = (TextView) convertView.findViewById(R.id.artistName);
            holder.artistGenreView = (TextView) convertView.findViewById(R.id.artistGenre);
            holder.artistDescriptionView = (TextView) convertView.findViewById(R.id.artistShortDescription);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ArtistItem artistItem = getItem(position);

        holder.artistNameView.setText(artistItem.getName());
        holder.artistGenreView.setText(artistItem.getGenre());

        int albumsNum = artistItem.getAlbums();
        int tracksNum = artistItem.getTracks();
        String albumsStr = ResourcesPluralUtil.getQuantityStringZero(
                convertView.getResources(), R.plurals.albums, R.string.albums_zero, albumsNum
        );
        String tracksStr = ResourcesPluralUtil.getQuantityStringZero(
                convertView.getResources(), R.plurals.tracks, R.string.tracks_zero, tracksNum
        );

        holder.artistDescriptionView.setText(
                String.format(convertView.getResources().getString(R.string.short_description_item),
                        albumsStr, tracksStr)
        );

        // Display placeholder while downloading
        Drawable placeholder = convertView.getResources().getDrawable(R.drawable.placeholder);
        holder.imageView.setImageDrawable(placeholder);

        // Cancel previous task
        ImageDownloadTask previousDownloadTask = this.imageDownloadTasks.get(holder.imageView);
        if (previousDownloadTask != null) {
            previousDownloadTask.cancel(false);
        }

        // Start downloading a new image
        ImageDownloadTask downloadTask = new ImageDownloadTask(holder.imageView);
        downloadTask.execute(artistItem.getSmallCoverUrl());
        this.imageDownloadTasks.put(holder.imageView, downloadTask);

        // Start the ellipsize animation
        holder.artistNameView.setSelected(true);
        holder.artistGenreView.setSelected(true);
        holder.artistDescriptionView.setSelected(true);

        return convertView;
    }

    static class ViewHolder {
        TextView artistNameView;
        TextView artistGenreView;
        TextView artistDescriptionView;
        ImageView imageView;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public class ArtistItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                CharSequence normConstraint = normalize(constraint.toString());
                List<ArtistItem> tempList = new ArrayList<>();

                for (ArtistItem artist : items) {
                    if (normalize(artist.getName()).contains(normConstraint)) {
                        tempList.add(artist);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = items.size();
                filterResults.values = items;
            }

            return filterResults;
        }

        /**
         * Normalize text
         * @param str text
         * @return text to lower case and removed whitespaces
         */
        private String normalize(String str) {
            return str.replaceAll("\\s","").toLowerCase();
        }

        /**
         * Notify about filtered list to ui
         * @param constraint text
         * @param results filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredItems = (List<ArtistItem>) results.values;
            notifyDataSetChanged();
        }
    }
}
