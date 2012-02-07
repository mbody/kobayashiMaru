package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by IntelliJ IDEA.
 * User: AREVEL
 * Date: 07/02/12
 * Time: 10:59
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Answer extends Model {
    
    public int mark;
    public String comment;
    @ManyToOne
    public Question question;
    @ManyToOne
    public Interview interview;

    public  Answer (int mark, String comment, Question question, Interview interview){
        this.mark = mark;
        this.comment = comment;
        this.question = question;
        this.interview = interview;
    }
}
