package heritagewalk.com.heritagewalk.models;

/**
 * Created by mini_ on 2017-11-27.
 */

public class Achievement {

    private String name;
    private String description;
    private int progress;
    private int rating;

    public Achievement(String name) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
