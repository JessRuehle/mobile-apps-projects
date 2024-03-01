package edu.bloomu.finalproject;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Retrieves the name of a hobby from the user and adds it to the database.
 *
 * @author Jessica Ruehle
 */
public class AddHobbyDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // use builder tp create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // inflate view
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.textbox_fragment, null);

        // set dialog title
        builder.setTitle(R.string.provide_name);

        // get reference to app
        FirebaseApp.initializeApp(getContext());
        DatabaseReference hobbyRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("hobbies");

        // initialize edittext element
        EditText input = view.findViewById(R.id.input);

        // builder event handling
        builder.setView(view)
                .setPositiveButton(R.string.add, (dialog, id) -> {

                    // get hobby info and send it to database
                    String name = input.getText().toString();
                    String newKey = hobbyRef.push().getKey();
                    hobbyRef.child(newKey).child("name").setValue(name);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    dismiss();
                });

        // create dialog
        return builder.create();
    }
}
