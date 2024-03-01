package edu.bloomu.finalproject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Displays an AlertDialog fragment prompting the user to input a description. Methods
 * setPositiveClickListener and setNegativeClickListener allow user to handle positive
 * and negative click events from the host activity or fragment.
 */
public class AddDialogFragment extends DialogFragment {

    // xml edittext element
    EditText input;
    String title;

    // listeners for when the user clicks the negative or positive buttons
    private DialogInterface.OnClickListener positiveClickListener;
    private DialogInterface.OnClickListener negativeClickListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // create a builder for an AlertDialog fragment
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // inflate the layout and initialize the view, title, and edittext element
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_dialog_fragment, null);
        input = view.findViewById(R.id.input);
        builder.setTitle(title);

        // set the view and message in the dialog
        builder.setView(view)
                .setMessage(R.string.description_msg)

                // the listeners will be called when the user clicks the "Add" or
                // "Cancel" buttons
                .setPositiveButton(R.string.add, positiveClickListener)
                .setNegativeButton(R.string.cancel, negativeClickListener);

        // create the dialog
        return builder.create();
    }

    /**
     * Initializes the onPositiveClick listener.
     */
    public void setPositiveClickListener(DialogInterface.OnClickListener listener) {
        this.positiveClickListener = listener;
    }

    /**
     * Initializes the onNegativeClick listener.
     */
    public void setNegativeClickListener(DialogInterface.OnClickListener listener) {
        this.negativeClickListener = listener;
    }

    /**
     * Retrieves the user's input in the EditText field.
     */
    public String getDescription() {
        return input.getText().toString();
    }

    /**
     * Set the title for the dialog box.
     */
    public void setDialogTitle(String title) {this.title = title;}
}
