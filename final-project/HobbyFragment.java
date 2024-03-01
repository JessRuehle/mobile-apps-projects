package edu.bloomu.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * References the Firebase database at the child node for this hobby. Data is collected
 * from the database and used to populate all the elements outlined for the fragment.
 * Event handling for fragment is implemented and interacts with the database to update
 * and retrieve information as necessary.
 *
 * KNOWN BUGS:
 *
 * 1. If all tasks are completed and user adds a new task, the parent goal's completed
 * state is not changed back to 'false'.
 *
 * @author Jessica Ruehle
 */
public class HobbyFragment extends Fragment {

    View view; // xml view
    String hobbyName; // name of the hobby being worked in
    DatabaseReference hobbyRef; // reference to the current hobby in the database
    ArrayList<Goal> goals = new ArrayList<>(); // list of goal objects pulled from database
    ArrayList<Task> tasks = new ArrayList<>(); // list of tasks for currently selected goal
    ListView goalsListView; // listview that hosts the goals
    ListView tasksListView; // listview that hosts the selected goal's tasks
    GoalAdapter goalAdapter; // adapter to populate the goalsListView
    TaskAdapter tasksAdapter; // adapter to populate the tasksListView
    Goal lastSelectedGoal; // user must select a goal to index

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // initialize the view and get the hobbyKey from the bundle
        view = inflater.inflate(R.layout.hobby_fragment, container, false);
        String hobbyKey = requireArguments().getString("HOBBY");

        // get a reference to the database at the location of the key provided by the
        // bundle
        FirebaseApp.initializeApp(getContext());
        hobbyRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("hobbies")
                .child(hobbyKey);

        // initialize the listview for the list of goals as well as its adapter. attach
        // the adapter to this view
        goalsListView = view.findViewById(R.id.goals_list);
        goalAdapter = new GoalAdapter(getContext(), goals);
        goalsListView.setAdapter(goalAdapter);

        // initialize the listview for the list of tasks as well as its adapter. attach
        // the adapter to this view
        tasksListView = view.findViewById(R.id.tasks_list);
        tasksAdapter = new TaskAdapter(getContext(), tasks);
        tasksListView.setAdapter(tasksAdapter);

        TextView goalTitle = view.findViewById(R.id.goals_list_title);
        TextView taskTitle = view.findViewById(R.id.tasks_list_title);
        goalTitle.setText(R.string.left_title);
        taskTitle.setText(R.string.right_title);

        // initialize the button elements to add a new goal or task
        Button addGoal = view.findViewById(R.id.add_goal);
        Button addTask = view.findViewById(R.id.add_task);
        addGoal.setText(R.string.add_goal_project);
        addTask.setText(R.string.add_task);

        // onDataChange event handler detects changes in the database and is called
        // everytime a change is made. by initializing the 'goals' and 'tasks'
        // arraylists with objects of their respective classes within the event
        // handler, the program is able to work with the most up-to-date version of the
        // data available
        hobbyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // get the name of the hobby
                hobbyName = dataSnapshot.child("name").getValue(String.class);

                // initialize the goals ArrayList
                goals = new ArrayList<>();

                // access goals and iterate over every goal child
                DataSnapshot goalsSnapshot = dataSnapshot.child("goals");
                for (DataSnapshot goalSnapshot : goalsSnapshot.getChildren()) {

                    // create new goal object and retrieve data from database to
                    // initialize it
                    Goal goal = new Goal();

                    // get the goal's key
                    goal.setKey(goalSnapshot.getKey());

                    // check if the description is null and if not, retrieve it
                    if (goalSnapshot.child("description")
                            .getValue(String.class) != null) {

                        goal.setDescription(goalSnapshot
                                .child("description")
                                .getValue(String.class));
                    }

                    // check if the completed status is null and if not, retrieve it
                    if (goalSnapshot.child("completed")
                            .getValue(Boolean.class) != null) {
                        goal.setCompleted(goalSnapshot
                                .child("completed")
                                .getValue(Boolean.class));
                    }

                    // access tasks and iterate over every task child
                    DataSnapshot tasksSnapshot = goalSnapshot.child("tasks");
                    for (DataSnapshot taskSnapshot : tasksSnapshot.getChildren()) {

                        // create a new task object and retrieve data from database to
                        // initialize it
                        Task task = new Task();

                        // get the task's key
                        task.setKey(taskSnapshot.getKey());

                        // check if the description is null and if not, retrieve it
                        if (taskSnapshot.child("description")
                                .getValue(String.class) != null) {

                            task.setDescription(taskSnapshot
                                    .child("description")
                                    .getValue(String.class));
                        }

                        // check if the completed status is null and if not, retrieve it
                        if (taskSnapshot.child("completed")
                                .getValue(Boolean.class) != null) {

                            task.setCompleted(taskSnapshot
                                    .child("completed")
                                    .getValue(Boolean.class));
                        }

                        // add the task to the goal object
                        goal.addTask(task);
                    }

                    // add the goal and its key to the map of goals
                    goals.add(goal);
                }

