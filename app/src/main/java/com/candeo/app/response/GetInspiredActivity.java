package com.candeo.app.response;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.NumberPicker;

import com.candeo.app.R;

import java.util.ArrayList;

public class GetInspiredActivity extends ActionBarActivity {

    private NumberPicker numberPicker=null;
    private ArrayList<String> feels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_inspired);
        numberPicker=(NumberPicker)findViewById(R.id.feel_picker);
        feels = new ArrayList<>();
        feels.add("Motivated");
        feels.add("Spirited");
        feels.add("Enlightened");
        feels.add("Happy");
        feels.add("Cheered");
        feels.add("Loved");
        feels.add("Blessed");
        feels.add("Funny");
        feels.add("Strong");
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_AFTER_DESCENDANTS);
        numberPicker.setDisplayedValues(feels.toArray(new String[feels.size()]));
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(feels.size()-1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_inspired, menu);
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
