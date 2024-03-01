package edu.bloomu.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Displays a list of hobbies for the user to select from. User can add or delete
 * hobbies, as well as open a page to view their more significant details.
 *
 * KNOWN BUGS:
 *
 * 1. Add button does not display '+' symbol as intended.
 *
 * @author Jessica Ruehle
 */
public class HobbiesActivity extends AppCompatActivity {

    ListView hobbyListView; // listview element to hold hobbies
    ArrayList<Hobby> hobbies = new ArrayList<>(); // arraylist of hobby objects
    HobbyAdapter adapter; // adapter to handle hobbyListView initialization
    DatabaseReference hobbiesRef; // reference to the hobbies child in the database
    public static final String HOBBYKEY = "HOBBYKEY";
    public static final String HOBBYNAME = "HOBBYNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobbies);

        // get a reference to the database and its hobbies child
        FirebaseApp.initializeApp(this);
        hobbiesRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("hobbies");

        // create a listview object to interact with the xml listview element
        // and create an adapter to later populate it after data retrieval
        hobbyListView = findViewById(R.id.hobby_list);
        adapter = new HobbyAdapter(HobbiesActivity.this, hobbies);
        hobbyListView.setAdapter(adapter);

        // initialize the floating action button
        FloatingActionButton add = findViewById(R.id.fab);

        // attach a ValueEventListener to listen for changes in the "hobbies" node
        hobbiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // create an arraylist of hobbies
                hobbies = new ArrayList<>();

                // Iterate through the "hobbies" node to extract the names
                for (DataSnapshot hobbySnapshot : dataSnapshot.getChildren()) {

                    Hobby newHobby = new Hobby();

                    // for each hobby in the list of children, get it's key and name
                    String hobbyName = hobbySnapshot.child("name").getValue(String.class);
                    String hobbyKey = hobbySnapshot.getKey();

                    // add the hobby's name and key to the hashmap
                    if (hobbyName != null) {
                        newHobby.setName(hobbyName);
                        newHobby.setKey(hobbyKey);
                    }

                    // add hobby to arraylist
                    hobbies.add(newHobby);
                }

                adapter.clear();  // clear the existing data in the adapter
                adapter.addAll(hobbies);  // add the new data
                adapter.notifyDataSetChanged(); // let the adapter know theres new data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        // when a user clicks on the add button, a dialog box is presented to get
        // information from the user
        add.setOnClickListener(e -> {
            AddHobbyDialog dialog = new AddHobbyDialog();
            dialog.show(getSupportFragmentManager(), "add_hobby");
        });

        // create a new intent for when it's time to transition to the next actitivty
        Intent fragmentIntent = new Intent(this, FragmentActivity.class);

        // attach an event handler for whatever hobby the user chooses to open. Passes
        // the key of the hobby to FragmentActivity and launches it
        hobbyListView.setOnItemClickListener((adapterView, view, position, id) -> {

            // get the hobby the user selected and retrieve its key from the map
            Hobby selectedHobby = adapter.getItem(position);
            String hobbyKey = selectedHobby.getKey();
            String hobbyName = selectedHobby.getName();

            // initialize the intent with the hobby's key and start the fragment activity
            fragmentIntent.putExtra(HOBBYKEY, hobbyKey);
            fragmentIntent.putExtra(HOBBYNAME, hobbyName);
            startActivity(fragmentIntent);
        });

        // event handler for when a user long presses a hobby in the list view
        hobbyListView.setOnItemLongClickListener((adapterView, view, position, id) -> {

            // get the task selected and call delete task to remove it
            Hobby selectedHobby = adapter.getItem(position);
            deleteHobby(selectedHobby);
            return true;
        });
    }

    /**
     * Prompts user to confirm hobby delete and performs deletion.
     */
    public void deleteHobby(Hobby hobby) {
        // create an alert
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // show a message asking the user to confirm deletion
        builder.setTitle(R.string.delete_hobby)

                // set the positive button listener
                .setPositiveButton(R.string.confirm, (dialog, id1) -> {

                    // get the selected goal's key
                    String hobbyKey = hobby.getKey();

                    // remove hobby
                    hobbiesRef.child(hobbyKey).removeValue();

                    // update adapter
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                })

                // if they select cancel, dismiss the dialog
                .setNegativeButton(R.string.cancel, (dialog, id2) -> dialog.dismiss());

        // create the dialog and show it
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
