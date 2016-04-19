/*
 * Copyright (c) Timur Iskhakov.
 * Distributed under the terms of the MIT License.
 */


package com.example.iskhakovt.yandextest;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public final static String ARTIST_ITEM = "com.example.iskhakovt.yandextest.MainActivity.ARTIST_ITEM";

    private boolean connectionFailureObserved = false;
    private ItemAdapter adapter = null;
    private String searchText = "", searchGenre = "";
    private ActionBarDrawerToggle drawerToggle;
    private List<String> genres = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createToolbar();
        loadArtists();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        // Set hint and hide icon
        final EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setHint(getString(R.string.search));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                displaySearched();
                return true;
            }
        });

        return true;
    }

    private void displaySearched() {
        if (adapter != null) {
            try {
                JSONObject json = new JSONObject();
                json.put("name", searchText);
                json.put("genre", searchGenre);

                adapter.getFilter().filter(json.toString());
            } catch (JSONException e) {
                // no exception should appear
                throw new RuntimeException(e);
            }
        }
    }

    private void createToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }
        };

        // Toolbar home button now opens the navigation bar
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void loadArtists() {
        new ListInitTask(this).execute(getString(R.string.artist_resource_url));
    }

    public void loaded(List<ArtistItem> attributes) {
        // Not required now, useful if updates would be implemented
        connectionFailureObserved = false;

        final ListView listView = (ListView) findViewById(R.id.list_view);

        adapter = new ItemAdapter(this, attributes);
        listView.setAdapter(adapter);
        displaySearched();
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

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final Menu menu = navigationView.getMenu();

        // Get all genres
        Set<String> genreSet = new HashSet<>();
        for (ArtistItem artist : attributes) {
            for (String genre : artist.getGenres()) {
                genreSet.add(genre);
            }
        }

        // Ensure that "All genres" id is unique
        int start = R.id.nav_all_genres + 1;

        // Put all genres to array and create menu items
        // ID = position in list + NAV_MENU_ITEM_ID_START
        for (String genre : genreSet) {
            int id = this.genres.size() + start;
            this.genres.add(genre);
            menu.add(R.id.nav_genre, id, 0, genre);
        }

        // Make all item checkable, make the check exclusive
        menu.setGroupCheckable(R.id.nav_genre, true, true);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Toolbar home button pressed
                if (drawerToggle.isDrawerIndicatorEnabled()) {
                    return drawerToggle.onOptionsItemSelected(item);
                } else {
                    onBackPressed();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setCheckable(true);

        // Handle navigation view item clicks here
        int id = item.getItemId();
        int start = R.id.nav_all_genres + 1;

        if (id == R.id.nav_all_genres) {
            if (!searchGenre.equals("")) {
                searchGenre = "";
                displaySearched();
            }
        } else if (id >= start && id < start + genres.size()) {
            String newGenreSearch = genres.get(id - start);
            if (!searchGenre.equals(newGenreSearch)) {
                searchGenre = newGenreSearch;
                displaySearched();
            }
        }

        item.setCheckable(true);

        Log.w("MainActivity", Boolean.toString(item.isCheckable()));
        if(item.isCheckable()) {
            item.setChecked(!item.isChecked());
        }

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
