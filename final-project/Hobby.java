package edu.bloomu.finalproject;

/**
 * Class to create a new Hobby.
 *
 * @author Jessica Ruehle
 */
public class Hobby {
    private String name; // hobby name

    private String key; // hobby key

    /**
     * Default constructor required for Firebase.
     */
    public Hobby() {}

    /**
     * Constructor to create a new Hobby given its name and description
     */
    public Hobby(String name) {
        this.name = name;
    }

    /**
     * Getter to retrieve Hobby name.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for Hobby name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for Hobby key.
     */
    public String getKey() {return key;}

    /**
     * Setter for Hobby key.
     */
    public void setKey(String key) {this.key = key;}
}
