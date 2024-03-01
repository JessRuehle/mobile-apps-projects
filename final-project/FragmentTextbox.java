package edu.bloomu.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Creates a new hobby given user input.
 *
 * @author Jessica Ruehle
 */
public class FragmentTextbox extends Fragment {

    View view; // view object

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // initialize the view
        view = inflater.inflate(R.layout.textbox_fragment, container, false);

        // initialize xml elements field
        EditText input = view.findViewById(R.id.input);

        // initialize new hobby key
        String hobbyKey = requireArguments().getString("KEY");

        // reference database
        FirebaseApp.initializeApp(getContext());
        DatabaseReference hobbyRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("hobbies")
                .child(hobbyKey);

        return view;

    }

}
