package models;

import play.db.jpa.JPA;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: AREVEL
 * Date: 07/02/12
 * Time: 10:48
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Interview extends Model {

    public static final int NB_QUESTIONS_PER_TOPIC = 5;
    
    public String candidateName;
    public String candidateFirstName;
    public Calendar interviewDate;
    @ManyToOne
    public User examiner;
    public String examinerComment;

    @OneToMany(cascade = javax.persistence.CascadeType.ALL, mappedBy = "interview")
    @OrderBy("id ASC")
    public List<InterviewTopic> topics;

    @OneToMany(cascade = javax.persistence.CascadeType.ALL, mappedBy = "interview")
    @OrderBy("index ASC")
    public List<InterviewQuestion> questions;

    public Interview(){

    }

    public Interview(String candidateName, String candidateFirstName, Calendar interviewDate, User examiner){
        this.candidateName = candidateName;
        this.candidateFirstName = candidateFirstName;
        this.interviewDate = interviewDate;
        this.examiner = examiner;
    }
    
    public List<Question> findUnusedQuestionByTopicAndDifficulty(Topic topic, int difficulty){
        String queryStr = "select q from Question q where q.difficulty = " + difficulty + " and q.topic.id = " + topic.id +
                " and q.id not in (select qi.question.id from InterviewQuestion qi where qi.interview.id = " + this.id + ")";
        Query query = JPA.em().createQuery(queryStr);
        return query.getResultList();
    }
    
    public int getNbQuestions(){
        return topics.size() * NB_QUESTIONS_PER_TOPIC;
    }
}
