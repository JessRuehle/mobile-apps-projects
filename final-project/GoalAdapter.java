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
 * Custome adapter to populate a ListView with Goal objects.
 *
 * @author Jessica Ruehle
 */
public class GoalAdapter extends ArrayAdapter<Goal> {

    public GoalAdapter(Context context, List<Goal> goals) {
        super(context, 0, goals);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Goal goal = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.listview_adapter_layout, parent, false);
        }

        // Lookup view for data population
        TextView goalName = convertView.findViewById(R.id.listitem_textview);

        // Populate the data into the template view using the data object
        goalName.setText(goal.getDescription());

        // Set background color based on task status
        if (goal.getCompleted()) {
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
