package edu.bloomu.finalproject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.fragment.app.DialogFragment;

/**
 * Displays a DialogFragment containing a toggle button that allows the user to check
 * and set the completed state of their task.
 *
 * @author Jessica Ruehle
 */
public class TaskDetailsDialog extends DialogFragment {

    AppCompatToggleButton toggleStatus; // toggle button
    boolean startingState = false; // beginning completed state

    // listeners for when the user clicks the negative or positive buttons
    private DialogInterface.OnClickListener positiveClickListener;
    private DialogInterface.OnClickListener negativeClickListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // create a builder for an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // inflate the layout and set the view
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.task_details_dialog, null);

        // initialize the toggle button and set its beginning state
        toggleStatus = view.findViewById(R.id.completed_toggle);
        toggleStatus.setChecked(startingState);

        // set title
        builder.setTitle(R.string.task_details);

        // set the view and message in the dialog
        builder.setView(view)

                // the listeners will be called when the user clicks the "Add" or
                // "Cancel" buttons
                .setPositiveButton(R.string.confirm, positiveClickListener)
                .setNegativeButton(R.string.cancel, negativeClickListener);

        // create the dialog
        return builder.create();
    }

    /**
     * Initializes the onPositiveClick listener. When called from parent fragment or
     * activity, handles the click events for the positive click button.
     */
    public void setTaskPositiveClickListener(DialogInterface.OnClickListener listener) {
        this.positiveClickListener = listener;
    }

    /**
     * Initializes the onNegativeClick listener. When called from parent fragment or
     * activity, handles the click events for the negative click button.
     */
    public void setTaskNegativeClickListener(DialogInterface.OnClickListener listener) {
        this.negativeClickListener = listener;
    }

    /**
     * Returns the state of the toggle button.
     */
    public boolean getCompletedStatus() {return toggleStatus.isChecked();}

    /**
     * Sets the state of the toggle button.
     */
    public void setCompletedStatus(boolean state) {startingState = state;}


//    public String editDescription() {
//        // use TextView method autofill to put current description there
//    }

}
