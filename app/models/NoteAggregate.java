package models;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Created with IntelliJ IDEA.
 * User: christophe
 * Date: 10/02/12
 * Time: 12:22
 */
public class NoteAggregate {
   
    public Topic topic;
    public Difficulty difficulty;
    public double average;

    public NoteAggregate(double average, Topic topic, Difficulty difficulty) {
        this.topic = topic;
        this.difficulty = difficulty;
        this.average = average;
    }
}
