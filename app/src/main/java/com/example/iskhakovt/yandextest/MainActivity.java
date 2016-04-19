/*
 * Copyright (c) Timur Iskhakov.
 * Distributed under the terms of the MIT License.
 */


package com.example.iskhakovt.yandextest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public final static String ARTIST_ITEM = "com.example.iskhakovt.yandextest.MainActivity.ARTIST_ITEM";
    private boolean connectionFailureObserved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadArtists();
    }

    private void loadArtists() {
        new ListInitTask(this).execute(getString(R.string.artist_resource_url));
    }

    public void loaded(ArrayList<ArtistItem> attributes) {
        // Not required now, useful if updates would be implemented
        connectionFailureObserved = false;

        final ListView listView = (ListView) findViewById(R.id.listView);

        ItemAdapter adaptor = new ItemAdapter(this, attributes);
        listView.setAdapter(adaptor);
        adaptor.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                ArtistItem item = (ArtistItem) listView.getItemAtPosition(position);

                Intent intent = new Intent(parent.getContext(), ArtistActivity.class);
                intent.putExtra(ARTIST_ITEM, item);
                startActivity(intent);
            }
        });
    }

    public void notLoaded() {
        if (!connectionFailureObserved) {
            connectionFailureObserved = true;

            String text = getString(R.string.connection_failure);
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.show();
        }

        loadArtists();
    }
}