                // update the list of goals
                updateGoals();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // event handler for click events for the goals list
        goalsListView.setOnItemClickListener((adapterView, view, position, id) -> {

            // get the goal the user selected
            lastSelectedGoal = goalAdapter.getItem(position);
            updateTasks(lastSelectedGoal.getTaskList());
        });

        // event handler for long press events for the goals list
        goalsListView.setOnItemLongClickListener((adapterView, view, position, id) -> {

            // check if the list of goals is empty
            if (goals.isEmpty()) return false;

            // get the task selected and call delete task to remove it
            lastSelectedGoal = goalAdapter.getItem(position);

            deleteGoal();
            return true;
        });

        // event handler for click events for the tasks list
        tasksListView.setOnItemClickListener((adapterView, view, position, id) -> {

            // check if the user selected a goal prior to a task and inform them if
            // they didn't
            if (lastSelectedGoal == null) {
                Toast.makeText(getContext(), R.string.select_goal_edit,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // since there is a default listview item for when there are no tasks yet,
            // make sure to check that they didn't select that. display a toast
            // informing the user of this
            if (lastSelectedGoal.getTaskList().isEmpty()) {
                Toast.makeText(getContext(), R.string.add_task_first,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // get the task the user selected
            Task selectedTask = tasksAdapter.getItem(position);

            // get the details about the task and make necessary edits
            getDetails(selectedTask);
        });

        // event handler for long press events for the tasks list
        tasksListView.setOnItemLongClickListener((adapterView, view, position, id) -> {

            // check if there are any tasks in this goal before moving forward
            if (lastSelectedGoal.getTaskList().isEmpty()) return false;

            // get the task selected and call delete task to remove it
            Task selectedTask = tasksAdapter.getItem(position);
            deleteTask(selectedTask);
            return true;
        });

        // event handler for when user selects the addGoal button
        addGoal.setOnClickListener(e -> addGoal());

        // event handler for when user selects the addTask button
        addTask.setOnClickListener(e -> {

            // check if there are any existing goals
            if (!goals.isEmpty()) {

                // if the user didn't select a goal first, prompt them to select one
                if (lastSelectedGoal == null) {
                    Toast.makeText(getContext(), R.string.select_goal,
                            Toast.LENGTH_SHORT).show();

                } else {

                    // if they did select a goal, add a task to it
                    addTask();
                }

                // if the user doesn't have any goals yet, notify them
            } else {

                // create an alert
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                // show a message asking the user to select a hobby
                builder.setMessage(R.string.no_goals_yet)
                        .setTitle(R.string.no_goals);

                // display the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Gets the list of tasks from the selected goal and updates the adapter.
     */
    private void updateTasks(ArrayList<Task> listOfTasks) {

        // clear the adapter
        tasksAdapter.clear();

        // if the list of tasks is empty, display a dummy task as a place holder
        if (listOfTasks.isEmpty()) tasksAdapter.add(new Task("No tasks yet!"));

        // otherwise, update the adapter with tasks
        else tasksAdapter.addAll(listOfTasks);

        // notify the adapter that the dataset has changed
        tasksAdapter.notifyDataSetChanged();
    }

    /**
     * Uses the list of goals to update the adapter.
     */
    public void updateGoals() {

        // if there are no goals yet, return false and do nothing
        goalAdapter.clear();

        // if the list of goals is empty, display a dummy goal as a place holder
        if (goals.isEmpty()) goalAdapter.add(new Goal("No goals yet!"));

        // otherwise, clear the adapter and update it with new goals
        else goalAdapter.addAll(goals);

        // let the adapter know the dataset changed
        goalAdapter.notifyDataSetChanged();
    }

    /**
     * Presents an AlertDialog that prompts the user to enter a description for their
     * new goal or project. Upon selecting the 'Add' button, a new goal is pushed to
     * the database and initialized with provided data.
     */
    private void addGoal() {

        // create a new instance of the dialog and initialize its elements
        AddDialogFragment dialog = new AddDialogFragment();

        // set the title for the dialog box
        dialog.setDialogTitle(getString(R.string.new_goal));

        // Set the positive and negative click listeners
        dialog.setPositiveClickListener((dialogInterface, i) -> {

            // get the input from the field on the screen
            String description = dialog.getDescription();

            // push a new key to identify the goal
            String newKey = hobbyRef.child("goals").push().getKey();

            // create a new goal object and add its data
            Goal goal = new Goal();
            goal.setKey(newKey);
            goal.setDescription(description);
            goal.setCompleted(false);

            // push the new object to the database so it can serialize it
            hobbyRef.child("goals").child(newKey).setValue(goal);
        });

        // if the user hits cancel, dismiss the dialog
        dialog.setNegativeClickListener((dialogInterface, i) ->
                dialogInterface.dismiss());

        // show the dialog
        dialog.show(getParentFragmentManager(), "add_hobby");
    }

    /**
     * Presents an AlertDialog that prompts the user to enter a description for their
     * new task. Upon selecting the 'Add' button, a new task is pushed to the database
     * and initialized with provided data.
     */
    private void addTask() {

        // create a new instance of the dialog and initialize its elements
        AddDialogFragment dialog = new AddDialogFragment();

        // set the dialog's title
        dialog.setDialogTitle(getString(R.string.new_task));

        // Set the positive and negative click listeners
        dialog.setPositiveClickListener((dialogInterface, i) -> {

            // get the input from the field on the screen
            String description = dialog.getDescription();

            // push a new key to identify the task
            String newKey = hobbyRef.child("goals")
                    .child(lastSelectedGoal.getKey())
                    .child("tasks").push().getKey();

            // create a new task object and add its data
            Task task = new Task();
            task.setKey(newKey);
            task.setDescription(description);
            task.setCompleted(false);

            // push the new object to the database so it can be serialized
            hobbyRef.child("goals").child(lastSelectedGoal.getKey())
                    .child("tasks").child(newKey).setValue(task);

            // add the task to the arraylist<Task> associated with this goal and update
            // the adapter
            lastSelectedGoal.addTask(task);

            // check if the goal is currently complete, since by adding a new,
            // uncompleted task the goal will now be incomplete
            if (lastSelectedGoal.getCompleted()) {
                for (Goal goal : goals)
                    if (goal.getKey() == lastSelectedGoal.getKey())
                        goal.setCompleted(false);

                hobbyRef.child("goals").child(lastSelectedGoal.getKey())
                        .child("completed").setValue(false);
            }

            updateTasks(lastSelectedGoal.getTaskList());
            updateGoals();
        });

        // if the user hits cancel, dismiss the dialog
        dialog.setNegativeClickListener((dialogInterface, i) ->
                dialogInterface.dismiss());

        // show the dialog
        dialog.show(getParentFragmentManager(), "add_task");
    }

    /**
     * Navigates into the database, removes the given Goal, and updates the ListView on
     * the screen.
     */
    private void deleteGoal() {

        // create an alert
        AlertDialog.Builder builder = new AlertDialog.Builder(HobbyFragment.this.getActivity());

        // show a message asking the user to confirm deletion
        builder.setTitle(R.string.delete_goal)

                // set the positive button listener
                .setPositiveButton(R.string.confirm, (dialog, id1) -> {

                    // get the selected goal's key
                    String goalKey = lastSelectedGoal.getKey();

                    // create a reference to the goal in the database
                    DatabaseReference goalRef = hobbyRef
                            .child("goals")
                            .child(goalKey);

                    // remove the child node associated with the key and return
                    goalRef.removeValue();
                    updateGoals();
                    tasksAdapter.clear();
                    tasksAdapter.notifyDataSetChanged();
                })

                // if they select cancel, dismiss the dialog
                .setNegativeButton(R.string.cancel, (dialog, id2) -> dialog.dismiss());

        // create the dialog and show it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Navigates into the database, removes the given Task, and updates the ListView on
     * the screen.
     */
    public void deleteTask(Task task) {

        // create an alert
        AlertDialog.Builder builder = new AlertDialog.Builder(HobbyFragment.this.getActivity());

        // show a message asking the user to confirm deletion
        builder.setTitle(R.string.delete_task)

                // set the positive button listener
                .setPositiveButton(R.string.confirm, (dialog, id1) -> {

                    // get the list of tasks correlated with the selected goal
                    ArrayList<Task> tasks = lastSelectedGoal.getTaskList();

                    // iterate over tasks to find correct one
                    for (Task listTask : tasks) {
                        if (task.getKey() == listTask.getKey()) {

                            // create a reference to the task in the database
                            DatabaseReference taskRef = hobbyRef
                                    .child("goals")
                                    .child(lastSelectedGoal.getKey())
                                    .child("tasks")
                                    .child(listTask.getKey());

                            // remove the child node associated with the key and return
                            taskRef.removeValue();

                            // update the arraylist<task> and pass it to the update
                            // tasks method to update the adapter
                            tasks.remove(listTask);
                            updateTasks(tasks);

                            return;
                        }
                    }
                })

                // if they select cancel, dismiss the dialog
                .setNegativeButton(R.string.cancel, (dialog, id2) -> dialog.dismiss());

        // create the dialog and show it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Retrieves details about the task and makes necessary changes if the user chooses
     * to update any of the tasks attributes.
     */
    private void getDetails(Task selectedTask) {

        // create a new instance of the dialog and initialize its elements
        TaskDetailsDialog dialog = new TaskDetailsDialog();
        dialog.setCompletedStatus(selectedTask.getCompleted());

        // set the positive click listener
        dialog.setTaskPositiveClickListener((dialogInterface, i) ->
        {

            // find the matching task from the arraylist and update its completed value
            // although this will happen after onDataChanged is called upon editing the
            // database, it is necessary for checks that must happen first
            for (Task listTask : tasks) {
                if (listTask.getKey() == selectedTask.getKey()) {

                    // update the arraylist item
                    listTask.setCompleted(dialog.getCompletedStatus());

                    // update the database value
                    hobbyRef.child("goals").child(lastSelectedGoal.getKey())
                            .child("tasks").child(listTask.getKey())
                            .child("completed").setValue(listTask.getCompleted());
                }
            }

            // if the user set the task to false but the goal is already complete,
            // set the goal's complete state to false, update the database, and
            // update the goals list
            if (!dialog.getCompletedStatus() && lastSelectedGoal.getCompleted()) {
                for (Goal goal : goals) {
                    if (goal.getKey() == lastSelectedGoal.getKey()) {
                        goal.setCompleted(false);
                    }
                }
                hobbyRef.child("goals").child(lastSelectedGoal.getKey())
                        .child("completed").setValue(false);
                goalAdapter.notifyDataSetChanged();
            }

            // notify the adapter about the change and dismiss the dialog
            tasksAdapter.notifyDataSetChanged();

            // iterate over all the tasks if one is incomplete, return
            for (Task task : tasks) if (!task.getCompleted()) return;

            // create an alert
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            // show a message asking the user to confirm completion
            builder.setTitle(R.string.complete_goal)

                    // explain to the user that completing their task will complete their goal
                    .setMessage(R.string.are_you_sure)

                    // set the positive button listener
                    .setPositiveButton(R.string.confirm, (dialog1, id1) -> {

                        // set the task and its parent goal's completed value to true in
                        // the database
                        hobbyRef.child("goals").child(lastSelectedGoal.getKey())
                                .child("completed").setValue(true);

                        // display toast
                        Toast.makeText(getContext(), R.string.congratulations,
                                Toast.LENGTH_SHORT).show();
                        dialog1.dismiss();
                    })

                    // if they select cancel, mark the task as incomplete and dismiss the
                    // dialog
                    .setNegativeButton(R.string.cancel, (dialog1, id2) -> {
                        for (Task task : tasks) {
                            if (selectedTask.getKey() == task.getKey()) {
                                task.setCompleted(false);
                                // update the database values
                                hobbyRef.child("goals")
                                        .child(lastSelectedGoal.getKey())
                                        .child("tasks")
                                        .child(task.getKey())
                                        .child("completed")
                                        .setValue(false);
                            }
                        }
                        tasksAdapter.notifyDataSetChanged();
                        dialog1.dismiss();
                    });

            // create the dialog and show it
            AlertDialog dialog1 = builder.create();
            dialog1.show();

            dialog.dismiss();
        });

        // set the negative click listener to just dismiss the dialog
        dialog.setTaskNegativeClickListener((dialogInterface, i) -> dialog.dismiss());

        // display the dialog fragment
        dialog.show(getParentFragmentManager(), "task_details");
    }
}