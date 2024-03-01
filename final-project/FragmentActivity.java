package edu.bloomu.finalproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Hosts the activity_fragment xml layout and creates a fragment containing data passed
 * from HobbiesActivity.
 *
 * @author Jessica Ruehle
 */
public class FragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        // retrieve intent from HobbyActivity with Hobby key
        Bundle hobbyBundle = getIntent().getExtras();
        String hobbyKey = hobbyBundle.getString(HobbiesActivity.HOBBYKEY);
        String hobbyName = hobbyBundle.getString(HobbiesActivity.HOBBYNAME);

        // create a bundle to pass the hobby key along
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putString("HOBBY", hobbyKey);

        /*
        // create the toolbar and give it a title
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(hobbyName);
        setSupportActionBar(toolbar);
         */

        // call the fragment manager to inflate a new fragment into the fragment
        // container view
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, HobbyFragment.class, fragmentBundle)
                .commit();
    }
}
