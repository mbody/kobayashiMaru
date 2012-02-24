package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by IntelliJ IDEA.
 * User: AREVEL
 * Date: 24/02/12
 * Time: 11:21
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class InterviewQuestionPass extends Model{

    @ManyToOne
    public Question question;
    @ManyToOne
    public Interview interview;

    public InterviewQuestionPass(int mark, Question question, Interview interview){
        this.question = question;
        this.interview = interview;
    }

    public InterviewQuestionPass(){

    }
}
