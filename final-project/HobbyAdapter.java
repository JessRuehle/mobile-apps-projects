package edu.bloomu.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Custom adapter to populate a ListView with Hobby objects.
 *
 * @author Jessica Ruehle
 */
public class HobbyAdapter extends ArrayAdapter<Hobby> {

    /**
     * Constructor that initializes its inherited constructor given a list of Hobby
     * objects
     */
    public HobbyAdapter(Context context, List<Hobby> hobbies) {
        super(context, 0, hobbies);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Hobby hobby = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.listview_adapter_layout, parent, false);
        }

        // Lookup view for data population
        TextView hobbyName = convertView.findViewById(R.id.listitem_textview);

        // Populate the data into the template view using the data object
        hobbyName.setText(hobby.getName());

        // Return the completed view to render on screen
        return convertView;
    }

}