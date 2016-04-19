/*
 * Copyright (c) Timur Iskhakov.
 * Distributed under the terms of the MIT License.
 */


package com.example.iskhakovt.yandextest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    public final static String ARTIST_ITEM = "com.example.iskhakovt.yandextest.MainActivity.ARTIST_ITEM";

    private boolean connectionFailureObserved = false;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = (ListView) findViewById(R.id.listView);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadArtists();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Set hint and hide icon
        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setHint(getString(R.string.search));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

    private void loadArtists() {
        new ListInitTask(this).execute(getString(R.string.artist_resource_url));
    }

    public void loaded(List<ArtistItem> attributes) {
        // Not required now, useful if updates would be implemented
        connectionFailureObserved = false;

        final ListView listView = (ListView) findViewById(R.id.listView);

        adapter = new ItemAdapter(this, attributes);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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
