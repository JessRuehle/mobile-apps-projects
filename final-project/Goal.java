package edu.bloomu.finalproject;

import java.util.ArrayList;

/**
 * Class to create a new Goal for a Hobby.
 *
 * @author Jessica Ruehle
 */
public class Goal {

    private String key; // Firebase key value for Goal child
    private String description; // Goal description
    private ArrayList<Task> tasks = new ArrayList<>();; // list of tasks to complete goal
    private boolean completed; // the completed state of the goal

    /**
     * Required empty constructor for Firebase
     */
    public Goal() {completed = false;}

    /**
     * Constructor to create a new Goal given a description for it. Default state of
     * goal is incomplete.
     */
    public Goal(String description) {
        this.description = description;
        completed = false;
    }

    /**
     * Getter to retrieve description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter to write description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter to retrieve the completed state.
     */
    public boolean getCompleted() {
        return completed;
    }

    /**
     * Setter to write the completion state.
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    /**
     * Adds a task to the list of tasks.
     */
    public void addTask(Task task) {tasks.add(task);}

    /**
     * Getter to return the ArrayList<Task> of Tasks;
     */
    public ArrayList<Task> getTaskList() {return tasks;}

    /**
     * Setter to write the key value.
     */
    public void setKey(String key) {this.key = key;}

    /**
     * Getter to retrieve the key value.
     */
    public String getKey() {return key;}

}

