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

import org.json.JSONException;
import org.json.JSONObject;


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
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_item, parent, false);

            holder = new ViewHolder();
            holder.artistNameView = (TextView) convertView.findViewById(R.id.artist_name);
            holder.artistGenreView = (TextView) convertView.findViewById(R.id.artist_genre);
            holder.artistDescriptionView = (TextView) convertView.findViewById(R.id.artist_short_description);
            holder.imageView = (ImageView) convertView.findViewById(R.id.artist_image);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ArtistItem artistItem = getItem(position);

        holder.artistNameView.setText(artistItem.getName());
        holder.artistGenreView.setText(artistItem.getGenreString());

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
        final Drawable placeholder = convertView.getResources().getDrawable(R.drawable.placeholder);
        holder.imageView.setImageDrawable(placeholder);

        // Cancel previous task
        final ImageDownloadTask previousDownloadTask = this.imageDownloadTasks.get(holder.imageView);
        if (previousDownloadTask != null) {
            previousDownloadTask.cancel(false);
        }

        // Start downloading a new image
        final ImageDownloadTask downloadTask = new ImageDownloadTask(holder.imageView);
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
        /**
         * Filter atrist items
         * @param constraint JSON string with fields name and genre
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            try {
                JSONObject json = new JSONObject(constraint.toString());

                String name = normalize(json.getString("name"));
                String genre = json.getString("genre");

                List<ArtistItem> tempList = new ArrayList<>();

                for (ArtistItem artist : items) {
                    if (fitName(name, artist) && fitGenre(genre, artist)) {
                        tempList.add(artist);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } catch (JSONException e) {
                filterResults.count = items.size();
                filterResults.values = items;
            }

            return filterResults;
        }


        /**
         * Checks whether the artist name was searching for
         * @param constraint name search string
         * @param candidate artist
         */
        private boolean fitName(String constraint, ArtistItem candidate) {
            return constraint.length() == 0 || normalize(candidate.getName()).contains(constraint);
        }

        /**
         * Checks whether the artist genre was searching for
         * @param constraint genre name
         * @param candidate artist
         */
        private boolean fitGenre(String constraint, ArtistItem candidate) {
            return constraint.length() == 0 || candidate.hasGenre(constraint);
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
