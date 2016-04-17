package com.example.iskhakovt.yandextest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listview = (ListView) findViewById(R.id.listView);

        ArrayList<ArtistItem> attributes = new ArrayList<>();

        attributes.add(new ArtistItem(
                "The Beatles",
                "Rock & Roll",
                "Cool",
                "https://upload.wikimedia.org/wikipedia/commons/d/df/The_Fabs.JPG",
                ""
        ));

        listview.setAdapter(new ItemAdapter(this, attributes));

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

            }
        });
    }
}
