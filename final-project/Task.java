package edu.bloomu.finalproject;

/**
 * Class to create a new Task for Goal.
 *
 * @author Jessica Ruehle
 */
public class Task {

    String key; // Firebase key value for Task child
    String description; // task description
    boolean completed; // state of task

    /**
     * Default constructor for Firebase.
     */
    public Task(){
        completed = false;
    }

    /**
     * Constructor to create a new Task.
     */
    public Task(String description) {
        this.description = description;
        completed = false;
    }

    /**
     * Setter for task description
     */
    public void setDescription(String description) {this.description = description;}

    /**
     * Getter for task description.
     */
    public String getDescription() {return this.description;}

    /**
     * Setter for task completion.
     */
    public void setCompleted(boolean completed) {this.completed = completed;}

    /**
     * Getter for task completion.
     */
    public boolean getCompleted() {return this.completed;}

    public void setKey(String key) {this.key = key;}

    public String getKey() {return key;}

}
