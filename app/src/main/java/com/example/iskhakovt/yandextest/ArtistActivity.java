/*
 * Copyright (c) Timur Iskhakov.
 * Distributed under the terms of the MIT License.
 */


package com.example.iskhakovt.yandextest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


public class ArtistActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add toolbar's Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        ArtistItem artistItem = (ArtistItem)intent.getSerializableExtra(MainActivity.ARTIST_ITEM);

        ImageView imageView = (ImageView) findViewById(R.id.image);
        TextView artistGenreView = (TextView) findViewById(R.id.artistGenre);
        TextView artistShortDescriptionView = (TextView) findViewById(R.id.artistShortDescription);
        TextView artistDescriptionView = (TextView) findViewById(R.id.artistDescription);

        // Toolbar's title
        getSupportActionBar().setTitle(artistItem.getName());

        artistGenreView.setText(artistItem.getGenre());

        int albumsNum = artistItem.getAlbums();
        int tracksNum = artistItem.getTracks();
        String albumsStr = this.getResources().getQuantityString(R.plurals.album, albumsNum);
        String tracksStr = this.getResources().getQuantityString(R.plurals.tracks, tracksNum);
        artistShortDescriptionView.setText(
                String.format(getString(R.string.short_description),
                        albumsNum, albumsStr, tracksNum, tracksStr)
        );

        artistDescriptionView.setText(artistItem.getDescription());

        ImageDownloadTask downloadTask = new ImageDownloadTask(imageView);
        downloadTask.execute(artistItem.getBigCoverUrl());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the toolbars's Up button
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
