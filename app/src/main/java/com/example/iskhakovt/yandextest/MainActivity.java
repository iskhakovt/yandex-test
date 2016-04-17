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
    //final String ATTRIBUTE_IMAGE = "";
    final String ATTRIBUTE_NAME = "name";
    final String ATTRIBUTE_NAME_GENRE = "genre";
    final String ATTRIBUTE_NAME_DESCRIPTION = "description";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listview = (ListView) findViewById(R.id.listView);

        ArrayList<Map<String, String>> attributes = new ArrayList<>();
        HashMap<String, String> attribute_values = new HashMap<>();

        attribute_values.put(ATTRIBUTE_NAME, "The Beatles");
        attribute_values.put(ATTRIBUTE_NAME_GENRE, "Rock & Roll");
        attribute_values.put(ATTRIBUTE_NAME_DESCRIPTION, "Cool");

        attributes.add(attribute_values);

        String[] from = {ATTRIBUTE_NAME, ATTRIBUTE_NAME_GENRE, ATTRIBUTE_NAME_DESCRIPTION};
        int[] to = {R.id.artistName, R.id.artistGenre, R.id.artistDescription};

        SimpleAdapter sa = new SimpleAdapter(this, attributes, R.layout.activity_item, from, to);
        listview.setAdapter(sa);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

            }
        });
    }
}
