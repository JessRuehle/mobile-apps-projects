package edu.bloomu.finalproject;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;

/**
 * Custom adapter to handle initializing the ListView of Tasks.
 *
 * @author Jessica Ruehle
 */
public class TaskAdapter extends ArrayAdapter<Task> {

    /**
     * Constructor that initializes its inherited constructor given a list of Task
     * objects,
     */
    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, 0, tasks);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Task task = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.listview_adapter_layout, parent, false);
        }

        // Lookup view for data population
        TextView taskName = convertView.findViewById(R.id.listitem_textview);

        // Populate the data into the template view using the data object
        taskName.setText(task.getDescription());



        // Set background color based on task status
        if (task.getCompleted()) {
            int completedColor = ContextCompat.getColor(getContext(), R.color.light_green);
            convertView.setBackgroundColor(completedColor);
        } else {
            // Reset background color for in-progress tasks
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}