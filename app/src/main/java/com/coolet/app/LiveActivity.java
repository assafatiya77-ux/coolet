package com.coolet.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.content.Intent;
import android.net.Uri;

public class LiveActivity extends Activity {
    private LinearLayout results;
    private Spinner city, genre;
    private final String[][] venues = {
        {"Berlin", "Punk", "SO36", "https://www.so36.com/"},
        {"Berlin", "Rock", "Lido", "https://www.lido-berlin.de/"},
        {"Tel Aviv", "Rock", "Barby", "https://www.barby.co.il/"},
        {"Tel Aviv", "Jazz", "Shablul Jazz", "https://www.shabluljazz.com/"},
        {"New York", "Jazz", "Village Vanguard", "https://villagevanguard.com/"},
        {"New York", "Jazz", "Blue Note", "https://www.bluenotejazz.com/nyc/"}
    };
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24,24,24,24);
        root.setBackgroundColor(Color.WHITE);
        TextView heading = new TextView(this);
        heading.setText("Coolet Live");
        heading.setTextSize(28);
        heading.setTypeface(null,Typeface.BOLD);
        heading.setTextColor(Color.BLACK);
        root.addView(heading);
        TextView note = new TextView(this);
        note.setText("Discover live music • Browse venues and their official schedules. Event dates are not yet synchronized.");
        note.setTextColor(Color.DKGRAY);
        root.addView(note);
        city = new Spinner(this);
        city.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,
            new String[]{"All cities","Berlin","Tel Aviv","New York"}));
        root.addView(city);
        genre = new Spinner(this);
        genre.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,
            new String[]{"All genres","Punk","Rock","Jazz"}));
        root.addView(genre);
        ScrollView scroll = new ScrollView(this);
        results = new LinearLayout(this);
        results.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(results);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) { refresh(); }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        };
        city.setOnItemSelectedListener(listener);
        genre.setOnItemSelectedListener(listener);
        refresh();
    }
    private void refresh() {
        if (results == null || city.getSelectedItem() == null || genre.getSelectedItem() == null) return;
        results.removeAllViews();
        String selectedCity = city.getSelectedItem().toString();
        String selectedGenre = genre.getSelectedItem().toString();
        int count=0;
        for (String[] venue:venues) {
            if (!selectedCity.equals("All cities") && !selectedCity.equals(venue[0])) continue;
            if (!selectedGenre.equals("All genres") && !selectedGenre.equals(venue[1])) continue;
            count++;
            TextView title = new TextView(this);
            title.setText(venue[2] + "  •  " + venue[0] + "  •  " + venue[1]);
            title.setTextColor(Color.BLACK);
            title.setTextSize(18);
            title.setPadding(0,24,0,8);
            results.addView(title);
            Button open = new Button(this);
            open.setText("Official concert schedule");
            String url = venue[3];
            open.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))));
            results.addView(open);
        }
        if (count==0) {
            TextView empty=new TextView(this);
            empty.setText("No venues in this filter yet.");
            results.addView(empty);
        }
    }
}
