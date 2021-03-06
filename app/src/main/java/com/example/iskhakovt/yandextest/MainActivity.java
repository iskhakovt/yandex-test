/*
 * Copyright (c) Timur Iskhakov.
 * Distributed under the terms of the MIT License.
 */


package com.example.iskhakovt.yandextest;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * String to pass ArtistItem to ArtistActivity
     */
    public final static String ARTIST_ITEM = "com.example.iskhakovt.yandextest.MainActivity.ARTIST_ITEM";

    /**
     * Adapter is not set yet, first JSON download try failed
     */
    private boolean firstLoadFailureObserved = false;;

    /**
     * Swipe Refresh list view layout
     */
    SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Adapter holding Artist Items for list view
     */
    private ItemAdapter adapter = null;

    /**
     * Text to filter by name
     */
    private String searchText = "";

    /**
     * Genre to filter
     */
    private String searchGenre = "";

    /**
     * Genres found in downloaded file
     */
    private List<String> genres = new ArrayList<>();


    /**
     * Navigation View drawer
     */
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        // TODO: find out if it is required
        // Postpone the transition until the window's decor view has
        // finished its layout.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();

            final View decor = getWindow().getDecorView();
            decor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean onPreDraw() {
                    decor.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                    return true;
                }
            });
        }
        */

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        // Set swipe refresh color
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        // Load artists on refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadArtists();
            }
        });

        CachingDownload.init(getApplicationContext());

        createToolbar();
    }

    @Override
    public void onStart() {
        super.onStart();
        
        if (adapter == null) {
            // Display refresh icon on start
            setRefreshing(true);

            loadArtists();
        }
    }

    /**
     * Create toolbar search icon menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        // Set hint and hide icon
        final EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setHint(getString(R.string.search) + "...");

        // Add text change events
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

    /**
     * Display only searched items
     */
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

    /**
     * Set toolbar attributes
     */
    private void createToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Register toolbar
        setSupportActionBar(toolbar);

        // Show toolbar back button, which would be replaced with Navigation View button
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
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Register this as Navigation View Listener
        // See main method onNavigationItemSelected below
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Load artists JSON file
     */
    private void loadArtists() {
        new ListInitTask(this).execute(getString(R.string.artist_resource_url));
    }

    /**
     * Succeeded to load artists file
     * @param attributes list of artists
     */
    public void onLoaded(List<ArtistItem> attributes) {
        Collections.shuffle(attributes, new Random(getResources().getInteger(R.integer.random_seed)));

        final ListView listView = (ListView) findViewById(R.id.list_view);

        // Load data to adapter
        adapter = new ItemAdapter(this, attributes);
        listView.setAdapter(adapter);
        displaySearched();
        adapter.notifyDataSetChanged();

        // Set artist items click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                ArtistItem artistItem = (ArtistItem) listView.getItemAtPosition(position);

                Intent intent = new Intent(parent.getContext(), ArtistActivity.class);

                // Selected artist to Artist Activity
                intent.putExtra(ARTIST_ITEM, artistItem);

                // Selected item in listView holder
                final ItemAdapter.ViewHolder holder = (ItemAdapter.ViewHolder) view.getTag();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    final View navigationBar = findViewById(android.R.id.navigationBarBackground);
                    final View statusBar = findViewById(android.R.id.statusBarBackground);
                    final View toolbar = findViewById(R.id.toolbar);

                    // TODO: FIX IT

                    // Views to animate
                    List<Pair<View, String>> pairs = new ArrayList<>();

                    // pairs.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
                    // NullPointerException because of transparent
                    // pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
                    // Transition Name is not set up
                    // pairs.add(Pair.create(toolbar, toolbar.getTransitionName()));

                    pairs.add(Pair.create((View) holder.imageView, holder.imageView.getTransitionName()));

                    // Animation set up
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                            MainActivity.this, pairs.toArray(new Pair[pairs.size()])
                    );

                    // Show Artist Activity
                    startActivity(intent, options.toBundle());
                } else {
                    // Show Artist Activity
                    startActivity(intent);
                }
            }
        });

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final Menu menu = navigationView.getMenu();

        // Get all genres
        Set<String> genreSet = new TreeSet<>();
        for (ArtistItem artist : attributes) {
            for (String genre : artist.getGenres()) {
                genreSet.add(genre);
            }
        }

        // Ensure that "All genres" id is unique
        int start = R.id.nav_all_genre + 1;

        // Put all genres to array and create menu items
        // ID = position in list + NAV_MENU_ITEM_ID_START
        for (String genre : genreSet) {
            int id = this.genres.size() + start;
            this.genres.add(genre);
            menu.add(R.id.nav_genre_group, id, 0, genre);
        }

        // Make all item checkable, make the check exclusive
        menu.setGroupCheckable(R.id.nav_genre_group, true, true);

        // Hide refresh icon
        setRefreshing(false);
    }

    /**
     * Failed to load artists file
     */
    public void onNotLoaded() {
        final View.OnClickListener clickListener = new View.OnClickListener() {
            public void onClick(View v) {
                loadArtists();
            }
        };

        final View coordinatorLayoutView = findViewById(R.id.snackbarPosition);

        // Show try again Snackbar
        Snackbar
                .make(coordinatorLayoutView, R.string.snackbar_text, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_action_text, clickListener)
                .show();

        // Hide refresh icon
        setRefreshing(false);
    }

    public void setRefreshing(final boolean state) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(state);
            }
        });
    }

    /**
     * Toolbar button pressed
     * @param item always search item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Toolbar home button pressed

                if (drawerToggle.isDrawerIndicatorEnabled()) {
                    // Toolbar home button is replaced with Navigation View  button
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

    /**
     * Back button pressed
     */
    @Override
    public void onBackPressed() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            // Was in Navigation View, close it
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Navigation View item (genre) was selected
     * @param item menu (genre) item
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setCheckable(true);

        // Genre items start id
        int start = R.id.nav_all_genre + 1;

        // Save selected only one item
        // This is required, because items are located in two groups
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (item.getGroupId() == R.id.nav_genre_group) {
            navigationView.getMenu().findItem(R.id.nav_all_genre).setChecked(false);
        } else {
            for (int i = 0; i != genres.size(); ++i) {
                navigationView.getMenu().findItem(i + start).setChecked(false);
            }
        }

        // Handle navigation view item clicks here
        int id = item.getItemId();

        if (id == R.id.nav_all_genre) {
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

        // Close Navigation View
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
