package models;

import play.db.jpa.Model;

import javax.persistence.Column;
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
public class InterviewQuestion extends Model {

    @Column(name = "INDEX_QUESTION")
    public int index;
    public int mark;
    @ManyToOne
    public Question question;
    @ManyToOne
    public Interview interview;

    public InterviewQuestion(int mark, Question question, Interview interview){
        this.mark = mark;
        this.question = question;
        this.interview = interview;
    }

    public InterviewQuestion(){

    }

    public static InterviewQuestion getInterviewQuestion(Long interviewId, int questionIndex){
        JPAQuery jpaQuery = find("interview.id = ? and index = ?" , interviewId, questionIndex);
        return jpaQuery.first();
    }

    public static InterviewQuestion getInterviewQuestionPassed(Long interviewId, int questionIndex){
        JPAQuery jpaQuery = find("interview.id = ?", interviewId, questionIndex);
        return jpaQuery.first();
    }
}
