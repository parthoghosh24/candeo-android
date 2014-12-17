package com.candeo.app.response;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.NumberPicker;

import com.candeo.app.R;

import java.util.ArrayList;

public class AppreciateActivity extends ActionBarActivity {

    private NumberPicker ratingPicker=null;
    private ArrayList<String> ratings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appreciate);
        ratingPicker=(NumberPicker)findViewById(R.id.rating_picker);
        ratings = new ArrayList<>();
        ratings.add("Ok");
        ratings.add("Good");
        ratings.add("Superb");
        ratings.add("Hot");
        ratings.add("Magic");
        ratingPicker.setDescendantFocusability(NumberPicker.FOCUS_AFTER_DESCENDANTS);
        ratingPicker.setDisplayedValues(ratings.toArray(new String[ratings.size()]));
        ratingPicker.setMinValue(0);
        ratingPicker.setMaxValue(ratings.size()-1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_appreciate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
