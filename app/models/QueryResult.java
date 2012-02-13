package models;

/**
 * Created with IntelliJ IDEA.
 * User: christophe
 * Date: 13/02/12
 * Time: 11:44
 */
public class QueryResult {
    public Topic topic;
    public Difficulty difficulty;
    public long countOfNote;

    public QueryResult() {
    }

    public QueryResult(long countOfNote, Topic topic, Difficulty difficulty) {
        this.topic = topic;
        this.difficulty = difficulty;
        this.countOfNote = countOfNote;
    }
}
