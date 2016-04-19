/*
 * Copyright (c) Timur Iskhakov.
 * Distributed under the terms of the MIT License.
 */


package com.example.iskhakovt.yandextest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Artist description activity
 */
public class ArtistActivity extends AppCompatActivity {
    /**
     * Displaying artist entry
     */
    ArtistItem artistItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Register toolbar
        setSupportActionBar(toolbar);

        // Add toolbar's back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get atrist entry from parent
        final Intent intent = getIntent();
        artistItem = (ArtistItem)intent.getSerializableExtra(MainActivity.ARTIST_ITEM);

        final ImageView imageView = (ImageView) findViewById(R.id.artist_image);
        TextView artistGenreView = (TextView) findViewById(R.id.artist_genre);
        TextView artistShortDescriptionView = (TextView) findViewById(R.id.artist_short_description);
        TextView artistDescriptionView = (TextView) findViewById(R.id.artist_description);

        // Toolbar's title
        getSupportActionBar().setTitle(artistItem.getName());

        artistGenreView.setText(artistItem.getGenreString(", "));

        int albumsNum = artistItem.getAlbums();
        int tracksNum = artistItem.getTracks();
        String albumsStr = ResourcesPluralUtil.getQuantityStringZero(
                this.getResources(), R.plurals.albums, R.string.albums_zero, albumsNum
        );
        String tracksStr = ResourcesPluralUtil.getQuantityStringZero(
                this.getResources(), R.plurals.tracks, R.string.tracks_zero, tracksNum
        );

        artistShortDescriptionView.setText(
                String.format(getString(R.string.short_description_artist), albumsStr, tracksStr)
        );

        artistDescriptionView.setText(artistItem.getDescription());

        final ImageDownloadTask downloadTask = new ImageDownloadTask(imageView);
        downloadTask.execute(artistItem.getBigCoverUrl());
    }

    /**
     * Toolbar item click
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the toolbars's Up button
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);

                // Clear the activity top
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Back to parent activity
                NavUtils.navigateUpTo(this, intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.artist_menu, menu);
        return true;
    }

    /**
     * Toolbar website click
     */
    public void onWebsite(MenuItem menuItem) {
        String url = artistItem.getLink();
        final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        if (browserIntent.resolveActivity(getPackageManager()) == null) {
            // No browser found

            String text = getString(R.string.no_browser);
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
        }

        // Show website in browser
        startActivity(browserIntent);
    }
}
